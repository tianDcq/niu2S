package com.micro.frame;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.micro.frame.http.AccountFeignClient;
import com.micro.frame.http.GameFeignClient;
import com.micro.frame.util.SpringUtil;

import org.bouncycastle.jcajce.provider.digest.GOST3411.HashMac;

import cn.hutool.core.lang.Console;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class Register {
    private static GameFeignClient gameFeignClient;
    private static AccountFeignClient accountFeignClient;

    static void calls() {
        gameFeignClient = SpringUtil.getBean(GameFeignClient.class);
        accountFeignClient = SpringUtil.getBean(AccountFeignClient.class);

        CallRegisterMgr callMgr = GameMain.getInstance().getCallRegisterMgr();

        callMgr.register("/game/getWildGameRoomConfigVo", new CallbackFactory() {

            @Override
            public Callback create() {
                return new Callback() {
                    @Override
                    public void func() {
                        Map map = (Map) this.getData();
                        this.setData(gameFeignClient.getWildGameRoomConfigVo((Long) map.get("siteId"),
                                (Integer) map.get("gameId")));
                    }
                };
            }
        });

        callMgr.register("/game/getWildGameRoomConfigVo2", new CallbackFactory() {

            @Override
            public Callback create() {
                return new Callback() {
                    @Override
                    public void func() {
                        Map map = (Map) this.getData();
                        this.setData(gameFeignClient.getAllSiteGame((Integer) map.get("gameId")));
                    }
                };
            }
        });

        callMgr.register("/acc/getPlayer", new CallbackFactory() {

            @Override
            public Callback create() {
                return new Callback() {
                    @Override
                    public void func() {
                        Map map = (Map) this.getData();
                        long siteId = ((Long) map.get("siteId")).longValue();
                        System.out.println("请求玩家数据");
                        Object obj = accountFeignClient.getPlayer(siteId, (String) map.get("account"));
                        System.out.println("数据接收成功");
                        this.setData(obj);
                    }
                };
            }
        });

        callMgr.register("/acc/addMoney", new CallbackFactory() {

            @Override
            public Callback create() {
                return new Callback() {
                    @Override
                    public void func() {
                        Map map = (Map) this.getData();

                        this.setData(accountFeignClient.addMoney((Long) map.get("siteId"), (String) map.get("account"),
                                (BigDecimal) map.get("money")));
                    }
                };
            }
        });
    }
}