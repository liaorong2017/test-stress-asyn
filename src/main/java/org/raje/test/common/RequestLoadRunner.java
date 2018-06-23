package org.raje.test.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RequestLoadRunner {
	private final Logger LG = LoggerFactory.getLogger(RequestLoadRunner.class);
	private ScheduledExecutorService scheduledExecutorService = Executors
			.newSingleThreadScheduledExecutor(new ThreadFactory() {
				@Override
				public Thread newThread(Runnable r) {
					return new Thread(r, "req_load_runner");
				}
			});
	
	

	@Value("${concurrent}")
	private String conCurrencyStr;

	@Resource
	private RequestContextTask task;

	@Resource
	private RequestSender requestSender;

	@PostConstruct
	public void init() {

		new Thread(requestSender, "req_sender").start();

		loadTask();

	}

	public void loadTask() {
		LG.info("--------------test concurrency------");
		String[] testArr = conCurrencyStr.split("\\|");
		int[] meterTimes = new int[testArr.length];
		int[] meterConcurrency = new int[testArr.length];
		for (int i = 0; i < testArr.length; i++) {
			String[] timeCon = testArr[i].split(":");
			meterTimes[i] = Integer.parseInt(timeCon[0]);
			meterConcurrency[i] = Integer.parseInt(timeCon[1]);
		}
		for (int i = 0; i < meterConcurrency.length; i++) {
			loadRun(meterTimes[i], meterConcurrency[i]);
			try {
				Thread.sleep(1000 * meterTimes[i]);
			} catch (InterruptedException e) {
				LG.error("testConcurrency sleep InterruptedException", e);
			}
		}
		System.exit(1);
	}

	private void loadRun(int lastTimeSeconds, int concurrentCount) {
		double totalRequest = lastTimeSeconds * concurrentCount;
		double totalMicroSeconds = lastTimeSeconds * 1000 * 1000;
		int timeInterval = 1;
		if (totalRequest > totalMicroSeconds) {
			throw new RuntimeException("请求量过大:" + concurrentCount);
		}
		timeInterval = (int) Math.ceil(totalMicroSeconds / totalRequest);
		long timePassed = 0;
		for (int i = 1; timePassed < totalMicroSeconds; i++) {
			timePassed = i * timeInterval;
			scheduledExecutorService.schedule(task, timePassed, TimeUnit.MICROSECONDS);
		}
	}

}
