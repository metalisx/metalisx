package org.metalisx.common.jobmanager;

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
import javax.inject.Inject;
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
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.metalisx.common.audit.AuditData;
import org.metalisx.common.audit.AuditManager;
import org.metalisx.common.audit.StringAuditData;
import org.metalisx.common.audit.StringAuditManager;

@RunWith(Arquillian.class)
public class JobManagerTest {

	private static final Logger LOGGER = Logger.getLogger(JobManagerTest.class.getName());

	@EJB
	private JobManager jobManager;

	@Inject
	private StringAuditManager auditManager;

	public JobManagerTest() {
	}

	@Deployment
	public static Archive<?> createTestArchive() {
		JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class, "testJobManager.jar")
				.addClasses(
						// Main
						AuditData.class, AuditManager.class, StringAuditData.class, StringAuditManager.class, Job.class,
						JobProcessor.class, JobManager.class,
						// Test
						JobManagerTest.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

		WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "testJobManager.war").addAsLibrary(javaArchive)
				.addAsResource("server.log4j.properties", "log4j.properties")
				.addAsWebInfResource("weblogic-ejb-jar.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

		return webArchive;
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
	 * asynchronous. First a long running job(with a sleep of two seconds) is
	 * queued then an immediate job is queued. The immediate job should be
	 * finished before the first job.
	 * 
	 * In JBoss the jobs are running in parallel which makes job 2 ending before
	 * job 1 ends. In WebLogic the jobs are not running in parallel, job 2 keeps
	 * waiting until job 1 ends. However if the delay is increased to 6 seconds
	 * then WebLogic is also running the jobs in parallel. This happens because
	 * of the strategy algorithm WebLogic uses to determine when to create a new
	 * thread. This algorithm process every two seconds statistic which can
	 * result in different outcomes. For instance if the test is run multiple
	 * times on the WebLogic server it will eventually be handled in parallel
	 * due to the updated statistics.
	 * 
	 * How can we make the outcome always the same for WebLogic? For this we
	 * want to instruct WebLogic to create the beans when required. To do this
	 * we need to deploy a weblogic-ejb-jar.xml descriptor file. In the
	 * descriptor file, add a work manager with the min-threads-constraint set
	 * to 2 and add weblogic-enterprise-bean for the EJB with a dispatch-policy
	 * set to the work manager. Running the test will now have always the same
	 * outcome, meaning both jobs are run in parallel.
	 * 
	 * The {@link StringAuditManager} is used to check if the jobs are run in
	 * parallel. This is based on the EJB event mechanism.
	 */
	@Test
	public void testParallel() {
		LOGGER.info("Test asynchronous start and parallel exectution of jobs");
		assertNotNull("Injection failed for JobManager", jobManager);
		jobManager.reset();
		auditManager.reset();

		int numberOfJobs = 1;
		
		LOGGER.info("Queueing long running job");
		String message1 = "Wayne " + numberOfJobs;
		jobManager.enqueue(message1, 2000);

		numberOfJobs = numberOfJobs + 1;
		LOGGER.info("Queueing immediate running job");
		String message2 = "Kent " + numberOfJobs;
		jobManager.enqueue(message2, 0);

		assertAllJobsAreFinished(numberOfJobs);

		auditManager.logAuditList();
		
		ArrayList<StringAuditData> auditList = auditManager.getSortedAuditList();
		assertEquals("Total audit list entries", 4, auditList.size());
		assertEquals("Last audit entry", "Ended " + message1, auditList.get(3).getData());
	}

	@Test
	public void loadTestJobManager() {
		LOGGER.info("Load test jobs");
		assertNotNull("Injection failed for JobManager", jobManager);
		jobManager.reset();

		int numberOfJobs = 100;

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
		
		auditManager.logAuditList();
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
