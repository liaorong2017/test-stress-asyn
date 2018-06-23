package org.raje.test.http;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class HttpConfig {
	private static final Logger LG = LoggerFactory.getLogger(HttpConfig.class);
	@Value("${http.url}")
	private String url;

	// 最大连接数
	@Value("${http.max.connect:200}")
	private int maxHttpConnect;

	// 请求连接超时时间
	@Value("${http.request.conn.timeout.in.ms:1000}")
	private int httpRequestConnTimeout;

	// 连接建立超时（ms）
	@Value("${http.connect.time.in.ms:3000}")
	private int httpConnTimeout;

	// 读超时（ms）
	@Value("${http.read.timeout.in.ms:5000}")
	private int httpReadTimeout;

	// keep alive
	@Value("${http.connection.keep.alive:true}")
	private boolean keepAlive = true;

	// 连接空闲时长，空闲时间超过这个时间，连接关闭
	@Value("${http.request.acquire.timeout:10}")
	private int acquireTimeout;

	@Value("${http.method:post}")
	private String method;

	@Value("${http.charset:utf-8}")
	private String httpCharset;

	@Value("${http.headers:}")
	private String headerStr;

	private List<Header> headers = new ArrayList<Header>();

	@PostConstruct
	public void init() {
		if (!StringUtils.isBlank(headerStr)) {
			String[] items = headerStr.split("\\|");
			for (String item : items) {
				String[] keys = item.split("|");
				if (keys.length == 2) {
					headers.add(new BasicHeader(keys[0], keys[1]));
				}
			}
		}
		LG.info("headers are :" + headers);
	}

	@Override
	public String toString() {
		return "HttpClientConfig{" + "maxHttpConnect=" + maxHttpConnect + ", httpConnTimeout='" + httpConnTimeout + '\''
				+ ", httpReadTimeout=" + httpReadTimeout + ", keepAlive=" + keepAlive + ", acquireTimeout="
				+ acquireTimeout + "}";
	}

	public int getMaxHttpConnect() {
		return maxHttpConnect;
	}

	public void setMaxHttpConnect(int maxHttpConnect) {
		this.maxHttpConnect = maxHttpConnect;
	}

	public int getHttpRequestConnTimeout() {
		return httpRequestConnTimeout;
	}

	public void setHttpRequestConnTimeout(int httpRequestConnTimeout) {
		this.httpRequestConnTimeout = httpRequestConnTimeout;
	}

	public int getHttpConnTimeout() {
		return httpConnTimeout;
	}

	public void setHttpConnTimeout(int httpConnTimeout) {
		this.httpConnTimeout = httpConnTimeout;
	}

	public int getHttpReadTimeout() {
		return httpReadTimeout;
	}

	public void setHttpReadTimeout(int httpReadTimeout) {
		this.httpReadTimeout = httpReadTimeout;
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	public int getAcquireTimeout() {
		return acquireTimeout;
	}

	public void setAcquireTimeout(int acquireTimeout) {
		this.acquireTimeout = acquireTimeout;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHttpCharset() {
		return httpCharset;
	}

	public void setHttpCharset(String httpCharset) {
		this.httpCharset = httpCharset;
	}

	public List<Header> getHeaders() {
		return headers;
	}

	public void setHeaders(List<Header> headers) {
		this.headers = headers;
	}

}
