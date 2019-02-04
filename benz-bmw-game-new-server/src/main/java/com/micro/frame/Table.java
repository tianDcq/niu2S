package com.micro.frame;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.micro.frame.socket.BaseRespone;
import com.micro.frame.socket.Response;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion.Static;

import lombok.Getter;
import lombok.Setter;

public abstract class Table extends Root {

    enum Status {
        Open, Pair, Game, End
    }

    public class Configs {
        public int pairTime;
        public int robotTime;
        public int max;
    }

    protected Configs configs;
    private @Getter Status status;

    protected Map<String, Role> roles = new HashMap<String, Role>();
    protected @Setter Room room;
    protected @Getter int id;

    private @Getter boolean canDestory = true;
    private Trigger destroyTrigger;
    private Timer robotTimer;
    private Timer pairTimer;

    public int maxRoles;

    void init(Map<String, Object> roomConfig) {
        status = Status.Open;
        // maxRoles = Integer.valueOf((String) roomConfig.get("roomPersons"));
        maxRoles = 100;

        onInit();
    }

    protected void onInit() {

    }

    Config.Error startPair() {

        Config.RobotPairType robotConfig = GameMain.getInstance().getGameMgr().getRobotPairType();
        if (robotConfig.type == Config.RobotPairType.Type.One) {
            int num = robotConfig.max - robotConfig.min;
            num = (int) Math.random() * num + robotConfig.min;
            for (int i = 0; i < num; ++i) {
                dragRobot();
            }
        } else if (robotConfig.type == Config.RobotPairType.Type.Fix
                || robotConfig.type == Config.RobotPairType.Type.Range) {

            if (robotTimer != null) {
                robotTimer.stop();
            }
            robotTimer = GameMain.getInstance().getTaskMgr().createTimer(configs.robotTime, new Callback() {

                void randomAddRobot() {

                }

                @Override
                public void func() {
                    randomAddRobot();
                }
            }, this);

            if (pairTimer != null) {
                pairTimer.stop();
            }

            pairTimer = GameMain.getInstance().getTaskMgr().createTimer(configs.pairTime, new Callback() {
                @Override
                public void func() {
                    if (robotTimer != null) {
                        robotTimer.stop();
                        robotTimer = null;
                    }

                    int need = configs.max - roles.size();
                    for (int i = 0; i < need; ++i) {
                        dragRobot();
                    }

                    if (configs.max != roles.size()) {
                        shutdown();
                    }
                }
            });
        }

        status = Status.Pair;
        return Config.ERR_SUCCESS;
    }

    // 强拉机器人进房间
    protected Config.Error dragRobot() {
        Robot robot = room.getRobot();

        return pair(robot);
    }

    Config.Error pair() {
        if (getIsDestroy()) {
            return Config.ERR_PAIR_DESTORY;
        }
        if (maxRoles <= roles.size()) {
            return Config.ERR_TABLE_FULL;
        }

        if (status == Status.Open) {
            start();
            return Config.ERR_SUCCESS;
        }

        return Config.ERR_PAIR_FAILURE;
    }

    Config.Error pair(Role role) {
        if (getIsDestroy()) {
            return Config.ERR_PAIR_DESTORY;
        }

        if (status == Status.Open) {
            Config.Error err = startPair();
            if (err == Config.ERR_SUCCESS) {
                if (enter(role)) {
                    if (roles.size() >= -1) {
                        start();
                    }
                    return Config.ERR_SUCCESS;
                } else {
                    return Config.ERR_PAIR_FAILURE;
                }

            }
            return err;
        } else if (status == Status.Game) {
            if (GameMain.getInstance().getGameMgr().getRobotPairType().type == Config.RobotPairType.Type.One) {
                return enter(role) ? Config.ERR_SUCCESS : Config.ERR_PAIR_FAILURE;
            }

            return Config.ERR_PAIR_TABLE_STATUS_ERROR;
        } else {
            return Config.ERR_PAIR_TABLE_STATUS_ERROR;
        }

    }

    boolean enter(Role role) {
        roles.put(role.uniqueId, role);
        role.enterTable(this);
        onEnter(role);
        return true;
    }

    Config.Error exit(Role role) {
        onExit(role);
        roles.remove(role.uniqueId);
        return Config.ERR_SUCCESS;
    }

    // 保存所有玩家数据
    protected void save() {

    }

    // 保存历史记录
    protected void saveHistory(Map<String, Object> history) {

    }

    // 桌子准备完毕
    protected void start() {
        status = Status.Game;
        onStart();
    }

    // 游戏开始
    protected boolean begin() {
        Config.RobotPairType robotConfig = GameMain.getInstance().getGameMgr().getRobotPairType();
        if (robotConfig.type == Config.RobotPairType.Type.One) {
            if (roles.size() < robotConfig.min) {
                int num = robotConfig.max - robotConfig.min;
                num = (int) Math.random() * num + robotConfig.min;
                for (int i = 0; i < num; ++i) {
                    dragRobot();
                }
            }
        }
        for (Role role : roles.values()) {
            role.checkMoney();
        }

        return true;
    }

    // 游戏结束
    protected void end() {
        for (Role role : roles.values()) {
            role.save();
        }
    }

    protected void shutdown() {
        destroy();
    }

    protected void destroy() {
        save();
        for (Role role : roles.values()) {
            role.exitRoom();
            onExit(role);
        }
        roles.clear();

        onDestroy();
    }

    protected void setCanDestroy(boolean b) {
        canDestory = b;
        if (canDestory && destroyTrigger != null) {
            destroyTrigger.fire();
        }
    }

    void doStop() {
        for (Role role : roles.values()) {
            role.doStop();
        }

        onStop();
    }

    void doTerminate() {
        for (Role role : roles.values()) {
            role.doTerminate();
        }

        onTerminate();
    }

    void doDestroy() {
        destroy();
        for (Role role : roles.values()) {
            role.doDestroy();
        }

        onStop();
    }

    protected void broadcast(Response msg) {
        for (Role r : roles.values()) {
            r.send(msg);
        }
    }

    protected void broadcast(BaseRespone self, BaseRespone other, String uniqueId) {
        for (Role r : roles.values()) {
            if (uniqueId.equals(r.uniqueId)) {
                r.send(self);
            } else {
                r.send(other);
            }
        }
    }

    protected abstract void onStop();

    protected abstract void onTerminate();

    protected abstract void onEnter(Role role);

    protected abstract void onExit(Role role);

    protected abstract void onStart();

    protected abstract void onDestroy();
}