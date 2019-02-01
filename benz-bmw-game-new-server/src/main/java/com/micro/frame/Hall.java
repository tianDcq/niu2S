package com.micro.frame;

import lombok.Getter;
import java.util.HashSet;
import java.util.Map;

public class Hall {
    private @Getter RoomMgr roomMgr;
    private @Getter Map<String, Role> roles;

    public void enter(Role role) {
        roles.put(role.uniqueId, role);
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