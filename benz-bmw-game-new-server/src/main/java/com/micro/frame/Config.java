package com.micro.frame;

import java.util.HashMap;

public class Config {
    // 更新频率
    public final static long RATE = 1000 / 60;
    // 消息处理超时时间
    public final static long TIMEOUT = 1000 * 5000;

    public static class Error {
        public int code;
        public String msg;

        Error(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }

    public final static Error ERR_FAILURE = new Error(-1, "未知服务器错误");
    public final static Error ERR_PAIR_FAILURE = new Error(-2, "匹配桌子失败");
    public final static Error ERR_PAIR_CREATE_ERROR = new Error(-3, "创建桌子失败");
    public final static Error ERR_PAIR_TABLE_STATUS_ERROR = new Error(-4, "桌子状态错误");
    public final static Error ERR_STOP = new Error(-5, "停服维护中");
    public final static Error ERR_PAIR_DESTORY = new Error(-5, "房间已销毁");
    public final static Error ERR_ROOM_NOT_EXIST = new Error(-6, "房间不存在");

    public final static Error ERR_SUCCESS = new Error(0, "成功");

    public final static class RobotPairType {
        public enum Type {
            One, Range, Fix, Solo
        }

        public Type type;
        public int min;
        public int max;

        public RobotPairType(Type type, int min, int max) {
            this.type = type;
            this.min = min;
            this.max = max;
        }
    }

    public static class RobotConfig {
        public int bornRate;
    }

    public final static int ROBOT_MAX_COUNT = 1000;
    public final static float ROBOT_COLLECT_TIME = 5 * 60;
    public final static float ROBOT_LIFE_TIME = 10 * 60;

    public final static HashMap<Robot.Type, RobotConfig> ROBOTTYPE = new HashMap<>();
    static {
        RobotConfig robot = new RobotConfig();
        robot.bornRate = 20;
        ROBOTTYPE.put(Robot.Type.Bold, robot);

        robot = new RobotConfig();
        robot.bornRate = 65;
        ROBOTTYPE.put(Robot.Type.Nomal, robot);

        robot = new RobotConfig();
        robot.bornRate = 15;
        ROBOTTYPE.put(Robot.Type.Timid, robot);
    }
}