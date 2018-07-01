package org.raje.test.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOSession;
import org.springframework.stereotype.Component;

@Component
public class DefaultIOEventDispatch implements IOEventDispatch {
	// TODO Auto-generated method stub
	private String content = "GET /index.html HTTP/1.1\r\nHost: 192.168.24.128:8080\r\nConnection: Keep-Alive\r\nUser-Agent: Apache-HttpAsyncClient/4.1.3 (Java/1.8.0_91)\r\n\r\n";

	@Override
	public void connected(IOSession session) {

		try {
			session.channel().write(ByteBuffer.wrap(content.getBytes()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void inputReady(IOSession session) {
		// TODO Auto-generated method stub
		new RuntimeException("inputReady").printStackTrace();
	}

	@Override
	public void outputReady(IOSession session) {
		// TODO Auto-generated method stub
		// new RuntimeException("outputReady").printStackTrace();
	}

	@Override
	public void timeout(IOSession session) {
		// TODO Auto-generated method stub
		// new RuntimeException("timeout").printStackTrace();
	}

	@Override
	public void disconnected(IOSession session) {
		// TODO Auto-generated method stub
		new RuntimeException("disconnected").printStackTrace();
	}

}
