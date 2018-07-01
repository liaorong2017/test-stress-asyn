package org.raje.test.tcp;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.raje.test.common.ConnectionResources;
import org.raje.test.common.Result;
import org.raje.test.monitor.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("timeoutThread")
public class TimeoutThread extends Thread {
	private static final Logger LG = LoggerFactory.getLogger(TimeoutThread.class);

	@Resource(name = "reqList")
	private Queue<NioContext> reqList;

	@Resource
	private ConnectionResources connections;

	@Resource
	private Monitor monitor;

	public TimeoutThread() {
		super("timeout_check");
	}

	@PostConstruct
	public void init() {

	}

	public void run() {
		List<NioContext> timeoutList = new LinkedList<NioContext>();
		while (true) {
			try {
				timeoutList.clear();
				// 获取超时集合
				long time = System.currentTimeMillis();
				Iterator<NioContext> itr = reqList.iterator();
				while (itr.hasNext()) {
					NioContext req = itr.next();
					if (req.getMaxTimeoutTime() < time) {
						itr.remove();
						try {							
							req.getSelectionKey().cancel();
							req.getSelectionKey().attach(null);
							req.getSocketChannel().close();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							connections.release();
							if (connections.availablePermits() > 50) {
								System.out.println(connections.availablePermits());
							}
							monitor.log(req.getStart(), Result.timeout);
						}
					}
				}
				sleep(500);
			} catch (Exception e) {
				LG.error("", e);
			}
		}
	}
}
