package org.raje.test.monitor;

import java.util.concurrent.atomic.AtomicInteger;

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
	private ConnectionResources connections;

	@Resource(name = "currTpsPlain")
	private AtomicInteger curPlainTps;

	@Resource(name = "totalSendCnt")
	private AtomicInteger totalSendCnt;

	@Resource(name = "hitCacheCnt")
	private AtomicInteger hitCacheCnt;

	private long currTime = System.currentTimeMillis();
	private long succCnt = 0l;
	private long errCnt = 0l;
	private long totalCnt = 0l;
	private long totalCost = 0l;
	private long index = 1l;
	private long adjustAvgCost = 0l;
	private long limitTps = Long.MAX_VALUE;
	private long realMaxTps = 0l;
	private long remmainCnt = 0l;
	private long[] maxTps = new long[10];
	private int maxTpsIndex = 0;

	public void count(long startTime, int result, long costTime) {
		synchronized (Counter.class) {
			if ((currTime + (intervalTime * sec)) < startTime) {
				coutGlobalCost();
				setRealMaxTps(totalCnt / intervalTime);
				doPriant();
				reset(startTime);
			}
			increment(startTime, result, costTime);
		}
	}

	private void doPriant() {
		System.out.println(String.format("第%d个%d秒: avgCost:%d, succ:%d, fail:%d, succRate:%d , TPS:%d,[recentCost:%s,plainTps:%s,limitTps:%s,connect:%s,recentMaxTps:%s,count:%s,cacheRate:%s]", index,
				intervalTime, totalCost / totalCnt, succCnt, errCnt, succCnt * 100 / totalCnt, totalCnt / intervalTime, adjustAvgCost, curPlainTps.get(), limitTps == Long.MAX_VALUE ? 0 : limitTps,
				this.maxCurrent - this.connections.availablePermits(), realMaxTps, remmainCnt, hitCacheCnt.get() == 0 ? 0 : hitCacheCnt.get() * 100 / totalSendCnt.get()));

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
		hitCacheCnt.set(0);
		totalSendCnt.set(0);
	}

	public long adjustTPS() {
		if (adjustAvgCost == 0) {
			return Integer.MAX_VALUE;
		}
		long theoreticalTps = (1000 * maxCurrent) / adjustAvgCost;
		if (remmainCnt >= 10) {
			if (realMaxTps < theoreticalTps) {
				maxTps[maxTpsIndex % 10] = realMaxTps;
				maxTpsIndex++;
				if (maxTpsIndex < 10) {
					limitTps = avgMaxTps() * (110 - maxTpsIndex) / 100;
				} else {
					limitTps = avgMaxTps() * 101 / 100;
				}
				remmainCnt = 0;
				realMaxTps = 0;
			}
		}
		return Math.min(theoreticalTps, limitTps);
	}

	public long avgMaxTps() {
		long sum = 0;
		for (long tps : maxTps) {
			sum += tps;
		}
		if (maxTpsIndex < 10) {
			return sum / maxTpsIndex;
		} else {
			return sum / 10;
		}
	}

	public long getIntervalTime() {
		return intervalTime;
	}

	public long getRealMaxTps() {
		return realMaxTps;
	}

	public void setRealMaxTps(long currTps) {
		if (this.realMaxTps >= currTps) {

			if (Long.MAX_VALUE == limitTps) {
				remmainCnt++;
			} else {
				long threshold = limitTps * 80 / 100;
				if (currTps > threshold || curPlainTps.get() > threshold) {
					remmainCnt++;
				}
			}
		} else {
			remmainCnt = 0;
		}
		this.realMaxTps = Math.max(currTps, this.realMaxTps);
	}

}
