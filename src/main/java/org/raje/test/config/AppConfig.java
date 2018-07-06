package org.raje.test.config;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.raje.test.common.ConnectionResources;
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

	@Bean(name = "currTpsPlain")
	public AtomicInteger currTpsPlain() {
		return new AtomicInteger(0);
	}

	@Bean(name = "maxCurrent")
	public ConnectionResources maxCurrent() {
		return new ConnectionResources(Integer.parseInt(env.getProperty("max.current")));
	}

	@Bean(name = "staySenderRequest")
	public Semaphore staySenderRequest() {
		return new Semaphore(0);
	}
	
	@Bean(name = "hitCacheCnt")
	public AtomicInteger hitCacheCnt() {
		return new AtomicInteger(0);
	}
	
	
	@Bean(name = "totalSendCnt")
	public AtomicInteger totalSendCnt() {
		return new AtomicInteger(0);
	}
	
	
	@Bean(name = "maxCreateConns")
	public Semaphore maxCreateConns() {
		return new Semaphore(200);
	}
	
	
	
	@Bean(name = "closeCnt")
	public AtomicInteger closeCnt() {
		return new AtomicInteger(0);
	}

	
	@Bean(name = "remainTimes")
	public AtomicLong remainTimes() {
		return new AtomicLong(0);
	}
	
	
	@Bean(name = "createConnCnt")
	public AtomicInteger createConnCnt() {
		return new AtomicInteger(0);
	}

	
	@Bean(name = "createConnCost")
	public AtomicLong createConnCost() {
		return new AtomicLong(0);
	}
	
}
