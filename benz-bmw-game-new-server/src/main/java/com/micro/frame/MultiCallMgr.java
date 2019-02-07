package com.micro.frame;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class MultiCallMgr extends Thread {

    class CallThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r);
        }
    }

    private LinkedBlockingQueue<Call> reqs = new LinkedBlockingQueue<>();

    class CallThread implements Runnable {
        Call call;

        CallThread(Call call) {
            this.call = call;
        }

        @Override
        public void run() {
            call.run();
        }
    }

    class CallThreadRejecter implements RejectedExecutionHandler {
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            log.info("线程池卡死！！！！！！！！！！！！！！");
            executor.shutdownNow();
            threads = (ThreadPoolExecutor) Executors.newFixedThreadPool(Config.MAXCALLTHREAD);
        }

    }

    ThreadPoolExecutor threads;

    MultiCallMgr() {
        threads = (ThreadPoolExecutor) Executors.newFixedThreadPool(Config.MAXCALLTHREAD);

        threads.setRejectedExecutionHandler(new CallThreadRejecter());
    }

    boolean isDone() {
        return threads.getActiveCount() == 0;
    }

    void call(Call c) {
        reqs.add(c);
    }

    @Override
    public void run() {

        while (true) {
            Call req = reqs.poll();
            if (req != null) {
                CallThread t = new CallThread(req);
                threads.execute(t);
            }
        }
    }
}