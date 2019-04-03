package com.micro.game;

import java.util.ArrayList;
import java.util.List;

import com.micro.game.TowNiuMessage.*;

import frame.Callback;
import frame.UtilsMgr;
import frame.log;
import frame.game.*;
import frame.game.RobotActions.PkRobot.RobotPkLogic;
import frame.socket.BaseResponse;
import frame.util.RandomUtil;
import lombok.Getter;
import lombok.Setter;

class TNRobot extends RobotPkLogic implements TNRoleInterface {
    public @Getter @Setter int bankNum;
    public @Getter @Setter long win;
    public @Getter @Setter int cow;
    public @Getter @Setter int sit;
    public @Getter @Setter int chipNum;
    public @Getter @Setter List<Integer> cards;
    public @Getter @Setter int playerState; // 1叫 2 叫完 3选分 4等待选分 5开牌 6等待 0未匹配

    @Override
    public void endGame() {

    }

    @Override
    public void send(BaseResponse res) {
        try {
            if (res.msgType == TwoNiuConfig.ResStart) {
                if (ResStart.parseFrom(res.protoMsg).getCurrPos() == sit) {
                    readyBanker();
                }
            } else if (res.msgType == TwoNiuConfig.ResBnaker) {
                if (ResBnaker.parseFrom(res.protoMsg).getCurrPos() == sit) {
                    readyBanker();
                }
            } else if (res.msgType == TwoNiuConfig.ResBetProd) {
                if (ResBetProd.parseFrom(res.protoMsg).getCurrPos() == sit) {
                    int time = RandomUtil.ramdom(((TNTable) table).chipTime - 3) + 2;
                    UtilsMgr.getTaskMgr().createTimer(time, new Callback() {

                        @Override
                        public void func() {
                            chip();
                        }
                    });
                }
            } else if (res.msgType == TwoNiuConfig.ResDisCards) {
                int time = RandomUtil.ramdom(5) + 5;
                UtilsMgr.getTaskMgr().createTimer(time, new Callback() {

                    @Override
                    public void func() {
                        open();
                    }
                });
            }

        } catch (Exception e) {
            log.error("什么机器人   ", e);
        }
    }

    private void readyBanker() {
        int time = RandomUtil.ramdom(((TNTable) table).bankTime - 3) + 2;
        UtilsMgr.getTaskMgr().createTimer(time, new Callback() {

            @Override
            public void func() {
                banker();
            }
        });
    }

    @Override
    protected void onEnterTable() {
        sit = 0;
        win = 0;
        chipNum = -1;
        cards = new ArrayList<>();
        playerState = 1;
    }

    public void banker() {
        if (table != null) {

            ((TNTable) table).playerBanker(this, RandomUtil.ramdom(3));
        }
    }

    public void chip() {
        if (table != null) {
            ((TNTable) table).playerChip(this, 0);
        }
    }

    public void open() {
        if (table != null) {
            ((TNTable) table).playerOpen(this);
        }
    }
}