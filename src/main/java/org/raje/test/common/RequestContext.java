package org.raje.test.common;

public class RequestContext {
	private byte[] reqBytes;
	private AsynCallBack<?> callBack;
	private long startTime;

	public RequestContext(byte[] reqBytes, AsynCallBack<?> callBack) {
		super();
		this.reqBytes = reqBytes;
		this.callBack = callBack;
		this.startTime = System.currentTimeMillis();
	}

	public byte[] getReqBytes() {
		return reqBytes;
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

}
