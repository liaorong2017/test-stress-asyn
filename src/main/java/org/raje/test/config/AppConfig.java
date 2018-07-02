package org.raje.test.config;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

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

	
}
