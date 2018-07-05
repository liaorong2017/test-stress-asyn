package org.raje.test.common;

public class Result {
	public static final Result SUCC = new Result(0, "0");
	public static final Result poolTimeout = new Result(1, "noToServer");
	public static final Result readTimeout = new Result(2, "read timetout");
	public static final Result connectTimeout = new Result(3, "connect timetout");
	public static final Result reset = new Result(4, "Connection reset");
	public static final Result unknow = new Result(-1, "unkonw exception");
	public static final Result writeTimeout = new Result(5, "write timetout");
	public static final Result httpStatusNoOk = new Result(6, "httpstatus not 200");
	public static final Result refused = new Result(7, "Connection refused");
	public static final Result connectionClosed = new Result(8, "Connection closed");
	public static final Result timeout = new Result(9, "timeout");
	public static final Result cancelled = new Result(10, "cancelled");
	public static final Result bindException = new Result(11, "bindException");

	private int result;
	private String info;

	public Result(int result, String info) {
		this.result = result;
		this.info = info;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return String.format("result=%s&info=%s", result, info);
	}

}
