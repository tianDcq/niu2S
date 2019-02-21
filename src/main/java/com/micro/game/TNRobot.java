package com.micro.game;

import java.util.List;
import java.util.Map;

import frame.Callback;
import frame.GameMain;
import frame.Robot;
import frame.socket.BaseResponse;
import frame.socket.Response;
import frame.util.RandomUtil;
import lombok.Getter;
import lombok.Setter;

class TNRobot extends Robot implements TNRoleInterface {
    public @Getter @Setter int bankNum;
    public @Getter @Setter long win;
    public @Getter @Setter int sit;
    public @Getter @Setter int chipNum;
    public @Getter @Setter List<Integer> cards;
    public @Getter @Setter int playerState; // 1叫 2 叫完 3选分 4等待选分 5开牌 6等待 0未匹配

    @Override
    public void endGame() {

    }

    @Override
    public void send(BaseResponse res) {
        if (res.msgType.equals("8015")) {
            if (res.status.equals("1")) {
                Map<String, Object> msg = ((Response) res).msg;
                int stage = (int) msg.get("stage");
                if (stage == 1) {
                    int time = RandomUtil.ramdom(((TNTable) table).bankTime - 3) + 2;
                    GameMain.getInstance().getTaskMgr().createTimer(time, new Callback() {

                        @Override
                        public void func() {
                            banker();
                        }
                    });
                } else if (stage == 2) {
                    int time = RandomUtil.ramdom(((TNTable) table).chipTime - 3) + 2;
                    GameMain.getInstance().getTaskMgr().createTimer(time, new Callback() {

                        @Override
                        public void func() {
                            chip();
                        }
                    });
                } else if (stage == 3) {
                    int time = RandomUtil.ramdom(3) + 2;
                    GameMain.getInstance().getTaskMgr().createTimer(time, new Callback() {

                        @Override
                        public void func() {
                            open();
                        }
                    });
                }
            }
        }
    }

    public void banker() {
        if(table!=null){
            ((TNTable) table).playerBanker(this, 0);
        }
    }

    public void chip() {
        if(table!=null){
            ((TNTable) table).playerChip(this, 0);
        }
    }

    public void open() {
        if(table!=null){
            ((TNTable) table).playerOpen(this);
        }
    }
}