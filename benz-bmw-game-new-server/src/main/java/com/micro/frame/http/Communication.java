package com.micro.frame.http;

import com.micro.common.bean.GlobeResponse;
import com.micro.frame.util.SpringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代理 请求连接
 */
public class Communication {

    static Map<String, ComCallback> gameServiceMap = new HashMap<>();

    private static GameFeignClient gameFeignClient;



    static {
        gameServiceMap.put("/game/getWildGameRoomConfigVo",new ComCallback(){
            @Override
            public Object func(Map<String, Object> map) {
                GlobeResponse<List<RoomConfigurationVO>> wildGameRoomConfigVo = gameFeignClient.getWildGameRoomConfigVo((Long) map.get("siteId"), (Integer) map.get("gameId"));
                return wildGameRoomConfigVo;
            }
        });

    }

    public static Map<String, ComCallback> getGameServiceMap() {
        if (gameFeignClient == null) {
            GameFeignClient bean = SpringUtil.getBean(GameFeignClient.class);
            gameFeignClient = bean;
        }
        return gameServiceMap;
    }

    public static void main(String[] args) {

    }


}
