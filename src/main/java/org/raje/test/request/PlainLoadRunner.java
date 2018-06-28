package org.raje.test.request;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.raje.test.monitor.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PlainLoadRunner {
	@Resource
	private Counter counter;

	@Resource
	private AtomicInteger currTpsPlain;

	@Value("${send.request.threads:1}")
	private int sendThreads;

	private final Logger LG = LoggerFactory.getLogger(PlainLoadRunner.class);
	private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "plain_load_runner");
		}
	});

	private Executor sendRequestExecutor;

	@Value("${concurrent}")
	private String conCurrencyStr;

	@Resource
	private RequestSemaphoreTask task;

	@Resource
	private RequestSender requestSender;

	@PostConstruct
	public void init() {
		sendRequestExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2, new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "sender_request");
			}
		});

		for (int i = 0; i < Runtime.getRuntime().availableProcessors() * 2; i++) {
			sendRequestExecutor.execute(requestSender);
		}
		loadTask();

	}

	public void loadTask() {
		LG.info("--------------test tps start ------");
		String[] testArr = conCurrencyStr.split("\\|");
		int[] meterTimes = new int[testArr.length];
		int[] meterConcurrency = new int[testArr.length];
		for (int i = 0; i < testArr.length; i++) {
			String[] timeCon = testArr[i].split(":");
			meterTimes[i] = Integer.parseInt(timeCon[0]);
			meterConcurrency[i] = Integer.parseInt(timeCon[1]);
		}
		for (int i = 0; i < meterConcurrency.length; i++) {
			int intervalCnt = (int) Math.ceil(meterTimes[i] / (double) counter.getIntervalTime());
			for (int j = 0; j < intervalCnt; j++) {
				loadRun(counter.getIntervalTime(), exceptTps(meterConcurrency[i]));
			}
		}
		System.exit(1);
	}

	private int exceptTps(int plainTps) {
		int exceptTps = (int) Math.min(plainTps, counter.adjustTPS());
		currTpsPlain.set(exceptTps);
		return exceptTps;
	}

	private void loadRun(long lastTimeSeconds, int concurrentCount) {

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
		try {
			Thread.sleep(1000 * lastTimeSeconds);
		} catch (InterruptedException e) {
			LG.error("testConcurrency sleep InterruptedException", e);
		}
	}

}
