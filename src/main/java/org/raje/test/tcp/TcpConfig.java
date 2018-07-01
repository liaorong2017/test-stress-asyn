package org.raje.test.tcp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TcpConfig {

	@Value("${tcp.host}")
	protected String host;

	@Value("${tcp.port}")
	protected int port;

	@Value("${tcp.connection.timeout:200}")
	protected int connectTimeoutMills;

	@Value("${tcp.pool.acquire.timeout:10}")
	protected int acquireTimeoutMillis;

	@Value("${tcp.read.timeout:500}")
	protected int readTimeout;

	@Value("${tcp.write.timeout:500}")
	protected int writeTimeout;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getConnectTimeoutMills() {
		return connectTimeoutMills;
	}

	public void setConnectTimeoutMills(int connectTimeoutMills) {
		this.connectTimeoutMills = connectTimeoutMills;
	}

	public int getAcquireTimeoutMillis() {
		return acquireTimeoutMillis;
	}

	public void setAcquireTimeoutMillis(int acquireTimeoutMillis) {
		this.acquireTimeoutMillis = acquireTimeoutMillis;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public int getWriteTimeout() {
		return writeTimeout;
	}

	public void setWriteTimeout(int writeTimeout) {
		this.writeTimeout = writeTimeout;
	}

}
