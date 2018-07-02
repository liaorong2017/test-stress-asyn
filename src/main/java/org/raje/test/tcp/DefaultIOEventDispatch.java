package org.raje.test.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOSession;
import org.raje.test.common.ConnectionResources;
import org.raje.test.common.Result;
import org.raje.test.monitor.Monitor;
import org.springframework.stereotype.Component;

@Component
public class DefaultIOEventDispatch implements IOEventDispatch {
	@Resource
	private ConnectionResources connectionResources;

	@Resource
	private Monitor monitor;

	@Resource
	private SimpleIOSessionPool pool;

	

	// TODO Auto-generated method stub
	private StringBuilder req = new StringBuilder();

	@PostConstruct
	public void init() {
		req.append("POST /sync.do HTTP/1.1\r\n");
		req.append("Content-Length: 0\r\n");
		req.append("Connection: Keep-Alive\r\n");
		req.append("Host: 10.12.142.248:19105\r\n\r\n");
	}

	@Override
	public void connected(IOSession session) {
		session.setEventMask(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
	}

	@Override
	public void inputReady(IOSession session) {
		try {
			ByteBuffer fixedRes = ByteBuffer.allocate(123);
			session.channel().read(fixedRes);
			long start = (long) session.getAttribute(IOSession.ATTACHMENT_KEY);
			monitor.log(start, Result.SUCC);
			session.clearEvent(SelectionKey.OP_READ);
			pool.release(session);
			connectionResources.release();
		} catch (IOException e) {
			session.close();
			long start = (long) session.getAttribute(IOSession.ATTACHMENT_KEY);
			monitor.log(start, Result.connectionClosed);
		}
	}

	@Override
	public void outputReady(IOSession session) {
		try {
			session.channel().write(ByteBuffer.wrap(req.toString().getBytes()));
			session.clearEvent(SelectionKey.OP_WRITE);
		} catch (IOException e) {
			session.close();
			long start = (long) session.getAttribute(IOSession.ATTACHMENT_KEY);
			monitor.log(start, Result.connectionClosed);
		}

	}

	@Override
	public void timeout(IOSession session) {
		session.close();
		long start = (long) session.getAttribute(IOSession.ATTACHMENT_KEY);
		monitor.log(start, Result.readTimeout);
	}

	@Override
	public void disconnected(IOSession session) {
		connectionResources.release();
	}

}
