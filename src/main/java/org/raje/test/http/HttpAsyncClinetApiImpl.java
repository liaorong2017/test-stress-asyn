package org.raje.test.http;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.nio.client.HttpAsyncClient;
import org.raje.test.common.ConnectionResources;
import org.raje.test.monitor.Counter;
import org.raje.test.monitor.Monitor;
import org.raje.test.request.AsyncClinetApi;
import org.springframework.stereotype.Component;

@Component
public class HttpAsyncClinetApiImpl implements AsyncClinetApi {
	@Resource
	private Counter counter;

	@Resource
	private HttpConfig httpConfig;

	@Resource
	private HttpAsyncClientPoolManager asyncHttpClientPoolManager;

	@Resource
	private ConnectionResources connections;

	@Resource
	private HttpAsynCallBack callBack;

	@Resource
	private Monitor monitor;

	private HttpAsyncClient httpAsyncClient;

	@PostConstruct
	public void init() {
		httpAsyncClient = asyncHttpClientPoolManager.getHttpAsyncClient();

	}

	public void sendRequest() {
		try {
			HttpRequestContext callBack = new HttpRequestContext(this.callBack, this.monitor, this.connections);
			HttpRequestBase httpRequest = null;
			if (httpConfig.getMethod().trim().toUpperCase().equals("POST")) {
				httpRequest = buidHttpPost(httpConfig.getBody());
			} else {
				httpRequest = buidHttpGet();
			}
			httpAsyncClient.execute(httpRequest, callBack);
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
