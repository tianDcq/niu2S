package com.micro.game;

import java.util.HashMap;
import java.util.Map;

import com.micro.frame.Player;
import com.micro.frame.Room;
import com.micro.frame.socket.ErrRespone;
import com.micro.frame.socket.Request;
import com.micro.frame.socket.Response;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
class HCPlayer extends Player implements HCRoleInterface {
    public @Getter ChipStruct[] chipList = new ChipStruct[8];

    @Override
    protected void onInit() {
        for (int i = 0; i < 8; ++i) {
            chipList[i] = new ChipStruct(i);
        }
    }

    @Override
    public void onMsg(Request req) {
        Map<String, Object> map = req.msg;
        String msgType = (String) map.get("msgType");
        switch (msgType) {
        case "2001": {

            Object roomId = map.get("roomId");
            //此处包kong
            if(roomId==null){
                log.error("roomId为空=========================");
            }
            this.enterRoom(roomId.toString());
            break;
        }
        case "2019": {
            Response mm = new Response(2019, 1);
            Map<String, Object> msg = new HashMap<>();
            Map<String, Object> selfData = new HashMap<>();
            selfData.put("coins", money);
            msg.put("selfData", selfData);
            HashMap<String, Room> rooms = hall.getRoomMgr().getRooms();
            Object[] roomData = new Object[rooms.size()];
            int i = 0;
            for (Map.Entry<String, Room> entry : rooms.entrySet()) {
                Room room = entry.getValue();
                Map<String, Object> roomConfig = room.getRoomConfig();
                Map<String, Object> roomC = new HashMap<>();
                roomC.put("roomType", roomConfig.get("roomType"));
                roomC.put("roomId", room.getRoomConfig().get("gameRoomId"));
                roomC.put("roomName", roomConfig.get("roomName"));
                boolean bb=false;
                if(roomConfig.get("shangzhuangSwitch")!=null){
                    bb = (Integer) roomConfig.get("shangzhuangSwitch") == 1;
                }
                roomC.put("hostAble", bb);
                roomC.put("minBet", Integer.valueOf((String) roomConfig.get("bottomRed1")) * 100);
                roomC.put("maxBet", Integer.valueOf((String) roomConfig.get("bottomRed1")) * 100);

                HCTable table = (HCTable) room.getTableMgr().getTables().get(0);
                roomC.put("currentPlayer", room.getRoles().size());
                Map<String, Object> phaseData = new HashMap<>();
                phaseData.put("status", table.getGameStae());
                phaseData.put("restTime", table.getTime());
                roomC.put("phaseData", phaseData);
                roomC.put("history", table.history);
                roomData[i] = roomC;
                ++i;
            }
            msg.put("roomData", roomData);
            mm.msg = msg;
            send(mm);
            break;
        }
        case "2010": {
            if (checkChip()) {
                ErrRespone msg = new ErrRespone(2010, 0, "已经下注不能退出");
                send(msg);
                return;
            }
            this.exitRoom();
            break;
        }
        case "2009": {
            if(table!=null){
                ((HCTable) table).playerUpBanker(this);
            }
            break;
        }
        case "2002": {
            if(table!=null){
                ((HCTable) table).playerChip(this, map);
            }
            break;
        }
        case "2011": {
            if(table!=null){
                ((HCTable) table).playerDownBanker(this);
            }
            break;
        }
        case "2018": {
            if(table!=null){
                ((HCTable) table).requstTableScene(this);
            }
            break;
        }

        }
    }

    private boolean checkChip() {
        for (int i = 0; i < 8; ++i) {
            if (chipList[i].betAmount > 0) {
                return true;
            }
        }
        return false;
    };

    @Override
    public void endGame() {
        for (int i = 0; i < 8; ++i) {
            chipList[i].betAmount = 0;
        }
    }

    @Override
    protected void onDisconnect() {
        if (!checkChip()) {
            exitRoom();
        }
    }

    @Override
    protected void onExitTable() {
        save();
    }
}