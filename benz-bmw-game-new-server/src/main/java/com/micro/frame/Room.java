package com.micro.frame;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class Room {
    public enum PairStatus {
        Success, Failed
    }

    private @Getter TableMgr tableMgr;
    private @Getter HashMap<String, Role> roles;
    private @Getter Map<String, Object> RoomConfig;

    public boolean enter(Role role) {
        roles.put(role.uniqueId, role);
        role.enterRoom(this);
        onEnter(role);
        return true;
    }

    private void exit(Role role) {
        roles.remove(role.uniqueId);
        role.exitRoom();
        onExit(role);
    }

    public PairStatus pair(Role role) {
        Table table = tableMgr.getWait();
        if (table.pair(role)) {
            this.exit(role);
            return PairStatus.Success;
        }
        return PairStatus.Failed;
    }

    protected void onEnter(Role role) {

    }

    protected void onExit(Role role) {

    }

    void doStop() {
        tableMgr.doStop();
        for (Role role : roles.values()) {
            role.doStop();
        }
    }

    void doTerminate() {
        tableMgr.doTerminate();
        for (Role role : roles.values()) {
            role.doTerminate();
        }
    }

    void doDestroy() {
        tableMgr.doDestroy();
        for (Role role : roles.values()) {
            role.doDestroy();
        }
    }
}