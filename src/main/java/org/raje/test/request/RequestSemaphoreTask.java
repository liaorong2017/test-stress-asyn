package org.raje.test.request;

import java.util.concurrent.Semaphore;

import javax.annotation.Resource;

import org.raje.test.common.ConnectionResources;
import org.springframework.stereotype.Component;

@Component
public class RequestSemaphoreTask implements Runnable {

	@Resource
	private Semaphore staySenderRequest;

	@Resource
	private ConnectionResources connections;

	@Override
	public void run() {
		staySenderRequest.release();
	}

}
