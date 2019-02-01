package com.micro.frame;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class Room {
    private @Getter TableMgr tableMgr;
    private @Getter HashMap<String, Role> roles;
    private @Getter Map<String, Object> RoomConfig;

    public boolean enter(Role role) {
        onEnter(role);
        return true;
    }

    public void pair(Role role) {
        Table table = tableMgr.getWait();
        table.pair(role);
    }

    protected void onEnter(Role role) {

    }
}