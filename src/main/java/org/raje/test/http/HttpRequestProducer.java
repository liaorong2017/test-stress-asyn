package org.raje.test.http;

import javax.annotation.Resource;

import org.raje.test.request.RequestContext;
import org.raje.test.request.RequestProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HttpRequestProducer implements RequestProducer {

	@Value("${http.body:}")
	private String body;

	@Resource
	private HttpAsynCallBack callBack;

	@Override
	public RequestContext producerRequest() {
		return new RequestContext(body.getBytes(), callBack);
	}

}
