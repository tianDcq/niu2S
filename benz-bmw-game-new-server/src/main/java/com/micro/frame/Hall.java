package com.micro.frame;

import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Hall {
    private @Getter RoomMgr roomMgr = new RoomMgr();
    private @Getter HashMap<String, Role> roles = new HashMap<String, Role>();

    public void enter(Role role) {
        roles.put(role.uniqueId, role);
        role.enterHall(this);
    };

    public void enterRoom(Role role, String id) {
        roles.remove(role.uniqueId);
        roomMgr.getRooms().get(id).enter(role);
    }

    public void doStop() {
        roomMgr.doStop();
        for (Role role : roles.values()) {
            role.doStop();
        }
    }

    public void doDestroy() {
        roomMgr.doDestroy();
        for (Role role : roles.values()) {
            role.doDestroy();
        }
    }

    public void doTerminate() {
        roomMgr.doTerminate();
        for (Role role : roles.values()) {
            role.doTerminate();
        }
    }
}