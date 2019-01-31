package com.micro.frame;

import lombok.Getter;

import java.util.HashSet;
import java.util.Map;

public class Room {
    private @Getter TableMgr tableMgr;
    private @Getter HashSet<Role> roles;
    private @Getter Map<String, Object> RoomConfig;

    public void enter(Role role) {

    }
}