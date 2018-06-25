package org.raje.test.monitor;

import javax.annotation.Resource;

import org.raje.test.common.ConnectionResources;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Counter {

	private static final long sec = 1000;

	@Value("${max.current}")
	private long maxCurrent;

	@Value("${interval_time_sec:5}")
	private long intervalTime;

	@Resource
	private ConnectionResources semaphore;


	private long currTime = System.currentTimeMillis();
	private long succCnt = 0l;
	private long errCnt = 0l;
	private long totalCnt = 0l;
	private long totalCost = 0l;
	private long index = 1l;
	private long adjustAvgCost = 0l;
	private long realMaxTps = 0l;
	private long remmainCnt = 0l;

	public void count(long startTime, int result, long costTime) {
		synchronized (Counter.class) {
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
				"第%d个%d秒: avgCost:%d, succ:%d, fail:%d, succRate:%d , curTPS:%d, -----[adjAvgCost:%s,adjTps:%s,realMaxTps:%s,current:%s,]",
				index, intervalTime, totalCost / totalCnt, succCnt, errCnt, succCnt * 100 / totalCnt,
				totalCnt / intervalTime, adjustAvgCost, adjustAvgCost == 0 ? 0 : adjustTPS(), realMaxTps,
				this.maxCurrent - this.semaphore.availablePermits()));

	}

	private void coutGlobalCost() {
		long currAvgCost = totalCost / totalCnt;
		if (adjustAvgCost == 0) {
			adjustAvgCost = currAvgCost;
		} else {
			adjustAvgCost = (Math.max(currAvgCost, adjustAvgCost) + Math.min(currAvgCost, adjustAvgCost) * 2) / 3;
		}
	}

	public long getAdjustAvgCost() {
		return adjustAvgCost;
	}

	private void increment(long startTime, int result, long costTime) {
		totalCnt++;
		if (0 == result) {
			succCnt++;
		} else {
			errCnt++;
		}
		totalCost += costTime;
	}

	private void reset(long startTime) {
		index++;
		currTime = startTime;
		succCnt = 0l;
		errCnt = 0l;
		totalCnt = 0l;
		totalCost = 0l;
	}

	public long adjustTPS() {
		if (adjustAvgCost == 0) {
			return Integer.MAX_VALUE;
		}
		long theoreticalTps = (1000 * maxCurrent) / adjustAvgCost;
		if (remmainCnt >= 10) {			
			return Math.min(theoreticalTps, realMaxTps);
		}
		return theoreticalTps;
	}

	public long getIntervalTime() {
		return intervalTime;
	}

	public long getRealMaxTps() {
		return realMaxTps;
	}


	public void setRealMaxTps(long realMaxTps) {
		if (this.realMaxTps >= realMaxTps) {
			remmainCnt++;
		} else {
			remmainCnt = 0;
		}
		this.realMaxTps = Math.max(realMaxTps, this.realMaxTps);
	}

}
