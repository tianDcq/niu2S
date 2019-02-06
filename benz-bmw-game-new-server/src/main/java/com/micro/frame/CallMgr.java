package com.micro.frame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

class CallMgr extends Thread {
    private LinkedBlockingQueue<Call> reqs = new LinkedBlockingQueue<>();

    void call(Call c) {
        reqs.add(c);
    }

    @Override
    public void run() {

        while (true) {
            Call req = reqs.poll();
            if (req != null) {
                req.run();
            }
        }
    }
}