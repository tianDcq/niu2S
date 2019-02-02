package com.micro.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.micro.frame.GameMain;
import com.micro.frame.Player;
import com.micro.frame.Role;
import com.micro.frame.Room;
import com.micro.frame.Table;
import com.micro.frame.socket.ErrRespone;
import com.micro.frame.socket.Request;
import com.micro.frame.socket.Response;

import lombok.Getter;

class HCPlayer extends Player implements HCRoleInterface {
    public @Getter ChipStruct[] chipList = new ChipStruct[8];

    public void onMsg(Request req) {
        Map<String, Object> map = req.msg;
        String msgType = (String) map.get("msgType");
        switch (msgType) {
        case "2001": {
            if (hall != null && table == null) {
                Object roomId = map.get("roomId");
                hall.enterRoom(this, (String) roomId);
            }
            break;
        }
        case "2019": {
            Response mm = new Response(2019, 1);
            Map<String, Object> msg = new HashMap<>();
            Map<String, Object> selfData = new HashMap<>();
            selfData.put("coins", money);
            HashMap<String, Room> rooms = hall.getRoomMgr().getRooms();
            Object[] roomData = new Object[rooms.size()];
            int i = 0;
            for (Map.Entry<String, Room> entry : rooms.entrySet()) {
                Room room = entry.getValue();
                Map<String, Object> roomConfig = room.getRoomConfig();
                HCTable table = (HCTable) room.getTableMgr().getTables().get("0");
                roomConfig.put("currentPlayer", room.getRoles().size());
                Map<String, Object> phaseData = new HashMap<>();
                phaseData.put("status", table.getGameStae());
                phaseData.put("time", table.getTime());
                roomConfig.put("phaseData", phaseData);
                // 明天平一个历史
                roomData[i] = roomConfig;
            }
            msg.put("roomData", roomData);
            mm.msg = msg;
            sendMsg(mm);
            break;
        }
        case "2010": {
            if (chipList.length > 0) {
                ErrRespone msg = new ErrRespone(2010, 0, "已经下注不能退出");
                sendMsg(msg);
                return;
            } else if (((HCTable) table).getGameStae() == 0) {
                ErrRespone msg = new ErrRespone(2010, 0, "已经开奖不能退出");
                sendMsg(msg);
                return;
            }
            ((HCTable) table).exit(this);
            break;
        }
        case "2009": {
            ((HCTable) table).playerUpBanker(this);
            break;
        }
        case "2002": {
            ((HCTable) table).playerChip(this, map);
            break;
        }
        case "2011": {
            ((HCTable) table).playerDownBanker(this);
        }
        case "2018": {
            ((HCTable) table).requstTableScene(this);
        }

        }
    }
}