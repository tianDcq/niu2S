package com.micro.frame;

import lombok.Getter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room {
    private @Getter Hall hall;
    public long roomId;

    public enum PairStatus {
        Success, Failed
    }

    private @Getter TableMgr tableMgr = new TableMgr();
    private @Getter HashMap<String, Role> roles = new HashMap<>();
    private @Getter ArrayDeque<Robot> waitRobots = new ArrayDeque<>(100);
    private @Getter Map<String, Object> RoomConfig = new HashMap<>();

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
        if (table.pair(role) == Config.ERR_SUCCESS) {
            this.exit(role);
            return PairStatus.Success;
        }
        return PairStatus.Failed;
    }

    Robot getRobot() {
        Robot robot = waitRobots.pollLast();
        if (robot == null) {
            robot = GameMain.getInstance().getRoleMgr().createRobot();
            robot.init();
        }

        return robot;
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