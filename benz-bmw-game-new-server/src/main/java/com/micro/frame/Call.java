package com.micro.frame;

import com.micro.common.bean.GlobeResponse;
import java.util.Map;
import com.alibaba.fastjson.JSON;

import lombok.Setter;

class Call {
    private @Setter Callback call;
    private Trigger success;
    private Trigger failure;
    CallMgr mgr;

    void setSuccess(Callback callback) {
        success = GameMain.getInstance().getTaskMgr().createTrigger(callback);
    }

    void setFailure(Callback callback) {
        failure = GameMain.getInstance().getTaskMgr().createTrigger(callback);
    }

    void done() {
        mgr.add(this);
    }

    void run() {
        call.func();
        GlobeResponse data = (GlobeResponse) call.getData();

        Trigger ok;
        Trigger no;
        if ("200".equals(data.getCode())) {
            ok = success;
            no = failure;
        } else {
            ok = failure;
            no = success;
        }
        if (ok != null) {
            ok.getCallback().setData(JSON.parseObject(JSON.toJSONString(data.getData()), Map.class));
            ok.fire();
        }
        if (no != null) {
            no.stop();
        }
    }
}