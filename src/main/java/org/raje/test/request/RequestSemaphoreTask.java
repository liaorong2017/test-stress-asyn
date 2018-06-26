package org.raje.test.request;

import java.util.concurrent.Semaphore;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

@Component
public class RequestSemaphoreTask implements Runnable {

	@Resource
	private Semaphore staySenderRequest;

	@Override
	public void run() {
		staySenderRequest.release();
		int availablePermits = staySenderRequest.availablePermits();
		if (availablePermits > 1000)
			System.out.println(availablePermits);
	}

}
