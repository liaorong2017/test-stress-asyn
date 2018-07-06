package org.raje.test.tcp;

import java.net.BindException;
import java.net.ConnectException;
import java.util.concurrent.Semaphore;

import javax.annotation.Resource;

import org.apache.http.nio.reactor.SessionRequest;
import org.apache.http.nio.reactor.SessionRequestCallback;
import org.raje.test.common.ConnectionResources;
import org.raje.test.common.Result;
import org.raje.test.monitor.Monitor;
import org.springframework.stereotype.Component;

@Component
public class DefaultSessionRequestCallback implements SessionRequestCallback {

	@Resource
	private ConnectionResources connectionResources;

	@Resource
	private Monitor monitor;
	
	
	@Resource(name = "maxCreateConns")
	private Semaphore maxCreateConns;

	@Override
	public void completed(SessionRequest request) {
		// TODO 连接成功
		//System.out.println(System.currentTimeMillis() -(long) request.getAttachment());
		maxCreateConns.release();
	}

	@Override
	public void failed(SessionRequest request) {
		if (request.getException() instanceof ConnectException && request.getException().getMessage().contains("Connection timed out")) {
			monitor(request, Result.connectTimeout);
		} else if (request.getException() instanceof ConnectException && request.getException().getMessage().contains("Connection refused")) {
			monitor(request, Result.refused);
		} else if (request.getException() instanceof BindException) {
			System.out.println(request.getException().getMessage());
			monitor(request, Result.bindException);
		} else {
			request.getException().printStackTrace();
		}
	}

	@Override
	public void timeout(SessionRequest request) {
		monitor(request, Result.connectTimeout);
	}

	@Override
	public void cancelled(SessionRequest request) {
		new RuntimeException("cancelled").printStackTrace();
		monitor(request, Result.cancelled);

	}

	private void monitor(SessionRequest request, Result res) {
		connectionResources.release();
		maxCreateConns.release();
		long start = (long) request.getAttachment();
		monitor.log(start, res);
	}

}
