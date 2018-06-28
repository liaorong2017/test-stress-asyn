package org.raje.test.tcp;

import org.springframework.stereotype.Component;

@Component
public class RequestProducer {
	
	public byte[] producerMessage(){
		return "hello".getBytes();
	}

}
