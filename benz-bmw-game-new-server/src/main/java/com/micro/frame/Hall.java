package com.micro.frame;

import lombok.Getter;
import java.util.HashSet;

public class Hall {
    private @Getter RoomMgr roomMgr;
    private @Getter HashSet<Role> roles;

    public void enter(Role role) {

    };

    public void enterRoom(Role role, String id) {
        roles.remove(role);
        roomMgr.getRooms().get(id).enter(role);
    }
}