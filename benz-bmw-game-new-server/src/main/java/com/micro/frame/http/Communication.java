package com.micro.frame.http;

import com.alibaba.fastjson.JSON;
import com.micro.common.bean.GlobeResponse;
import com.micro.frame.util.SpringUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代理 请求连接
 */
public class Communication {

    static Map<String, ComCallback> gameServiceMap = new HashMap<>();
    static Map<String, ComCallback> accountServiceMap = new HashMap<>();

    private static GameFeignClient gameFeignClient;

    private static AccountFeignClient accountFeignClient ;



    static {
        gameServiceMap.put("/game/getWildGameRoomConfigVo",new ComCallback(){
            @Override
            public Object func(Map<String, Object> map) {
                GlobeResponse<List<RoomConfigurationVO>> wildGameRoomConfigVo = gameFeignClient.getWildGameRoomConfigVo((Long) map.get("siteId"), (Integer) map.get("gameId"));
                return wildGameRoomConfigVo;
            }
        });

        gameServiceMap.put("/game/getWildGameRoomConfigVo2",new ComCallback(){
            @Override
            public Object func(Map<String, Object> map) {
                GlobeResponse<Object> wildGameRoomConfigVo = gameFeignClient.getAllSiteGame((Integer) map.get("gameId"));
                return wildGameRoomConfigVo;
            }
        });

        accountServiceMap.put("/acc/getPlayer",new ComCallback(){
            @Override
            public Object func(Map<String, Object> map) {
                GlobeResponse<Object> wildGameRoomConfigVo = accountFeignClient.getPlayer((Long) map.get("siteId"), (String) map.get("account"));
                String jsonString = JSON.toJSONString(wildGameRoomConfigVo.getData());
                HashMap hashMap = JSON.parseObject(jsonString, HashMap.class);
                hashMap.put("uniqueId", map.get("uniqueId"));
                wildGameRoomConfigVo.setData(hashMap);
                return wildGameRoomConfigVo;
            }
        });

        accountServiceMap.put("/acc/addMoney",new ComCallback(){
            @Override
            public Object func(Map<String, Object> map) {
                GlobeResponse<Object> wildGameRoomConfigVo = accountFeignClient.addMoney((Long) map.get("account"), (String) map.get("account"),(BigDecimal) map.get("money"));
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

    public static Map<String, ComCallback> getAccoutServiceMap() {
        if (accountFeignClient == null) {
            AccountFeignClient bean = SpringUtil.getBean(AccountFeignClient.class);
            accountFeignClient = bean;
        }
        return accountServiceMap;
    }

    public static void main(String[] args) {

    }


}
