package com.micro.frame.http;

import com.micro.frame.Callback;
import com.micro.frame.Trigger;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
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

    // form表单请求
    public Callback sendForm(String url, Map<String, String> params) {
        try {
            HashMap hashMap = null;
            hashMap = HttpClientUtils.doPost(url, params);
            return getCallback(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // json表单请求
    public Callback sendJson(String url, Map<String, Object> params) {
        try {
            HashMap hashMap = null;
            HttpClientUtils.doPostJson(url, params);
            return getCallback(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Callback getCallback(HashMap hashMap) {
        if ("200".equals(hashMap.get("code"))) {

            Trigger trigger = new Trigger(successCallback);
            if (successCallback != null) {
                successCallback.func();
                successCallback.setData(hashMap.get("data"));
            }
            trigger.fire();

            return successCallback;
        } else {
            if (failCallback != null) {
                failCallback.func();
                successCallback.setData(hashMap.get("data"));
            }
            return failCallback;
        }
    }



    public static void main(String[] args) {
//        GameHttpRequest httpRequest = GameHttpRequest.buildRequest();
//        httpRequest.setSuccessCallback(new Callback() {
//            @Override
//            public void func() {
//                System.out.println(1);
//            }
//        });
//        httpRequest.setFailCallback(new Callback() {
//            @Override
//            public void func() {
//                System.out.println(2);
//            }
//        });
//        // 发起请求
//        Map<String, String> map = new HashMap<>();
//        map.put("siteId", "1");
//        map.put("gameId", "12");
//        Callback send = httpRequest.sendForm("http://localhost:9501/game/getWildGameRoomConfigVo",map);
//        System.out.println(send);


    }


}
