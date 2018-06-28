package org.raje.test.tcp;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component("timeoutThread")
public class TimeoutThread extends Thread {
    private static final Logger LG = LoggerFactory.getLogger(TimeoutThread.class);
    @Resource(name = "reqList")
    private Queue<NioContext> reqList;

    @Resource
    private Executor executor;

    public void run() {
        List<NioContext> timeoutList = new LinkedList<NioContext>();
        while (true) {
            try {
                timeoutList.clear();
                // 获取超时集合
                long time = System.currentTimeMillis();
                Iterator<NioContext> itr = reqList.iterator();
                while (itr.hasNext()) {
                	NioContext req = itr.next();
                    if (req.getTimeoutTimestamp() < time) {
                        itr.remove();
                        timeoutList.add(req);
                    } else {
                        break;
                    }
                }
                // 处理超时集合
				for (NioContext req : timeoutList) {
                    executor.execute(req.getTimeoutCallback());
                }
                // 睡0.5秒再继续
                sleep(500);
            } catch (Exception e) {
                LG.error("", e);
            }
        }
    }
}
