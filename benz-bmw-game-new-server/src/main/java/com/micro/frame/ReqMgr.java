package com.micro.frame;

import com.alibaba.fastjson.JSON;
import com.micro.common.bean.GlobeResponse;
import com.micro.frame.http.AccountFeignClient;
import com.micro.frame.http.ComCallback;
import com.micro.frame.http.GameFeignClient;
import com.micro.frame.http.RoomConfigurationVO;
import com.micro.frame.util.SpringUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReqMgr {


    private Map<String, ComCallback> gameServiceMap = new HashMap<>();

    private Map<String, ComCallback> accountServiceMap = new HashMap<>();

    // 远程调用的接口,需要手动注入
    private static GameFeignClient gameFeignClient;
    private static AccountFeignClient accountFeignClient ;

    // 注册远程接口
    public void init(){

        getGameServiceMap();

        getGameServiceMap();


        gameServiceMap.put("/game/getWildGameRoomConfigVo",(Map<String, Object> map)->{
            GlobeResponse<List<RoomConfigurationVO>> wildGameRoomConfigVo = gameFeignClient.getWildGameRoomConfigVo((Long) map.get("siteId"), (Integer) map.get("gameId"));
            return wildGameRoomConfigVo;
        });

        gameServiceMap.put("/game/getWildGameRoomConfigVo2",(Map<String, Object> map)->{
            GlobeResponse<Object> wildGameRoomConfigVo = gameFeignClient.getAllSiteGame((Integer) map.get("gameId"));
            return wildGameRoomConfigVo;
        });

        accountServiceMap.put("/acc/getPlayer",(Map<String, Object> map)->{
            GlobeResponse<Object> wildGameRoomConfigVo = accountFeignClient.getPlayer((Long) map.get("siteId"), (String) map.get("account"));
            return wildGameRoomConfigVo;
        });

        accountServiceMap.put("/acc/addMoney",(Map<String, Object> map)->{
            GlobeResponse<Object> wildGameRoomConfigVo = accountFeignClient.addMoney((Long) map.get("siteId"), (String) map.get("account"),(BigDecimal) map.get("money"));
            return wildGameRoomConfigVo;
        });
    }

    public Map<String, ComCallback> getGameServiceMap() {
        if (gameFeignClient == null) {
            GameFeignClient bean = SpringUtil.getBean(GameFeignClient.class);
            gameFeignClient = bean;
        }
        return gameServiceMap;
    }

    public Map<String, ComCallback> gccoutServiceMap() {
        if (accountFeignClient == null) {
            AccountFeignClient bean = SpringUtil.getBean(AccountFeignClient.class);
            accountFeignClient = bean;
        }
        return accountServiceMap;
    }

}
