package org.raje.test.request;

import java.util.concurrent.BlockingQueue;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

@Component
public class RequestContextTask implements Runnable {
	@Resource
	private RequestProducer producer;

	@Resource(name="reqListBlocking")
	private BlockingQueue<RequestContext> reqQueue;

	@Override
	public void run() {
		reqQueue.add(producer.producerRequest());
	}

}
