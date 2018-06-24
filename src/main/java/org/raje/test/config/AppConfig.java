package org.raje.test.config;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.raje.test.common.ConnectionResources;
import org.raje.test.request.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySources(value = { @PropertySource("conf.properties") })
public class AppConfig {

	@Autowired
	private Environment env;

	@Bean(name = "reqListBlocking")
	public BlockingQueue<RequestContext> blockingQueue() {
		return new LinkedBlockingQueue<RequestContext>();
	}

	@Bean(name = "currTpsPlain")
	public AtomicInteger currTpsPlain() {
		return new AtomicInteger(0);
	}

	@Bean(name = "maxCurrent")
	public ConnectionResources semaphore() {
		return new ConnectionResources(Integer.parseInt(env.getProperty("max.current")));
	}
	
	@Bean(name = "periodDiscardCnt")
	public AtomicLong periodRealDiscardCnt() {
		return new AtomicLong(0);
	}
	
	

}
