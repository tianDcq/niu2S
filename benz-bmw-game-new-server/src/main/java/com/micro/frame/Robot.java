package com.micro.frame;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

public abstract class Robot extends Role {
    enum Type {
        Bold, Nomal, Timid
    };

    public Type type;

    private static int index = 0;
    void init() {
        int random = (int) (Math.random() * 100);
        int inc = 0;
        for (HashMap.Entry<Robot.Type, Config.RobotConfig> entry : Config.ROBOTTYPE.entrySet()) {
            inc += entry.getValue().bornRate;
            if (inc >= random) {
                type = entry.getKey();
                break;
            }
        }
        gender=1;
        nickName="卡拉圣诞快乐";
        portrait="4";
        money=100000000;
        uniqueId="Robot_" + (index++);
        super.init();
    }

    public Config.Error exitHall() {
        if (this.table != null) {
            Config.Error err = this.table.exit(this);
            if (err != Config.ERR_SUCCESS) {
                return err;
            }
        }

        return Config.ERR_SUCCESS;
    }
}