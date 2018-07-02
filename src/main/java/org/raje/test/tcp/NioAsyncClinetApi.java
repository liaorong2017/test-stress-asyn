package org.raje.test.tcp;

import javax.annotation.Resource;

import org.raje.test.request.AsyncClinetApi;
import org.springframework.stereotype.Component;

@Component
public class NioAsyncClinetApi implements AsyncClinetApi {
	
	@Resource
	private SimpleIOSessionPool pool;

	@Override
	public void sendRequest() {
		pool.execute();
	}

}
