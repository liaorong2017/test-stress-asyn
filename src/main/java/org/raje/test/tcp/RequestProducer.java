package org.raje.test.tcp;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class RequestProducer {

	private byte[] reqByte;

	@PostConstruct
	public void init() {
		StringBuilder req = new StringBuilder();
		req.append("POST /sync.do HTTP/1.1\r\n");
		req.append("Content-Length: 0\r\n");
		req.append("Host: 10.12.142.248:19105\r\n\r\n");
		reqByte = req.toString().getBytes();
	}

	public byte[] producerMessage() {
		return reqByte;
	}

}
