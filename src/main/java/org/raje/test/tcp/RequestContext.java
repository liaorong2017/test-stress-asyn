package org.raje.test.tcp;

public class RequestContext {
	private long start;

	public RequestContext() {
		super();
		start = System.currentTimeMillis();
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

}
