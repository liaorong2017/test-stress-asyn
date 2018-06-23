package org.raje.test.http;

import java.util.concurrent.Semaphore;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.nio.client.HttpAsyncClient;
import org.raje.test.common.AsyncClinetApi;
import org.raje.test.common.RequestContext;
import org.raje.test.monitor.Monitor;
import org.springframework.stereotype.Component;

@Component
public class HttpAsyncClinetApiImpl implements AsyncClinetApi {
	@Resource
	private HttpConfig httpConfig;

	@Resource
	private HttpAsyncClientPoolManager asyncHttpClientPoolManager;

	private Semaphore semaphore;

	private HttpAsyncClient httpAsyncClient;;

	@Resource
	private Monitor monitor;

	@PostConstruct
	public void init() {
		semaphore = new Semaphore(httpConfig.getMaxHttpConnect(), true);
		httpAsyncClient = asyncHttpClientPoolManager.getHttpAsyncClient();
	}

	public void sendRequest(RequestContext context) {
		try {
			// if (semaphore.tryAcquire(httpConfig.getAcquireTimeout(),
			// TimeUnit.MILLISECONDS)) {
			context.setStartTime(System.currentTimeMillis());
			HttpRequestBase httpRequest = null;
			if (httpConfig.getMethod().trim().toUpperCase().equals("POST")) {
				httpRequest = buidHttpPost(new String(context.getReqBytes()));
			} else {
				httpRequest = buidHttpGet();
			}
			httpAsyncClient.execute(httpRequest, new HttpFutureCallback(context, monitor, semaphore));
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HttpGet buidHttpGet() {
		return new HttpGet(httpConfig.getUrl());

	}

	public HttpPost buidHttpPost(String req) throws Exception {
		HttpPost httpPost = new HttpPost(httpConfig.getUrl());
		if (!StringUtils.isBlank(req)) {
			StringEntity enity = new StringEntity(req);
			httpPost.setEntity(enity);
		}
		return httpPost;
	}
}
