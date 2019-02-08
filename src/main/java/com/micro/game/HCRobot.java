package com.micro.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import frame.Callback;
import frame.GameMain;
import frame.Robot;
import frame.socket.BaseResponse;
import frame.socket.Response;

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
    public void send(BaseResponse res) {
        String msgType = res.msgType;
        if (msgType.equals("2012")) {
            int state = (int) ((Response) res).msg.get("betable");
            if (state == 1) {
                // 下注
                GameMain game = GameMain.getInstance();
                if (((HCTable) table).getBanker() != this) {
                    if (money > minChip) {
                        long mm = money - minChip;
                        long ii = maxChip - minChip;
                        mm = mm > ii ? ii : mm;
                        final long chipMoney = (long) (Math.random() * mm);
                        int time = (int) (Math.random() * (chipTime - 3)) + 3;

                        int p = (int) (Math.random() * 8);
                        game.getTaskMgr().createTimer(time, new Callback() {
                            @Override
                            public void func() {
                                chip(p, chipMoney);
                            }
                        }, this);
                    }
                    if (allowBanker) {
                        if (!((HCTable) table).getBankerList().contains(this)) {
                            int time = (int) (Math.random() * bankerTime);
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
    protected void onEnterTable() {
        Map<String, Object> roomConfig = room.getRoomConfig();
        minChip = Integer.valueOf((String) roomConfig.get("bottomRed1")) * 100;
        maxChip = Integer.valueOf((String) roomConfig.get("bottomRed2")) * 100;
        chipTime = Integer.valueOf((String) roomConfig.get("betTime"));
        bankerTime = (int) chipTime + Integer.valueOf((String) roomConfig.get("betTime"));
        boolean contor = false;
        if (roomConfig.get("shangzhuangSwitch") != null) {
            contor = (int) roomConfig.get("shangzhuangSwitch") == 1;
        }

        boolean sys = (int) roomConfig.get("sysGold") == 1;
        allowBanker = contor && sys;
        bankerMoney = Integer.valueOf((String) roomConfig.get("bankerCond"));
        money = (int) (Math.random() * 50000) + minChip;
    }

    @Override
    protected void onInit() {
        for (int i = 0; i < 8; ++i) {
            chipList[i] = new ChipStruct(i);
        }
    }

    private void upBanker() {
        ((HCTable) table).playerUpBanker(this);
    }

    private void chip(int tar, long chip) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> chipInfo = new HashMap<>();
        chipInfo.put("betTarget", tar);
        chipInfo.put("betAmount", (int) chip);
        list.add(chipInfo);
        map.put("betInfo", list);
        map.put("gameIndex", ((HCTable) table).getGameIndex());
        ((HCTable) table).playerChip(this, map);
    }
}