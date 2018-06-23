package org.raje.test.monitor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.raje.test.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Monitor {
	private static final Logger LG = LoggerFactory.getLogger(Monitor.class);

	@Value("${REMOTE_LOG_IP}")
	private String remoteIp;

	@Value("${REMOTE_LOG_PORT}")
	private int remotePort;

	@Value("${REMOTE_LOG_SERVER}")
	private String server;

	@Value("${REMOTE_LOG_SERVICE}")
	private String service;

	@Value("${REMOTE_LOG_ENABLE:false}")
	private boolean enable;

	@Resource
	private Counter counter;

	private Socket socket = null;

	private static Executor exe = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "remote_log");
		}
	});

	@PostConstruct
	public void init() {
		if (enable) {
			socket = new Socket();
			try {
				socket.setKeepAlive(true);
				socket.setSoTimeout(0);
				socket.setTcpNoDelay(true);
				socket.connect(new InetSocketAddress(remoteIp, remotePort), 3000);
			} catch (Exception e) {
				LG.error("远程日记连接报错", e);
			}
		}

	}

	public void log(final long startTime, final Result result) {
		long costTime = System.currentTimeMillis() - startTime;
		exe.execute(new Runnable() {
			public void run() {
				counter.count(startTime, result.getResult(), costTime);
				if (enable && socket != null && socket.isConnected())

					sendLogToRemoteServer(startTime,
							result.getResult() < 100 ? result.getInfo() : String.valueOf(result.getResult()), costTime);

			}

			private void sendLogToRemoteServer(long startTime, String result, long costTime) {
				byte[] rlog = String.format("%d|%s|%s|%s|%d\n", startTime, server, service, result, costTime)
						.getBytes();
				synchronized (socket) {
					try {
						socket.getOutputStream().write(rlog);
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(-1);
					}
				}
			}

		});

	}

}
