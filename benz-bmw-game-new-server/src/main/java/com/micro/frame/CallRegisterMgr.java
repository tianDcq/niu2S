package com.micro.frame;

import java.util.HashMap;
import java.util.Map;

public class CallRegisterMgr {
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
        req.setCall(urlCall);
        return req;
    }
}