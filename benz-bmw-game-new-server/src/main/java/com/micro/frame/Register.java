package com.micro.frame;

import java.math.BigDecimal;
import java.util.Map;

import com.micro.frame.http.AccountFeignClient;
import com.micro.frame.http.GameFeignClient;
import com.micro.frame.util.SpringUtil;

class Register {
    private static GameFeignClient gameFeignClient;
    private static AccountFeignClient accountFeignClient;

    static void calls() {
        gameFeignClient = SpringUtil.getBean(GameFeignClient.class);
        accountFeignClient = SpringUtil.getBean(AccountFeignClient.class);

        CallRegisterMgr callMgr = GameMain.getInstance().getCallRegisterMgr();

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
                System.out.println("scott is idiot+++++++++++++++++++++++" + this.getData());
                Map map = (Map) this.getData();
                this.setData(accountFeignClient.getPlayer((Long) map.get("siteId"), (String) map.get("account")));
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