package org.metalisx.common.async;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Asynchronous;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

/**
 * You can not reutrn Futures because if the caller calls the get method on the
 * future it will block and it will become synchronous.
 * 
 * THIS ONLY POSTPONE THE ERROR, IT DOES NOT FIX IT. The DependsOn annotation is
 * added because the test throws an error when it calls the deregister method of
 * the JobManager because the application server is already shutting down the
 * JobManager singleton.
 * 
 * @author Stefan.Oude.Nijhuis
 *
 */
@Stateless
@LocalBean
@DependsOn({ "JobManager" })
public class JobProcessor {

	private static final Logger LOGGER = Logger.getLogger(JobProcessor.class);

	@EJB
	JobManager jobManager;

	private Date dateStarted;

	@PostConstruct
	public void postConstruct() {
		dateStarted = new Date();
		LOGGER.info("JobProcessor started : date started = " + dateStarted);
	}

	@Asynchronous
	public void execute(Job job) {
		if (job.getDelay() > 0) {
			try {
				Thread.sleep(job.getDelay());
			} catch (InterruptedException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		jobManager.dequeue(job);
	}

	@PreDestroy
	public void preDestroy() {
		LOGGER.info("JobProcessor destroyed : date started = " + dateStarted);
	}

}
