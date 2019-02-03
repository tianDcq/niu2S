package com.micro.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.micro.frame.Callback;
import com.micro.frame.GameMain;
import com.micro.frame.Robot;
import com.micro.frame.socket.Response;

import lombok.Getter;

class HCRobot extends Robot implements HCRoleInterface {
    public @Getter ChipStruct[] chipList = new ChipStruct[8];
    private int minChip;
    private int maxChip;
    private int chipTime;
    private int bankerTime;
    public boolean allowBanker;
    public int bankerMoney;

    @Override
    public void endGame() {
        for (int i = 0; i < 8; ++i) {
            chipList[i].betAmount = 0;
        }
    }

    @Override
    public void sendMsg(Response res) {
        String msgType = res.msgType;
        if (msgType == "2012") {
            int state = (int) res.msg.get("betable");
            if (state == 1) {
                // 下注
                GameMain game = GameMain.getInstance();
                if (((HCTable) table).getBanker() != this) {
                    if (money > minChip) {
                        long mm = money - minChip;
                        long ii = maxChip - minChip;
                        mm = mm > ii ? ii : mm;
                        final long chipMoney = (long) Math.random() * mm;
                        int time = (int) Math.random() * chipTime;

                        int p = (int) (Math.random() * 9);
                        game.getTaskMgr().createTimer(time, new Callback() {
                            @Override
                            public void func() {
                                chip(p, chipMoney);
                            }
                        }, this);
                    }
                    if (allowBanker) {
                        if (!((HCTable) table).getBankerList().contains(uniqueId)) {
                            int time = (int) Math.random() * bankerTime;
                            game.getTaskMgr().createTimer(time, new Callback() {

                                @Override
                                public void func() {
                                    upBanker();
                                }
                            }, this);
                        }
                    }

                }

            }
        }
    }

    @Override
    protected void onInit() {
        Map<String, Object> roomConfig = room.getRoomConfig();
        minChip = (int) roomConfig.get("bottomRed1");
        maxChip = (int) roomConfig.get("bottomRed2");
        chipTime = (int) roomConfig.get("betTime");
        bankerTime = (int) chipTime + (int) roomConfig.get("freeTime");
        // boolean contor = (int) roomConfig.get("shangzhuangSwitch") == 1;
        boolean contor = true;
        boolean sys = (int) roomConfig.get("sysGold") == 1;
        allowBanker = contor && sys;
        bankerMoney = (int) roomConfig.get("bankerCond");
    }

    private void upBanker() {
        ((HCTable) table).playerUpBanker(this);
    }

    private void chip(int tar, long money) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> chipInfo = new HashMap<>();
        chipInfo.put("betTarget", tar);
        chipInfo.put("betAmount", money);
        list.add(chipInfo);
        map.put("betInfo", list);
        ((HCTable) table).playerChip(this, map);
    }
}