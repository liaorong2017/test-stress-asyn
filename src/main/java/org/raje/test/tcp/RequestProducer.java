package org.raje.test.tcp;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class RequestProducer {

	private byte[] reqByte;

	@PostConstruct
	public void init() {
		String content = "GET /index.html HTTP/1.1\r\nHost: 192.168.24.128:8080\r\nConnection: Keep-Alive\r\nUser-Agent: Apache-HttpAsyncClient/4.1.3 (Java/1.8.0_91)\r\n\r\n";

		reqByte = content.getBytes();
	}

	public byte[] producerMessage() {
		return reqByte;
	}

}
