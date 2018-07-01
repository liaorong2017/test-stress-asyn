package org.raje.test.http;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.reactor.IOReactorException;
import org.springframework.stereotype.Component;

@Component
public class HttpAsyncClientPoolManager {

	@Resource
	private HttpConfig httpConfig;

	private CloseableHttpAsyncClient httpAsyncClient;

	@PostConstruct
	public void init() throws IOReactorException {
		IOReactorConfig ioReactorConfig = builderIOReactorConfig();
		DefaultConnectingIOReactor ioreactor = new DefaultConnectingIOReactor(ioReactorConfig);
		PoolingNHttpClientConnectionManager mngr = new PoolingNHttpClientConnectionManager(ioreactor);
		mngr.setDefaultMaxPerRoute(Integer.MAX_VALUE);
		mngr.setMaxTotal(Integer.MAX_VALUE);
		httpAsyncClient = HttpAsyncClientBuilder.create().setConnectionManager(mngr).build();
		httpAsyncClient.start();
	}

	private IOReactorConfig builderIOReactorConfig() {
		IOReactorConfig.Builder builder = IOReactorConfig.custom();
		builder.setBacklogSize(65535);	
		builder.setSoTimeout(httpConfig.getHttpReadTimeout());
		builder.setConnectTimeout(httpConfig.getHttpConnTimeout());
		builder.setSoKeepAlive(false);
		builder.setSoTimeout(httpConfig.getHttpReadTimeout());
		builder.setIoThreadCount(Runtime.getRuntime().availableProcessors() * 2);
		builder.setTcpNoDelay(true);
//		builder.setRcvBufSize(16*1024*1024);
//		builder.setSndBufSize(16*1024*1024);
		return builder.build();
	}

	public HttpAsyncClient getHttpAsyncClient() {
		return httpAsyncClient;
	}

}
