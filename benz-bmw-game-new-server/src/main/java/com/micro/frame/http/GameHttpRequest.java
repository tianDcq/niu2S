package com.micro.frame.http;

import com.alibaba.fastjson.JSON;
import com.micro.common.bean.GlobeResponse;
import com.micro.frame.Callback;
import com.micro.frame.GameMain;
import com.micro.frame.TaskMgr;
import com.micro.frame.Trigger;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            TaskMgr taskMgr = GameMain.getInstance().getTaskMgr();
            Map<String, ComCallback> gameServiceMap = Communication.getGameServiceMap();
            ComCallback callback = gameServiceMap.get(url);
            GlobeResponse<List<RoomConfigurationVO>> func = (GlobeResponse<List<RoomConfigurationVO>>) callback.func(params);

            String json = JSON.toJSONString(func.getData());

            if ("200".equals(func.getCode())) {
                if (successCallback != null) {
                    successCallback.setData(json);
                    taskMgr.createTrigger(successCallback).fire();
                }

            } else {
                if (failCallback != null) {
                    failCallback.setData(json);
                    taskMgr.createTrigger(failCallback).fire();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
