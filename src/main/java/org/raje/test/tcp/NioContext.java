package org.raje.test.tcp;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by Sean Lei on 13/04/2017.
 */
public class NioContext {
	private long start;
	private long timeoutTimestamp;
	private Runnable timeoutCallback;
	private SocketChannel socketChannel;
	private SelectionKey selectionKey;
	private RequestProducer producer;

	public NioContext(int maxTimeout) {
		start = System.currentTimeMillis();
		this.timeoutTimestamp = System.currentTimeMillis() + maxTimeout;
	}

	public long getTimeoutTimestamp() {
		return timeoutTimestamp;
	}

	public Runnable getTimeoutCallback() {
		return timeoutCallback;
	}

	public void setTimeoutCallback(Runnable timeoutCallback) {
		this.timeoutCallback = timeoutCallback;
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

}
