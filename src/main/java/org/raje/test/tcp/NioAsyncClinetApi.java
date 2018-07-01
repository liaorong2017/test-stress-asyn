package org.raje.test.tcp;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.IOReactorException;
import org.raje.test.common.ConnectionResources;
import org.raje.test.request.AsyncClinetApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NioAsyncClinetApi implements AsyncClinetApi {
	private static final Logger LG = LoggerFactory.getLogger(NioAsyncClinetApi.class);

	@Resource
	private ConnectionResources connectionResources;
	
	@Resource
	private DefaultSessionRequestCallback callback;

	private DefaultConnectingIOReactor ioreactor;

	@PostConstruct
	public void init() {
		IOReactorConfig ioReactorConfig = builderIOReactorConfig();
		try {
			ioreactor = new DefaultConnectingIOReactor(ioReactorConfig);
		} catch (IOReactorException e) {
			LG.error("init DefaultConnectingIOReactor error", e);
			System.exit(1);
		}
	}

	@Override
	public void sendRequest() {
		ioreactor.connect(remoteAddress, null, attachment, callback);

	}

	private IOReactorConfig builderIOReactorConfig() {
		IOReactorConfig.Builder builder = IOReactorConfig.custom();
		builder.setBacklogSize(65535);
		builder.setConnectTimeout(400);
		builder.setSoKeepAlive(false);
		builder.setSoTimeout(1000);
		builder.setIoThreadCount(Runtime.getRuntime().availableProcessors() * 2);
		builder.setTcpNoDelay(true);
		return builder.build();
	}

}
