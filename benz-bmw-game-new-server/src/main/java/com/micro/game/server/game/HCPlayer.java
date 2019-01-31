package com.micro.game.server.game;

import java.util.Map;

import com.micro.game.server.frame.GameMain;
import com.micro.game.server.frame.Player;
import com.micro.game.server.frame.Role;
import com.micro.game.server.frame.Room;
import com.micro.game.server.vo.common.Request;

class HCPlayer extends Player implements HCRoleInterface {
    public HCPlayer(String uniqueId) {
        super(uniqueId);
    }

    public void onMsg(Request req) {
        Map map = req.msg;
        if ((String) map.get("msgType") == "2001") {
            // 进入房间
            if (hall != null && table == null) {
                Object roomId = map.get("roomId");
                hall.playerToRoom(this, (String) roomId);
            }
        }
    }
}