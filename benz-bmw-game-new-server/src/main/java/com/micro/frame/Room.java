package com.micro.frame;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room {
    private @Setter @Getter Hall hall;

    public enum PairStatus {
        Success, Failed
    }

    private @Getter TableMgr tableMgr;
    private @Getter HashMap<String, Role> roles = new HashMap<>();
    private @Getter ArrayDeque<Robot> waitRobots = new ArrayDeque<>(100);
    private @Getter Map<String, Object> roomConfig;

    Room() {
        tableMgr = new TableMgr();
        tableMgr.setRoom(this);
    }

    void init(Map<String, Object> roomConfig) {
        this.roomConfig = roomConfig;
        prepareTable();
    }

    void prepareTable() {
        if (GameMain.getInstance().getGameMgr().getRobotPairType().type == Config.RobotPairType.Type.One) {
            tableMgr.getWait().pair();
        }
    }

    public boolean enter(Role role) {
        roles.put(role.uniqueId, role);
        role.enterRoom(this);
        onEnter(role);

        if (GameMain.getInstance().getGameMgr().getRobotPairType().type == Config.RobotPairType.Type.One) {
            pair(role);
        }

        return true;
    }

    protected Config.Error exit(Role role) {
        roles.remove(role.uniqueId);
        role.exitRoom();
        onExit(role);
        if (role instanceof Robot) {
            waitRobots.addLast((Robot) role);
        }
        return Config.ERR_SUCCESS;
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