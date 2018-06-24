package org.raje.test.tcp;

import java.nio.charset.Charset;

import javax.annotation.Resource;

import org.raje.test.request.RequestContext;
import org.raje.test.request.RequestProducer;
import org.springframework.stereotype.Component;

@Component
public class NettyRequestProducer implements RequestProducer {

	//@Value("${tcp.content:}")
	private String content = "GET /index.html HTTP/1.1\r\nHost: 192.168.24.128:8080\r\nConnection: Keep-Alive\r\nUser-Agent: Apache-HttpAsyncClient/4.1.3 (Java/1.8.0_91)\r\n\r\n";
	

	@Resource
	private NettyAsynCallBack callBack;

	private final Charset charset = Charset.forName("GBK");

	@Override
	public RequestContext producerRequest() {
		return new RequestContext(content.getBytes(charset), callBack);
	}

}
