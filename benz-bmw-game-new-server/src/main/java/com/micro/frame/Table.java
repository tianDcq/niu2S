package com.micro.frame;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public abstract class Table extends Root {

    enum Status {
        Open, Pair, Game, End
    }

    enum Type {
        Common, Unique
    }

    public class Configs {
        public int pairTime;
        public int robotTime;
        public Type type;
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

    protected Config.Error startPair() {
        status = Status.Pair;

        if (configs.pairTime > 0) {
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

        return Config.ERR_SUCCESS;
    }

    // 强拉机器人进房间
    protected Config.Error dragRobot() {
        Robot robot = room.getRobot();
        robot.prepareEnterTable(this);

        return pair(robot);
    }

    protected Config.Error pair(Role role) {
        if (getIsDestroy()) {
            return Config.ERR_PAIR_DESTORY;
        }
        if (status == Status.Open) {
            Config.Error err = startPair();
            if (err == Config.ERR_SUCCESS) {
                if (addRole(role)) {
                    if (roles.size() >= this.configs.max) {
                        status = Status.Game;
                        start();
                    }
                    return Config.ERR_SUCCESS;
                } else {
                    return Config.ERR_PAIR_FAILURE;
                }

            }
            return err;
        } else if (status == Status.Game) {
            if (configs.type == Type.Unique) {
                return addRole(role) ? Config.ERR_SUCCESS : Config.ERR_PAIR_FAILURE;
            }

            return Config.ERR_PAIR_TABLE_STATUS_ERROR;
        } else {
            return Config.ERR_PAIR_TABLE_STATUS_ERROR;
        }

    }

    protected boolean enter(Role role) {
        if (addRole(role)) {
            role.enterTable(this);
            onEnter(role);
            return true;
        }
        return false;
    }

    public boolean exit(Role role) {
        if (removeRole(role)) {
            role.exitTable();
            role.room.enter(role);
            onExit(role);
            return true;
        }
        return false;
    }

    protected boolean addRole(Role role) {
        roles.put(role.uniqueId, role);
        return true;
    }

    protected boolean removeRole(Role role) {
        roles.remove(role.uniqueId);
        return true;
    }

    // 保存所有玩家数据
    protected void save() {

    }

    // 保存历史记录
    protected void saveHistory(Map<String, Object> history) {

    }

    // 桌子准备完毕
    protected void start() {
        onStart();
    }

    // 游戏开始
    protected boolean begin() {
        return true;
    }

    // 游戏结束
    protected void end() {

    }

    protected void shutdown() {
        destroy();
    }

    protected void destroy() {
        save();
        for (Role role : roles.values()) {
            role.exitTable();
            onExit(role);
        }

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

    protected abstract void onStop();

    protected abstract void onTerminate();

    protected abstract void onEnter(Role role);

    protected abstract void onExit(Role role);

    protected abstract void onStart();

    protected abstract void onDestroy();
}