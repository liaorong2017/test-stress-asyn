package org.raje.test.tcp;

import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOSession;
import org.springframework.stereotype.Component;

@Component
public class DefaultIOEventDispatch implements IOEventDispatch {

	@Override
	public void connected(IOSession session) {
		// TODO Auto-generated method stub
		session.channel().write(src)
		
	}

	@Override
	public void inputReady(IOSession session) {
		// TODO Auto-generated method stub
		new RuntimeException("inputReady").printStackTrace();
	}

	@Override
	public void outputReady(IOSession session) {
		// TODO Auto-generated method stub
		//new RuntimeException("outputReady").printStackTrace();
	}

	@Override
	public void timeout(IOSession session) {
		// TODO Auto-generated method stub
		//new RuntimeException("timeout").printStackTrace();
	}

	@Override
	public void disconnected(IOSession session) {
		// TODO Auto-generated method stub
		new RuntimeException("disconnected").printStackTrace();
	}

}
