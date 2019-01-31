package com.micro.game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.micro.frame.*;
import com.micro.old.server.vo.common.Response;

import lombok.Getter;

final class HCTable extends Table {
    private HashSet<Role> roles;
    public boolean upBanker;
    private @Getter int time;
    private @Getter int gameStae;
    private int openTime;
    private int waitTime;
    private int chipTime;

    public HCTable(float time) {
        super(time);
        upBanker = true;
        Map<String, Object> roomConfig = room.getRoomConfig();
        openTime = (int) roomConfig.get("openTime");
        waitTime = (int) roomConfig.get("waitTime");
        chipTime = (int) roomConfig.get("chipTime");
    }

    public void addRole(Role role) {
        roles.add(role);
        Response ownMsg = new Response();
        ownMsg.msgType = "2001";
        ownMsg.status = "1";

        Response mm = new Response();
        mm.msgType = "2007";
        mm.status = "1";
        Map<String, Object> msg = new HashMap<>();
        msg.put("playerName", role.nickName);
        msg.put("playerCoins", role.money);
        msg.put("portrait", role.portrait);
        msg.put("token", role.token);
        msg.put("uniqueId", role.uniqueId);
        mm.msg = msg;
        pushMsgToOther(mm, ownMsg, role.uniqueId);
    };

    public void removeRole(Role role) {

    };

    public HashSet<Role> getRoles() {
        return roles;
    };

    public void start() {

    };

    public void plaerLeave(String id) {
        if (gameStae == 1) {

        }
    };

    public void pushMsgToOther(Response otherMsg, Response ownMsg, String oId) {
        for (Role rr : roles) {
            String id = rr.uniqueId;
            if (oId.equals(id)) {
                rr.sendMsg(ownMsg);
            } else {
                rr.sendMsg(otherMsg);
            }
        }
    }
}