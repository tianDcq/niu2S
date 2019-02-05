package com.micro.frame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

class CallMgr extends Thread {
    private LinkedBlockingQueue<Call> reqs = new LinkedBlockingQueue<>();
    private HashMap<String, Callback> urls = new HashMap<>();

    void register(String url, Callback callback) {
        urls.put(url, callback);
    }

    Call create(String url, Map<String, Object> params) {
        Callback urlCall = urls.get(url);
        if (urlCall == null) {
            return null;
        }
        urlCall.setData(params);
        Call req = new Call();
        req.mgr = this;
        req.setCall(urlCall);
        return req;
    }

    void add(Call call) {
        reqs.add(call);
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