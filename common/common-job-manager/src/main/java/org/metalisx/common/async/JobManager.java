package org.metalisx.common.async;

import java.util.Date;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.apache.log4j.Logger;

@Singleton
@LocalBean
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class JobManager {

	private static final Logger LOGGER = Logger.getLogger(JobManager.class);

	private HashMap<String, Job> queue = new HashMap<>();

	private Date dateStarted;

	private int totalQueued = 0;
	private int totalProcessed = 0;

	@EJB
	private JobProcessor jobProcessor;

	@PostConstruct
	public void postConstruct() {
		dateStarted = new Date();
		LOGGER.info("JobManager started : date started = " + dateStarted);
	}

	@PreDestroy
	public void preDestroy() {
		LOGGER.info("JobManager destroyed : date started = " + dateStarted);
	}

	@Lock(LockType.READ)
	public Date getDateStarted() {
		return dateStarted;
	}

	@Lock(LockType.WRITE)
	public void reset() {
		queue = new HashMap<>();
		totalQueued = 0;
		totalProcessed = 0;
	}
	
	@Lock(LockType.WRITE)
	public void enqueue(String message, int delay) {
		String key = String.valueOf(System.currentTimeMillis()) + "-" + (1 + queue.size() + totalProcessed);
		Job job = new Job(key, message, delay);
		queue.put(key, job);
		jobProcessor.execute(job);
		totalQueued = totalQueued + 1;
		LOGGER.info("message = " + message + ", key = " + key + " : ADDED. Total message queued = " + totalQueued);
	}

	@Lock(LockType.WRITE)
	public void dequeue(Job job) {
		queue.remove(job.getKey());
		totalProcessed = totalProcessed + 1;
		LOGGER.info("message = " + job.getMessage() + ", key = " + job.getKey() + " : REMOVED. Total jobs processed = "
				+ totalProcessed + ". Remaining jobs in queue = " + queue.size());
	}

	@Lock(LockType.READ)
	public int getTotalQueued() {
		return totalQueued;
	}

	@Lock(LockType.READ)
	public int getTotalProcessed() {
		return totalProcessed;
	}

}
