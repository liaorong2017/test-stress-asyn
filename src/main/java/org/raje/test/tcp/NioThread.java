package org.raje.test.tcp;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Queue;

import javax.annotation.Resource;

import org.raje.test.common.ConnectionResources;
import org.raje.test.common.Result;
import org.raje.test.monitor.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 
 * @author raje 2018年6月28日
 *
 */
@Component("nioThread")
public class NioThread extends Thread {
	private static final Logger LG = LoggerFactory.getLogger(NioThread.class);
	@Resource(name = "todoList")
	private Queue<NioContext> todoList;

	@Resource(name = "reqList")
	private Queue<NioContext> reqList;

	@Resource
	private Monitor monitor;

	@Resource
	private NioCallBack callBack;

	@Resource
	private Selector selector;

	@Resource
	private ConnectionResources connections;

	public NioThread() {
		super("io_selector");
	}

	public void run() {
		while (true) {
			try {
				Iterator<NioContext> itrr = todoList.iterator();
				while (itrr.hasNext()) {
					NioContext reqCtx = itrr.next();
					reqCtx.setStart(System.currentTimeMillis());
					reqCtx.setSelectionKey(reqCtx.getSocketChannel().register(selector, SelectionKey.OP_CONNECT, reqCtx));
					reqList.add(reqCtx);
					itrr.remove();
				}

				try {
					if (selector.select() == 0) {
						continue;
					}
				} catch (Exception e) {
					LG.error("selector.select error", e);
					continue;
				}
				// 遍历待处理连接
				Iterator<SelectionKey> itr = selector.selectedKeys().iterator();
				while (itr.hasNext()) {
					SelectionKey selectionKey = itr.next();
					// 与超时处理线程同步（同一时刻只允许其中一个进行处理）
					synchronized (selectionKey.attachment()) {
						if (selectionKey.isValid()) {
							if (selectionKey.isConnectable()) {
								// 处理连接事件
								handleConnectable(selectionKey);
							} else if (selectionKey.isReadable()) {
								// 处理可读事件
								handleReadable(selectionKey);
							} else if (selectionKey.isWritable()) {
								// 处理可写事件
								handleWritable(selectionKey);
							}
						}
					}
					// 移除已处理连接
					itr.remove();
				}
			} catch (CancelledKeyException ce) {
				LG.warn("", ce);
			} catch (Exception e) {
				LG.error("", e);
			} finally {

			}
		}
	}

	private void handleConnectable(SelectionKey selectionKey) {
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
		NioContext context = (NioContext) selectionKey.attachment();
		// 当连接等待建立时完成建立
		if (socketChannel.isConnectionPending()) {
			try {
				// 完成建立
				if (socketChannel.finishConnect()) {
					// 切换到等待发送请求
					selectionKey.interestOps(SelectionKey.OP_WRITE);
				} else {

					monitor.log(context.getStart(), Result.connectTimeout);
					LG.error("connect io exception"+socketChannel.finishConnect());
					// 建立连接异常
					System.exit(1);
				}
			} catch (IOException e) {

				if (e instanceof ConnectException) {
					long cost = System.currentTimeMillis() - context.getStart();
					LG.error("connect timeout :"+cost, e);
					monitor.log(context.getStart(), Result.connectTimeout);
				} else {

					LG.error("connect io exception", e);
					// 建立连接异常
					System.exit(1);
				}
			}
		}
	}

	private void handleWritable(SelectionKey selectionKey) {
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
		NioContext reqCtx = (NioContext) selectionKey.attachment();
		try {
			socketChannel.write(ByteBuffer.wrap(reqCtx.getProducer().producerMessage()));
		} catch (IOException e) {
			LG.error("write data to socket error", e);
			System.exit(1);
		}
		selectionKey.interestOps(SelectionKey.OP_READ);

	}

	private void handleReadable(SelectionKey selectionKey) {
		connections.release();
		if(connections.availablePermits() > 50){
			System.out.println(connections.availablePermits());
		}
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
		try {			
			NioContext reqCtx = (NioContext) selectionKey.attachment();
			ByteBuffer fixedRes = ByteBuffer.allocate(123);
			socketChannel.read(fixedRes);
			Result res = callBack.callBack(new String(fixedRes.array(), Charset.forName("GBK")));
			monitor.log(reqCtx.getStart(), res);
			reqList.remove(reqCtx);
		} catch (IOException e) {
			LG.error("read data to socket error", e);
			// 建立连接异常
			System.exit(1);
		}finally {
			try {
				socketChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
