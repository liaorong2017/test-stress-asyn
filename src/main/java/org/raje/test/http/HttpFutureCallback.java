package org.raje.test.http;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.Semaphore;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.util.EntityUtils;
import org.raje.test.common.Result;
import org.raje.test.monitor.Monitor;
import org.raje.test.request.RequestContext;
import org.raje.test.request.RequestLoadRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpFutureCallback implements FutureCallback<HttpResponse> {
	private final Logger LG = LoggerFactory.getLogger(RequestLoadRunner.class);
	private RequestContext context;

	private Monitor monitor;

	private Semaphore semaphore;

	private HttpAsynCallBack callBack;

	public HttpFutureCallback(RequestContext context, Monitor monitor, Semaphore semaphore) {
		super();
		this.context = context;
		this.monitor = monitor;
		this.semaphore = semaphore;
		this.callBack = (HttpAsynCallBack) context.getCallBack();
	}

	@Override
	public void completed(HttpResponse httpResponse) {
		semaphore.release();
		try {
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				monitor.log(context.getStartTime(), Result.httpStatusNoOk);
				return;
			}
			HttpEntity responseEntity = httpResponse.getEntity();
			String content = EntityUtils.toString(responseEntity);

			Result resulte = callBack.callBack(content);
			monitor.log(context.getStartTime(), resulte);

		} catch (ParseException e) {
			LG.error("completed error", e);
		} catch (IOException e) {
			LG.error("completed error", e);
		}

	}

	@Override
	public void failed(Exception ex) {
		semaphore.release();
		long costTime = System.currentTimeMillis() - context.getStartTime();
		if (ex instanceof ConnectException) {
			if (ex.getMessage().contains("Connection refused")) {
				monitor.log(context.getStartTime(), Result.refused);
				return;
			} else if (ex.getMessage().contains("Connection timed out")) {
				monitor.log(context.getStartTime(), Result.connectTimeout);
				return;
			}
		} else if (ex instanceof ConnectionClosedException) {
			monitor.log(context.getStartTime(), Result.connectionClosed);
			return;
		} else if (ex instanceof SocketTimeoutException) {
			monitor.log(context.getStartTime(), Result.readTimeout);
			return;
		}else if(ex instanceof IOException) {
			monitor.log(context.getStartTime(), Result.connectionClosed);
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
