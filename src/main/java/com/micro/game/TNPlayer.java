package com.micro.game;

import frame.Callback;
import frame.Config;
import frame.Player;
import frame.Room;
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

@Slf4j
class TNPlayer extends Player implements TNRoleInterface {
    public @Getter @Setter int sit;
    public @Getter @Setter int bankNum;
    public @Getter @Setter int chipNum;
    public @Getter @Setter List<Integer> cards;
    public @Getter @Setter int playerState;    //1叫  2 叫完 3选分 4等待选分 5开牌 6等待 0未匹配

    @Override
    protected void onInit() {
        playerState=0;
    }

    @Override
    public void onMsg(Request req) {
        Map<String, Object> map = req.msg;
        
        switch (req.type) {
        case 8001: {
            Response mm = new Response(8001, 1);
            Map<String, Object> msg = new HashMap<>();
            msg.put("myCoin", money);
            HashMap<String, Room> rooms = hall.getRoomMgr().getRooms();
            Object[] roomData = new Object[rooms.size()];
            int i = 0;
            for(Room room:rooms.values()){
                Map<String, Object> roomConfig = room.getRoomConfig();
                Map<String, Object> roomC = new HashMap<>();
                roomC.put("roomC", roomConfig.get("roomName"));
                roomC.put("roomId", roomConfig.get("gameRoomId"));
                roomC.put("onlineNum", room.getRoles().size());
                // roomC.put("allowToIn", roomConfig.get("minMoney"));
                roomC.put("allowToIn", 10000);
                roomC.put("fieldNum", roomConfig.get("gameRoomId"));
                roomData[i]=roomC;
                ++i;
            }
            msg.put("rooms", roomData);
            mm.msg = msg;
            send(mm);
            break;
        }

        case 8004: {
            Object roomId = map.get("fieldNum");
            if (roomId == null) {
                if (this.enterRoom() == Config.ERR_SUCCESS) {
                    break;
                }
            } else {
                if (this.enterRoom(roomId.toString()) == Config.ERR_SUCCESS) {
                    break;
                }
            }
            ErrResponse msg = new ErrResponse(Config.ERR_TABLE_DESTORY);
            saveReconnectState(false);
            send(msg);
            exitHall();
            break;
        }
        case 8019: {
            if(table==null){
                Response mm = new Response(8019, 1);
                Map<String, Object> msg = new HashMap<>();
                Map<String, Object> roomConfig = room.getRoomConfig();
                msg.put("myCoin",money);
                msg.put("callType",0);
                msg.put("isOpen",false);
                msg.put("isReady",false);
                msg.put("seatNum", uniqueId);
                msg.put("seatsInfo", new int[0]);
                Map<String, Object> stageTimerConfig = new HashMap<>();
                stageTimerConfig.put("callDealer", roomConfig.get("callTime"));
                stageTimerConfig.put("chipIn", roomConfig.get("betTime"));
                stageTimerConfig.put("dealPoker", roomConfig.get("betTime"));
                stageTimerConfig.put("ready", roomConfig.get("betTime"));
                msg.put("stageTimerConfig",stageTimerConfig);
                mm.msg=msg;
                send(mm);
            }else{
                ((TNTable)table).getUpdateTable(this);
            }
            break;
        }
        case 8020: {
            if(playerState!=0){
                return;
            }
            Response mm = new Response(8020, 1);
            Map<String, Object> msg = new HashMap<>();
            Map<String, Object> roomConfig = room.getRoomConfig();
            msg.put("waitTime",roomConfig.get("startTime"));
            mm.msg=msg;
            send(mm);
            pair();
            break;
        }

        case 8008: {
            if(playerState==1){
                ((TNTable)table).playerBanker(this, Integer.parseInt((String)map.get("callType")));
            }else{
                ErrResponse msg = new ErrResponse("不在叫庄阶段");
                send(msg);
            }
            break;
        }

        case 8010:{
            if(playerState==3){
                ((TNTable)table).playerChip(this,(int)map.get("chipInAmount"));
            }
            break;
        }
        case 8012:{
            if(playerState==5){
                ((TNTable)table).playerOpen(this);
            }
            break;
        }

        case 8017:{
            if(playerState==6||playerState==0){
                exitRoom();
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
    private void sendGameRecord(TNGameHistory game) {
        if (game != null) {
            Response recordMsg = new Response(2023, 1);
            Map<String, Object> msg = new HashMap<>();
            recordMsg.msg = msg;
            send(recordMsg);
        } else {
            ErrResponse msg = new ErrResponse("该局纪录丢失");
            send(msg);
        }
    }

    private void sendPlayerRecord(int curr, int count, List<TNGameHistory> games) {
        Response recordMsg = new Response(2022, 1);
        Map<String, Object> msg = new HashMap<>();
        msg.put("currentPage", curr);
        msg.put("totalPage", count);
        List<Map<String, Object>> gameRecord = new ArrayList<>();
        if (games != null) {
            for (TNGameHistory game : games) {
                Map<String, Object> gameinfo = new HashMap<>();
                gameinfo.put("id", game.gameId);
                gameinfo.put("roomName", game.roomName);
                gameinfo.put("endTime", game.endTime);
                gameinfo.put("result", game.wins.get(uniqueId));
                gameRecord.add(gameinfo);
            }
        }
        msg.put("gameRecord", gameRecord);
        recordMsg.msg = msg;
        send(recordMsg);
    }

    @Override
    protected void onEnterRoom() {
        Response mm = new Response(8004, 1);
        Map<String, Object> msg = new HashMap<>();
        Map<String, Object> roomConfig = room.getRoomConfig();
        msg.put("fieldNum",roomConfig.get("gameRoomId"));
        msg.put("tableNum","");
        mm.msg=msg;
        send(mm);
	}

    @Override
    public void endGame() {
    }

    @Override
    protected void onDisconnect() {
        if(playerState==0||playerState==6){
            exitRoom();
        }else if(playerState==5){
            ((TNTable)table).playerOpen(this);
            exitRoom();
        }
    }

    @Override
    protected void onExitTable() {

    }
}