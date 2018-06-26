package org.raje.test.http;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.util.EntityUtils;
import org.raje.test.common.ConnectionResources;
import org.raje.test.common.Result;
import org.raje.test.monitor.Monitor;
import org.raje.test.request.PlainLoadRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestContext implements FutureCallback<HttpResponse> {
	private final Logger LG = LoggerFactory.getLogger(PlainLoadRunner.class);
	
	private long start;

	private Monitor monitor;

	private ConnectionResources semaphore;

	private HttpAsynCallBack callBack;

	public HttpRequestContext(HttpAsynCallBack callBack, Monitor monitor, ConnectionResources semaphore) {
		super();
		this.monitor = monitor;
		this.semaphore = semaphore;
		this.callBack = callBack;
		this.start = System.currentTimeMillis();
	}

	@Override
	public void completed(HttpResponse httpResponse) {
		semaphore.release();
		try {
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				monitor.log(start, Result.httpStatusNoOk);
				return;
			}
			HttpEntity responseEntity = httpResponse.getEntity();
			String content = EntityUtils.toString(responseEntity);

			Result resulte = callBack.callBack(content);
			monitor.log(start, resulte);

		} catch (ParseException e) {
			LG.error("completed error", e);
		} catch (IOException e) {
			LG.error("completed error", e);
		}

	}

	@Override
	public void failed(Exception ex) {
		semaphore.release();
		long costTime = System.currentTimeMillis() - start;
		if (ex instanceof ConnectException) {
			if (ex.getMessage().contains("Connection refused")) {
				monitor.log(start, Result.refused);
				return;
			} else if (ex.getMessage().contains("Connection timed out")) {
				monitor.log(start, Result.connectTimeout);
				return;
			}
		} else if (ex instanceof ConnectionClosedException) {
			monitor.log(start, Result.connectionClosed);
			return;
		} else if (ex instanceof SocketTimeoutException) {
			monitor.log(start, Result.readTimeout);
			return;
		} else if (ex instanceof IOException) {
			monitor.log(start, Result.connectionClosed);
			return;
		}

		new RuntimeException().printStackTrace();
		LG.error("failed error:" + costTime, ex);

	}

	@Override
	public void cancelled() {
		semaphore.release();
		new RuntimeException().printStackTrace();
	}

}
