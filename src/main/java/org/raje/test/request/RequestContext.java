package org.raje.test.request;

public class RequestContext {
	private RequestProducer<?> producer;
	private AsynCallBack<?> callBack;
	private long startTime;

	public RequestContext(RequestProducer<?> producer, AsynCallBack<?> callBack) {
		super();
		this.producer = producer;
		this.callBack = callBack;
		this.startTime = System.currentTimeMillis();
	}


	public AsynCallBack<?> getCallBack() {
		return callBack;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}


	public RequestProducer<?> getProducer() {
		return producer;
	}


	public void setProducer(RequestProducer<?> producer) {
		this.producer = producer;
	}
	
	

}
