package com.micro.frame;

import com.micro.common.bean.GlobeResponse;
import java.util.Map;
import com.alibaba.fastjson.JSON;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class Call {
    private @Setter Callback call;
    private Trigger success;
    private Trigger failure;
    private @Setter long timeout = Config.DEFAULTTIMEOUT;
    private @Getter long runTime;
    private boolean isDone;

    void setSuccess(Callback callback) {
        success = GameMain.getInstance().getTaskMgr().createTrigger(callback);
    }

    void setFailure(Callback callback) {
        failure = GameMain.getInstance().getTaskMgr().createTrigger(callback);
    }

    boolean isTimeout() {
        return System.currentTimeMillis() - this.runTime >= timeout;
    }

    void run() {
        this.runTime = System.currentTimeMillis();
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
            ok.getCallback().setData(data.getData());
            ok.fire();
        }
        if (no != null) {
            no.stop();
        }
    }
}