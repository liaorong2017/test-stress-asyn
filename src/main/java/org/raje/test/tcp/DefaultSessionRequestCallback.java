package org.raje.test.tcp;

import org.apache.http.nio.reactor.SessionRequest;
import org.apache.http.nio.reactor.SessionRequestCallback;
import org.springframework.stereotype.Component;

@Component
public class DefaultSessionRequestCallback implements SessionRequestCallback{

	@Override
	public void completed(SessionRequest request) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void failed(SessionRequest request) {
		// TODO Auto-generated method stub
		//new RuntimeException().printStackTrace();
	}

	@Override
	public void timeout(SessionRequest request) {
		// TODO Auto-generated method stub
		//new RuntimeException().printStackTrace();
	}

	@Override
	public void cancelled(SessionRequest request) {
		// TODO Auto-generated method stub
		//new RuntimeException().printStackTrace();
	}

}
