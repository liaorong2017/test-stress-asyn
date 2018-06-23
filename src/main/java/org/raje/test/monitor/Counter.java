package org.raje.test.monitor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Counter {
	@Value("${max.current}")
	private long maxCurrent;
	
	private static long currTime = System.currentTimeMillis();
	private static long sucNumTmp = 0l;
	private static long errNumTmp = 0l;
	private static long totalTmp = 0l;
	private static long allTimeCost = 0l;
	private static long index = 1l;
	private static long avgCostTime = 0l;

	private static final long sec = 1000;

	@Value("${interval_time_sec:5}")
	private long intervalTime;

	public void count(long startTime, int result, long costTime) {
		synchronized (Counter.class) {
			// doPriant();
			if ((currTime + (intervalTime * sec)) < startTime) {
				doPriant();
				reset(startTime);
			}
			increment(startTime, result, costTime);
		}
	}

	private void doPriant() {
		System.out.println(String.format("第%d个%d秒: avgCostTime:%d, sucNum:%d, failNum:%d, succRate:%d , TPS:%d", index,
				intervalTime, allTimeCost / totalTmp, sucNumTmp, errNumTmp, sucNumTmp * 100 / totalTmp,
				totalTmp / intervalTime));
		if (avgCostTime == 0) {
			avgCostTime = allTimeCost / totalTmp;
		} else {
			avgCostTime = (allTimeCost / totalTmp + avgCostTime) / 2;
		}

	}

	private void increment(long startTime, int result, long costTime) {
		totalTmp++;
		if (0 == result) {
			sucNumTmp++;
		} else {
			errNumTmp++;
		}
		allTimeCost += costTime;
	}

	private void reset(long startTime) {
		index++;
		currTime = startTime;
		sucNumTmp = 0l;
		errNumTmp = 0l;
		totalTmp = 0l;
		allTimeCost = 0l;
	}

	public long counterTPS() {
		if (avgCostTime == 0) {
			return Integer.MAX_VALUE;
		}
		return (1000 *  maxCurrent) / avgCostTime;

	}

}
