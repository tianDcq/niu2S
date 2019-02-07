package com.micro.frame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class CallMgr extends Thread {
    private LinkedBlockingQueue<Call> reqs = new LinkedBlockingQueue<>();

    void call(Call c) {
        reqs.add(c);
    }

    @Override
    public void run() {

        while (true) {
            try {
                Call req = reqs.poll();
                if (req != null) {
                    req.run();
                }
            } catch (Exception err) {
                log.error("多线程任务未知错误！！！ err:" + err.getMessage());
            }
        }
    }
}