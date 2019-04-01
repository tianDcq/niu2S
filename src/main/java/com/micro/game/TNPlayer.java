package com.micro.game;

import frame.Callback;
import frame.Config;
import frame.Timer;
import frame.UtilsMgr;
import frame.socket.ErrResponse;
import frame.socket.Request;
import frame.socket.Response;
import frame.socket.common.proto.LobbySiteRoom.BetRoomCfg;
import frame.socket.common.proto.LobbySiteRoom.PkRoomCfg;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import frame.game.*;

import com.micro.game.TowNiuMessage.*;

@Slf4j
class TNPlayer extends Player implements TNRoleInterface {
    public @Getter @Setter int sit;
    public @Getter @Setter long win;
    public @Getter @Setter int bankNum;
    public @Getter @Setter int chipNum;
    public @Getter @Setter int cow;
    public @Getter @Setter List<Integer> cards;
    public @Getter @Setter int playerState; // 1叫 2 叫完 3选分 4等待选分 5开牌 6等待 0未匹配
    private Timer kickTimer;

    @Override
    protected void onInit() {
        playerState = 0;
    }

    @Override
    public void onMsg(Request req) {
        switch (req.msgType) {
        case TwoNiuConfig.ReqRooms: {
            ResRooms.Builder resBulid = ResRooms.newBuilder();
            resBulid.setMoney(money);
            HashMap<Integer, Room> rooms = hall.getRoomMgr().getRooms();
            for (Room room : rooms.values()) {
                PkRoomCfg roomConfig = room.getPkRoomCfg();
                ResRooms.roomConfig.Builder configBuild = ResRooms.roomConfig.newBuilder();
                configBuild.setRoomName(roomConfig.getRoomName());
                configBuild.setRoomId(roomConfig.getId());
                configBuild.setPlayers(room.getRoles().size());
                configBuild.setMinMoney((int) roomConfig.getMinMoney());
                configBuild.setBaseMoney((int) roomConfig.getRoomField());
                configBuild.setRoomType(roomConfig.getRoomType());
                resBulid.addRooms(configBuild);
            }
            send(new Response(TwoNiuConfig.ResRooms, resBulid.build().toByteArray()));
            break;
        }
        case TwoNiuConfig.ReqEnter: {
            try {
                int roomId = ReqEnter.parseFrom(req.protoMsg).getRoomId();
                PkRoomCfg roomConfig = hall.getRoomMgr().getRooms().get(roomId).getPkRoomCfg();
                if (money < roomConfig.getMinMoney()) {
                    send(new ErrResponse(TwoNiuConfig.ReqEnter, "钱不够"));
                    break;
                } else if (this.enterRoom(roomId) == Config.ERR_SUCCESS) {
                    break;
                }
                saveReconnectState(false);
                send(new Response(TwoNiuConfig.ResEnter, ResEnter.newBuilder().setEnter(false).build().toByteArray()));
            } catch (Exception e) {
                // TODO: handle exception
            }

            break;
        }
        case TwoNiuConfig.ReqTableInfo: {
            if (table == null) {
                PkRoomCfg cfg = room.getPkRoomCfg();
                ResTableInfo.Builder res = ResTableInfo.newBuilder();
                res.setGameState(0);
                ResTableInfo.timeConfig.Builder timec = ResTableInfo.timeConfig.newBuilder();
                timec.setPairTime(cfg.getStartTime());
                timec.setBankerTime(cfg.getCallTime());
                timec.setChipTime(cfg.getBetTime());
                timec.setShowTime(cfg.getBetTime());
                res.setTimeCf(timec);
                send(new Response(TwoNiuConfig.ResTableInfo, res.build().toByteArray()));
            } else {
                ((TNTable) table).getUpdateTable(this);
            }
            break;
        }
        case TwoNiuConfig.ReqPair: {
            if (room != null) {
                long min = room.getPkRoomCfg().getMinMoney();
                if (playerState != 0) {
                    return;
                } else if (money < min) {
                    send(new ErrResponse(TwoNiuConfig.ReqExitRoom, "金币不足" + min + "不能继续在此房间游戏"));
                    return;
                }
                pair();
                if (kickTimer != null) {
                    kickTimer.stop();
                    kickTimer = null;
                }
            }
            break;
        }

        case TwoNiuConfig.ReqBanker: {
            try {
                if (playerState == 1) {
                    ((TNTable) table).playerBanker(this, ReqBanker.parseFrom(req.protoMsg).getBankerNum());
                } else {
                    ErrResponse res = new ErrResponse("不在叫庄阶段");
                    send(res);
                }
            } catch (Exception e) {
                log.info("叫装 ", e);
            }
            break;
        }

        case TwoNiuConfig.ReqBet: {
            try {
                if (playerState == 3) {
                    ((TNTable) table).playerChip(this, ReqBet.parseFrom(req.protoMsg).getBet());
                }
            } catch (Exception e) {
                // TODO: handle exception
            }

            break;
        }
        case TwoNiuConfig.ReqShowCard: {
            if (playerState == 5) {
                ((TNTable) table).playerOpen(this);
            }
            break;
        }
        case TwoNiuConfig.ReqExitRoom: {
            if (playerState == 6 || playerState == 0) {
                exitRoom();
            } else {
                send(new ErrResponse(TwoNiuConfig.ReqExitRoom, 1, "打完了再退要死啊"));
            }
            break;
        }

        }
    }

    /**
     * 发送游戏纪录详情
     * 
     * @param game 数据库里面的游戏信息
     */
    @Override
    protected void onEnterRoom() {
        send(new Response(TwoNiuConfig.ResEnter, ResEnter.newBuilder().setEnter(true).build().toByteArray()));
        ReadyKick();
    }

    private void ReadyKick() {
        if (kickTimer != null) {
            kickTimer.stop();
        }
        kickTimer = UtilsMgr.getTaskMgr().createTimer(60, new Callback() {

            @Override
            public void func() {
                send(new ErrResponse("长时间不准备踢出房间"));
                exitRoom();
            }
        });
    }

    @Override
    protected void onExitRoom() {
        send(new Response(TwoNiuConfig.ResExitRoom, ResExitRoom.newBuilder().build().toByteArray()));
        if (kickTimer != null) {
            kickTimer.stop();
            kickTimer = null;
        }
    }

    @Override
    protected void onEnterTable() {
        sit = 0;
        win = 0;
        chipNum = -1;
        cards = new ArrayList<>();
        playerState = 0;
    }

    @Override
    public void endGame() {
        ReadyKick();
    }

    @Override
    public void onConnected() {
        ResRooms.Builder resBulid = ResRooms.newBuilder();
        resBulid.setMoney(money);
        HashMap<Integer, Room> rooms = hall.getRoomMgr().getRooms();
        for (Room room : rooms.values()) {
            PkRoomCfg roomConfig = room.getPkRoomCfg();
            ResRooms.roomConfig.Builder configBuild = ResRooms.roomConfig.newBuilder();
            configBuild.setRoomName(roomConfig.getRoomName());
            configBuild.setRoomId(roomConfig.getId());
            configBuild.setPlayers(room.getRoles().size());
            configBuild.setMinMoney((int) roomConfig.getMinMoney());
            configBuild.setBaseMoney((int) roomConfig.getRoomField());
            configBuild.setRoomType(roomConfig.getRoomType());
            resBulid.addRooms(configBuild);
        }
        send(new Response(TwoNiuConfig.ResRooms, resBulid.build().toByteArray()));
    }

    @Override
    public void onDisconnect() {
        log.info("msg");
    }

    @Override
    public void onReconnect() {
        log.info("msg");
        this.enterRoom();
    }

    @Override
    protected void onExitTable() {

    }

    @Override
    public boolean isDisconnectKickOut() {
        return true;
    }
}