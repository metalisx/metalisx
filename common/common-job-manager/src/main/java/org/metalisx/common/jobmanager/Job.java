package org.metalisx.common.jobmanager;

public class Job {

	private String key;
	private String message;
	private int delay;

	public Job(String key, String message, int delay) {
		this.key = key;
		this.message = message;
		this.delay = delay;
	}

	public String getKey() {
		return key;
	}

	public String getMessage() {
		return message;
	}

	public int getDelay() {
		return delay;
	}

}
