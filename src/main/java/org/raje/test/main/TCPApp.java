package org.raje.test.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.raje.test.config","org.raje.test.common","org.raje.test.tcp","org.raje.test.monitor"})
@EnableAutoConfiguration
public class TCPApp {
	public static void main(String[] args) {
		SpringApplication.run(TCPApp.class, args);
	}

}
