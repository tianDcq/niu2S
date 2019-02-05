package com.micro.frame;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.micro.frame.http.AccountFeignClient;
import com.micro.frame.http.GameFeignClient;
import com.micro.frame.util.SpringUtil;

import org.bouncycastle.jcajce.provider.digest.GOST3411.HashMac;

class Register {
    private static GameFeignClient gameFeignClient;
    private static AccountFeignClient accountFeignClient;

    static void calls() {
        gameFeignClient = SpringUtil.getBean(GameFeignClient.class);
        accountFeignClient = SpringUtil.getBean(AccountFeignClient.class);

        CallMgr callMgr = GameMain.getInstance().getCallMgr();

        callMgr.register("/game/getWildGameRoomConfigVo", new Callback() {
            @Override
            public void func() {
                Map map = (Map) this.getData();
                this.setData(
                        gameFeignClient.getWildGameRoomConfigVo((Long) map.get("siteId"), (Integer) map.get("gameId")));
            }
        });

        callMgr.register("/game/getWildGameRoomConfigVo2", new Callback() {
            @Override
            public void func() {
                Map map = (Map) this.getData();
                this.setData(gameFeignClient.getAllSiteGame((Integer) map.get("gameId")));
            }
        });

        callMgr.register("/acc/getPlayer", new Callback() {
            @Override
            public void func() {
                // Map map = (Map) this.getData();

                String json = JSON.toJSONString(this.getData());
                System.out.println("===发送请求的josn===>"+json);
                Map map = JSON.parseObject(json, HashMap.class);
                Long siteId = ((Integer)map.get("siteId")).longValue();
                Object obj=accountFeignClient.getPlayer(siteId, (String) map.get("account"));
                System.out.println("  111111111  "+obj);
                this.setData(obj);
            }
        });

        callMgr.register("/acc/addMoney", new Callback() {
            @Override
            public void func() {
                Map map = (Map) this.getData();

                this.setData(accountFeignClient.addMoney((Long) map.get("siteId"), (String) map.get("account"),
                        (BigDecimal) map.get("money")));
            }
        });
    }
}