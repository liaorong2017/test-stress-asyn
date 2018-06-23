package org.raje.test.common;

import java.util.concurrent.Semaphore;

public class SemaphoreWithFlag {


	private Semaphore semaphore;
	private String releaseMethod = null;

	public SemaphoreWithFlag(Semaphore semaphore) {
		super();
		this.semaphore = semaphore;
	}

	public Semaphore getSemaphore() {
		return semaphore;
	}

	public void setSemaphore(Semaphore semaphore) {
		this.semaphore = semaphore;
	}

	public String getReleaseMethod() {
		return releaseMethod;
	}

	public void setReleaseMethod(String releaseMethod) {
		this.releaseMethod = releaseMethod;
	}

	public void release(String method) {
		if (releaseMethod == null) {
			//semaphore.release();
			this.releaseMethod = method;
		}
	}

}
