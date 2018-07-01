package org.raje.test.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.concurrent.BlockingQueue;

import javax.annotation.Resource;

import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOSession;
import org.raje.test.common.ConnectionResources;
import org.raje.test.common.Result;
import org.raje.test.monitor.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultIOEventDispatch implements IOEventDispatch {
	private static final Logger LG = LoggerFactory.getLogger(DefaultIOEventDispatch.class);
	@Resource
	private ConnectionResources connectionResources;

	@Resource
	private Monitor monitor;

	@Resource
	private BlockingQueue<IOSession> ioSessions;

	// TODO Auto-generated method stub
	private String content = "GET /index.html HTTP/1.1\r\nHost: 192.168.24.128:8080\r\nConnection: Keep-Alive\r\nUser-Agent: Apache-HttpAsyncClient/4.1.3 (Java/1.8.0_91)\r\n\r\n";

	@Override
	public void connected(IOSession session) {
		session.setEventMask(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
	}

	@Override
	public void inputReady(IOSession session) {
		// new RuntimeException("inputReady").printStackTrace();

		ByteBuffer fixedRes = ByteBuffer.allocate(236);
		try {
			session.channel().read(fixedRes);
			long start = (long) session.getAttribute(IOSession.ATTACHMENT_KEY);
			monitor.log(start, Result.SUCC);
			ioSessions.add(session);
			connectionResources.release();
		} catch (IOException e) {
			LG.error("read error", e);
			session.close();
		}
	}

	@Override
	public void outputReady(IOSession session) {
		try {
			session.channel().write(ByteBuffer.wrap(content.getBytes()));
			session.clearEvent(SelectionKey.OP_WRITE);
		} catch (IOException e) {
			LG.error("write error", e);
			session.close();
		}

	}

	@Override
	public void timeout(IOSession session) {
		new RuntimeException().printStackTrace();
		session.close();
	}

	@Override
	public void disconnected(IOSession session) {
		long start = (long) session.getAttribute(IOSession.ATTACHMENT_KEY);
		monitor.log(start, Result.connectionClosed);
		connectionResources.release();
	}

}
