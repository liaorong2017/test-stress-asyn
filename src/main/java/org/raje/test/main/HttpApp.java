package org.raje.test.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.raje.test.config","org.raje.test.common","org.raje.test.http","org.raje.test.monitor"})
@EnableAutoConfiguration
public class HttpApp {
	public static void main(String[] args) {
		SpringApplication.run(HttpApp.class, args);
	}

}
