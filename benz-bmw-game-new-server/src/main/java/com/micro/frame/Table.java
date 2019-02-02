package com.micro.frame;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public abstract class Table {

    enum Status {
        Open, Pair, Game, Close
    }

    enum Type {
        Common, Unique
    }

    public class Configs {
        public float pairTime;
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

    protected boolean pair(Role role) {
        if (status == Status.Open) {
            status = Status.Pair;
            if (addRole(role)) {
                if (roles.size() >= this.configs.max) {
                    status = Status.Game;
                    start();
                }
                return true;
            }
            return false;
        } else if (status == Status.Game) {
            if (configs.type == Type.Unique) {
                return addRole(role);
            }

            return false;
        } else {
            return false;
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
    protected void begin() {

    }

    // 游戏结束
    protected void end() {

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