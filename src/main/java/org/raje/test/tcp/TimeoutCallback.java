package org.raje.test.tcp;

import java.io.IOException;

public class TimeoutCallback implements Runnable {
    private NioContext reqCtx;

    public TimeoutCallback(NioContext reqCtx) {
        this.reqCtx = reqCtx;
    }

    @Override
    public void run() {

        // 取消NIO监听
        reqCtx.getSelectionKey().cancel();
        // 关闭连接
        try {
            reqCtx.getSocketChannel().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
