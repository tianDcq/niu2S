package com.micro.game;

import frame.Callback;
import frame.GameMain;
import frame.Robot;
import frame.socket.BaseResponse;
import frame.socket.Response;
import frame.util.RandomUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class HCRobot extends Robot implements HCRoleInterface {
    public @Getter ChipStruct[] chipList = new ChipStruct[8];
    // private int minChip;
    // private int maxChip;
    // private int chipTime;
    // private int bankerTime;
    // public boolean allowBanker;
    // public long bankerMoney;
    // public int bankerSize;
    private final int[] chipL = { 100, 1000, 5000, 10000, 50000, 100000 };

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
                HCTable hcTable = (HCTable) table;
                int max = hcTable.maxChip;
                int min = hcTable.minChip;
                int chipTime = hcTable.chipTime;
                int bankerTime = chipTime * 2;
                int maxChip = chipL.length - 1;
                int minChip = 0;
                int bankerSize = hcTable.bankerSize;
                long bankerMoney = hcTable.bankMoney;
                boolean allowBanker = hcTable.allowBank && hcTable.sys;

                for (int i = 0; i < chipL.length; ++i) {
                    if (chipL[i] >= min) {
                        minChip = i;
                        break;
                    }
                }

                for (int i = chipL.length - 1; i > 0; --i) {
                    if (chipL[i] <= max) {
                        // 要是谁把这个配的比1还小就砍死他
                        maxChip = i;
                        break;
                    }
                }

                // 下注
                GameMain game = GameMain.getInstance();
                if (((HCTable) table).getBanker() != this) {
                    if (money > chipL[minChip]) {
                        int mm = chipL.length - 1;
                        for (; mm > minChip; --mm) {
                            if (money > chipL[mm]) {
                                break;
                            }
                        }
                        long chip = chipL[RandomUtil.ramdom(minChip, Math.min(mm, maxChip))];

                        int time = (int) (Math.random() * (chipTime - 3)) + 3;

                        int p = (int) (Math.random() * 8);
                        game.getTaskMgr().createTimer(time, new Callback() {
                            @Override
                            public void func() {
                                chip(p, chip);
                            }
                        }, this);
                    }
                    if (allowBanker && money > bankerMoney) {
                        if (((HCTable) table).getBankerList().size() < bankerSize) {
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
    }

    @Override
    protected void onEnterTable() {
        // Map<String, Object> roomConfig = room.getRoomConfig();
        // int max = Integer.valueOf((String) roomConfig.get("bottomRed1"))*100;
        // int min = Integer.valueOf((String) roomConfig.get("bottomRed2"))*100;
        // max=100000;
        // maxChip=chipL.length-1;
        // for (int i = 0; i < chipL.length; ++i) {
        // if (chipL[i] >= min) {
        // minChip = i;
        // }
        // if (chipL[i] > max) {
        // // 要是谁把这个配的比1还小就砍死他
        // maxChip = i - 1;
        // break;
        // }
        // }
        // chipTime = Integer.valueOf((String) roomConfig.get("betTime"));
        // bankerTime = (int) chipTime + Integer.valueOf((String)
        // roomConfig.get("betTime"));

        // boolean contor = false;
        // if (roomConfig.get("sysBanker") != null) {
        // contor = (int) roomConfig.get("sysBanker") == 1;
        // }

        // boolean sys = (int) roomConfig.get("sysGold") == 1;
        // allowBanker = contor && sys;
        // bankerMoney = Integer.valueOf((String) roomConfig.get("bankerCond"));
        // money = (int) (Math.random() * 50000) + minChip;
    }

    @Override
    protected void onInit() {
        for (int i = 0; i < 8; ++i) {
            chipList[i] = new ChipStruct(i);
        }
    }

    public long getChip() {
        long chip = 0;
        for (int i = 0; i < 8; ++i) {
            chip += chipList[i].betAmount;
        }
        return chip;
    };

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