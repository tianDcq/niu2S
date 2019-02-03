package com.micro.frame;

import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.micro.frame.socket.Response;

public class Hall {
    private @Getter RoomMgr roomMgr;
    private @Getter HashMap<String, Role> roles = new HashMap<String, Role>();

    Hall() {
        roomMgr = new RoomMgr();
        roomMgr.setHall(this);
    }

    public void enter(Role role) {
        roles.put(role.uniqueId, role);
    };

    void exit(Role role) {
        roles.remove(role.uniqueId);
    }

    void enterRoom(Role role, String id) {

        roomMgr.getRooms().get(id).enter(role);
    }

    public void senToAll(Response msg) {
        for (Role role : roles.values()) {
            role.send(msg);
        }
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