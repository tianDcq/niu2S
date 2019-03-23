package com.micro.game;

import frame.Config;
import frame.UtilsMgr;
import frame.socket.ErrResponse;
import frame.socket.Request;
import frame.socket.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                Map<String, Object> roomConfig = room.getRoomConfig();
                ResRooms.roomConfig.Builder configBuild = ResRooms.roomConfig.newBuilder();
                configBuild.setRoomName((String) roomConfig.get("roomName"));
                configBuild.setRoomId((int) (roomConfig.get("gameRoomId")));
                configBuild.setPlayers(room.getRoles().size());
                configBuild.setBanker(false);
                configBuild.setMinMoney((int) roomConfig.get("minMoney"));
                configBuild.setBaseMoney(555*1000);
                configBuild.setRoomType(1);
                resBulid.addRooms(configBuild);
            }
            send(new Response(TwoNiuConfig.ResRooms, resBulid.build().toByteArray()));
            break;
        }
        case TwoNiuConfig.ReqEnter: {
            try {
                int roomId = ReqEnter.parseFrom(req.protoMsg).getRoomId();
                if (this.enterRoom(roomId) == Config.ERR_SUCCESS) {
                    break;
                }
                saveReconnectState(false);
                send(new Response(TwoNiuConfig.ResEnter,ResEnter.newBuilder().setEnter(false).build().toByteArray()));
            } catch (Exception e) {
                // TODO: handle exception
            }

            break;
        }
        case TwoNiuConfig.ReqTableInfo: {
            if (table == null) {
                Map<String, Object> roomConfig = room.getRoomConfig();
                ResTableInfo.Builder res = ResTableInfo.newBuilder();
                res.setGameState(0);
                ResTableInfo.timeConfig.Builder timec = ResTableInfo.timeConfig.newBuilder();
                timec.setPairTime((int) roomConfig.get("callTime"));
                timec.setBankerTime((int) roomConfig.get("betTime"));
                timec.setChipTime((int) roomConfig.get("betTime"));
                timec.setShowTime((int) roomConfig.get("betTime"));
                res.setTimeCf(timec);
                send(new Response(TwoNiuConfig.ResTableInfo, res.build().toByteArray()));
            } else {
                ((TNTable) table).getUpdateTable(this);
            }
            break;
        }
        case TwoNiuConfig.ReqPair: {
            if (playerState != 0) {
                return;
            }
            pair();
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
                log.info("叫装 ",e);
            }
            break;
        }

        case TwoNiuConfig.ReqBet: {
            try {
                if (playerState == 3) {
                    ((TNTable) table).playerChip(this, ReqBet.parseFrom(req.protoMsg).getBet());
                }
            } catch (Exception e) {
                //TODO: handle exception
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
            }else{
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
        send(new Response(TwoNiuConfig.ResEnter,ResEnter.newBuilder().setEnter(true).build().toByteArray()));
    }

    @Override
    protected void onExitRoom() {
        send(new Response(TwoNiuConfig.ResExitRoom,ResExitRoom.newBuilder().build().toByteArray()));
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
    }

    @Override
    public void onConnected() {
        ResRooms.Builder resBulid = ResRooms.newBuilder();
        resBulid.setMoney(money);
        HashMap<Integer, Room> rooms = hall.getRoomMgr().getRooms();
        for (Room room : rooms.values()) {
            Map<String, Object> roomConfig = room.getRoomConfig();
            ResRooms.roomConfig.Builder configBuild = ResRooms.roomConfig.newBuilder();
            configBuild.setRoomName((String) roomConfig.get("roomName"));
            configBuild.setRoomId((int) (roomConfig.get("gameRoomId")));
            configBuild.setPlayers(room.getRoles().size());
            configBuild.setBanker(false);
            configBuild.setMinMoney(1000);
            configBuild.setBaseMoney(555*1000);
            configBuild.setRoomType(1);
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