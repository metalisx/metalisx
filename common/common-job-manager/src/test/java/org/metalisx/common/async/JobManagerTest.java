package org.metalisx.common.async;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ejb.EJB;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class JobManagerTest {

	private static final Logger LOGGER = Logger.getLogger(JobManagerTest.class.getName());

	@EJB
	private JobManager jobManager;

	public JobManagerTest() {
	}

	@Deployment
	public static Archive<?> createTestArchive() {
		return ShrinkWrap.create(JavaArchive.class, "testJobManager.jar")
				.addClasses(
						// Services
						Job.class, JobProcessor.class, JobManager.class,
						// Test
						JobManagerTest.class)
				.addAsResource("server.log4j.properties", "log4j.properties")
				.addAsManifestResource("META-INF/test-beans.xml", "beans.xml");
	}

	@BeforeClass
	public static void beforeClass() {
		LOGGER.info("======================= " + new Date());
	}

	@Before
	public void before() throws NamingException, MalformedObjectNameException, MBeanException,
			AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
		PropertyConfigurator.configure(JobManagerTest.class.getResource("/log4j.properties"));
	}

	/**
	 * Test to validate if the JobProcessor execute method is indeed
	 * asynchronous. First a long running job is queued then a immediate job. In
	 * the log the output of the long running should be after the output of the
	 * immediate job. This can only be validated by reading the log. The test
	 * will validate if the job manager has executed two jobs.
	 * 
	 * JBoss is running in parallel which makes job 1 ending after job 2.
	 * WebLogic is not running in parallel, job 2 is waiting until job 1 is
	 * finished. However if the delay is set to 6 seconds then WebLogic is also
	 * running in parallel, what happens here, strategy?
	 */
	@Test
	public void testJobManager() {
		assertNotNull("Injection failed for JobManager", jobManager);
		jobManager.reset();

		int numberOfJobs = 1;
		LOGGER.info("Queueing long running job");
		jobManager.enqueue("Wayne " + numberOfJobs, 2000);

		numberOfJobs = numberOfJobs + 1;
		LOGGER.info("Queueing immediate running job");
		jobManager.enqueue("Kent " + numberOfJobs, 0);

		assertAllJobsAreFinished(numberOfJobs);
	}

	@Test
	public void loadTestJobManager() {
		assertNotNull("Injection failed for JobManager", jobManager);
		jobManager.reset();

		int numberOfJobs = 1000;

		ExecutorService executorService = Executors.newFixedThreadPool(10);

		// Create list of callable's
		Collection<RegisterCallable> jobs = new ArrayList<>();
		for (int i = 1; i <= numberOfJobs; i++) {
			jobs.add(new RegisterCallable("Wayne " + i));
		}

		// Use the executor service to run the callable's asynchronous
		LOGGER.info("Start queuing jobs");
		try {
			executorService.invokeAll(jobs);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
		LOGGER.info("End queuing jobs");

		assertAllJobsAreFinished(numberOfJobs);
	}

	private void assertAllJobsAreFinished(int numberOfJobs) {
		LOGGER.info("Listen until all jobs are queued");
		while (jobManager.getTotalQueued() != numberOfJobs) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		assertEquals("Total queued", numberOfJobs, jobManager.getTotalQueued());

		LOGGER.info("Listen until all jobs are processed");
		while (jobManager.getTotalProcessed() != numberOfJobs) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		assertEquals("Total processed", numberOfJobs, jobManager.getTotalProcessed());
	}

	private class RegisterCallable implements Callable<String> {

		private String message;

		public RegisterCallable(String message) {
			this.message = message;
		}

		@Override
		public String call() {
			jobManager.enqueue(message, 1000);
			return "Done";
		}

	}

}
