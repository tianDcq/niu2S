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
        System.out.println("注册进去的一个call   ");
        urlCall.setData(params);
        Call req = new Call();
        req.mgr = this;
        req.setCall(urlCall);
        return req;
    }

    void add(Call call) {
        System.out.println("添加call   ");
        reqs.add(call);
    }

    @Override
    public void run() {
        while (true) {
            Call req = reqs.poll();
            if (req != null) {
                System.out.println("调用一个注册进去的   ");
                req.run();
            }
        }
    }
}