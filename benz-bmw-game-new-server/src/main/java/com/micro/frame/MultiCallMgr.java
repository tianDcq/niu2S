package com.micro.frame;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

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

    ExecutorService threads;

    MultiCallMgr() {
        threads = Executors.newFixedThreadPool(Config.MAXCALLTHREAD);
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