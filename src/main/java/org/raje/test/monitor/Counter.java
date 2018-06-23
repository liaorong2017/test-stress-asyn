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
	private static long globalAvgCost = 0l;

	private static final long sec = 1000;

	@Value("${interval_time_sec:5}")
	private long intervalTime;

	public void count(long startTime, int result, long costTime) {
		synchronized (Counter.class) {
			// doPriant();
			if ((currTime + (intervalTime * sec)) < startTime) {
				coutGlobalCost();
				doPriant();
				reset(startTime);
			}
			increment(startTime, result, costTime);
		}
	}

	private void doPriant() {	
		System.out.println(String.format(
				"第%d个%d秒: curAvgCost:%d, sucNum:%d, failNum:%d, succRate:%d , curTPS:%d, globalAvgCost:%s,maxTps:%s",
				index, intervalTime, allTimeCost / totalTmp, sucNumTmp, errNumTmp, sucNumTmp * 100 / totalTmp,
				totalTmp / intervalTime, globalAvgCost, globalAvgCost == 0 ? 0 : counterTPS()));
		

	}

	private void coutGlobalCost() {
		long currAvgCost = allTimeCost / totalTmp;
		if (globalAvgCost == 0) {
			globalAvgCost = currAvgCost;
		} else {
			if ((currAvgCost * 100) / globalAvgCost < 150)
				globalAvgCost = (currAvgCost * (index-1) + globalAvgCost) / index;
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
		if (globalAvgCost == 0) {
			return Integer.MAX_VALUE;
		}
		return (1000 * maxCurrent) / globalAvgCost;

	}

}
