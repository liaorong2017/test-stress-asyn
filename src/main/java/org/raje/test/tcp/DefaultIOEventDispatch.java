package org.raje.test.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
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
	private SimpleIOSessionPool pool;
	
	@Resource(name="closeCnt")
	private AtomicInteger closeCnt;
	
	@Resource(name="remainTimes")
	private AtomicLong remainTimes;

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
		session.setEvent(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
	}

	@Override
	public void inputReady(IOSession session) {
		if (isAttachmentNull(session)) {
			if (!session.isClosed()) {
				try {
					ByteBuffer fixedRes = ByteBuffer.allocate(200);
					int len = session.channel().read(fixedRes);
					if (len == 123) {
						monitor(session, Result.SUCC);
						pool.release(session);
					} else if (len == 142) {
						monitor(session, Result.SUCC);
					} else if (len < 0) {
						monitor(session, Result.connectionClosed);
					} else {
						System.out.println(new String(fixedRes.array(), 0, len));
						System.exit(1);
					}
				} catch (IOException e) {
					LG.error("未知情况关闭", e);
					monitor(session, Result.connectionClosed);
					close(session);
				}
			} else {
				System.out.println("close ");
				monitor(session, Result.connectionClosed);
			}
		} else {
			close(session);
		}
	}

	@Override
	public void outputReady(IOSession session) {
		try {
			session.channel().write(ByteBuffer.wrap(req.toString().getBytes()));
			session.clearEvent(SelectionKey.OP_WRITE);
		} catch (IOException e) {
			e.printStackTrace();
			monitor(session, Result.connectionClosed);
			close(session);
		}

	}

	@Override
	public void timeout(IOSession session) {
		if (isAttachmentNull(session)) {
			monitor(session, Result.readTimeout);		
		} else {
			System.out.println("time out");
		}
	}

	@Override
	public void disconnected(IOSession session) {
		closeCnt.incrementAndGet();
		remainTimes.addAndGet(System.currentTimeMillis() - (long)session.getAttribute("createTime"));
		if (session.getAttribute("close") == null) {
			new RuntimeException().printStackTrace();
		} else {
			session.removeAttribute("close");
		}
	}

	private void reset(IOSession session) {
		session.removeAttribute(IOSession.ATTACHMENT_KEY);
		session.setSocketTimeout(Integer.MAX_VALUE);
	}

	private void close(IOSession session) {
		pool.remove(session);
		session.setAttribute("close", "flag");
		session.close();
	}

	private boolean isAttachmentNull(IOSession session) {
		return session.getAttribute(IOSession.ATTACHMENT_KEY) != null;
	}

	private void monitor(IOSession session, Result res) {
		monitor.log((long) session.getAttribute(IOSession.ATTACHMENT_KEY), res);
		reset(session);
		connectionResources.release();
		
	}

}
