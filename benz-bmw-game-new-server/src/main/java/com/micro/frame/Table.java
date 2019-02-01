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
    protected float pairTime;
    protected @Setter Room room;
    protected @Getter int id;

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

    public void leave(Role role) {

    }

    protected boolean addRole(Role role) {
        roles.put(role.uniqueId, role);
        role.enterTable(this);
        onAddRole(role);
        return true;
    }

    protected boolean removeRole(Role role) {
        roles.remove(role.uniqueId);
        role.leaveTable();
        onRemoveRole(role);
        return false;
    }

    protected void start() {

    }

    protected abstract void onAddRole(Role role);

    public abstract void onRemoveRole(Role role);

    public abstract void onStart();
}