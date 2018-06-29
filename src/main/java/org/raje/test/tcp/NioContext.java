package org.raje.test.tcp;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class NioContext {
	private long start;
	private long maxTimeoutTime;
	private SocketChannel socketChannel;
	private SelectionKey selectionKey;
	private RequestProducer producer;

	public NioContext(int maxTimeout) {
		start = System.currentTimeMillis();
		maxTimeoutTime = start + maxTimeout;
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public SelectionKey getSelectionKey() {
		return selectionKey;
	}

	public void setSelectionKey(SelectionKey selectionKey) {
		this.selectionKey = selectionKey;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public RequestProducer getProducer() {
		return producer;
	}

	public void setProducer(RequestProducer producer) {
		this.producer = producer;
	}

	public long getMaxTimeoutTime() {
		return maxTimeoutTime;
	}

	public void setMaxTimeoutTime(long maxTimeoutTime) {
		this.maxTimeoutTime = maxTimeoutTime;
	}
	
	

}
