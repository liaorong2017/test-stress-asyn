package org.raje.test.config;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.raje.test.common.RequestContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

@Component
@PropertySources(value = { @PropertySource("conf.properties") })
public class AppConfig {

	@Bean(name = "reqListBlocking")
	public BlockingQueue<RequestContext> blockingQueue() {
		return new LinkedBlockingQueue<RequestContext>();
	}

}
