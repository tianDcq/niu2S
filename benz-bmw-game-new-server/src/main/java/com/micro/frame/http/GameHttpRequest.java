package com.micro.frame.http;

import com.alibaba.fastjson.JSON;
import com.micro.common.bean.GlobeResponse;
import com.micro.frame.Callback;
import com.micro.frame.GameMain;
import com.micro.frame.TaskMgr;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GameHttpRequest {

    @Setter
    private Callback successCallback;

    @Setter
    private Callback failCallback;


    // 创建http对象
    public static GameHttpRequest buildRequest() {
        return new GameHttpRequest();
    }

    public void sendForm(String url, Map<String, Object> params) {
        try {
            ThreadPool.getExecutor().execute(()->{
                TaskMgr taskMgr = GameMain.getInstance().getTaskMgr();
                Map<String, ComCallback> gameServiceMap = GameMain.getInstance().getReqMgr().gccoutServiceMap();
                ComCallback callback = gameServiceMap.get(url);
                GlobeResponse<Object> func = (GlobeResponse<Object>) callback.func(params);

                String json = JSON.toJSONString(func.getData());

                Map map = JSON.parseObject(json, Map.class);

                if ("200".equals(func.getCode())) {
                    if (successCallback != null) {
                        successCallback.setData(map);
                        taskMgr.createTrigger(successCallback).fire();
                    }

                } else {
                    if (failCallback != null) {
                        failCallback.setData(map);
                        taskMgr.createTrigger(failCallback).fire();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
