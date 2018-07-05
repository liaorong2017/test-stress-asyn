package org.raje.test.common;

import java.util.concurrent.Semaphore;

public class ConnectionResources extends Semaphore {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2534183326712465798L;

	public ConnectionResources(int permits) {
		super(permits);
	}

	public void reducePermits(int reduction) {
		super.reducePermits(reduction);
	}

}
