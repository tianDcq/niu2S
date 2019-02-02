package com.micro.frame;

import java.util.HashMap;

class Config {
    // 更新频率
    final static long RATE = 1000 / 60;
    // 消息处理超时时间
    final static long TIMEOUT = 1000 * 5;

    static class RobotConfig {
        public int bornRate;
    }

    final static int ROBOT_MAX_COUNT = 1000;
    final static float ROBOT_COLLECT_TIME = 5 * 60;
    final static float ROBOT_LIFE_TIME = 10 * 60;

    final static HashMap<Robot.Type, RobotConfig> ROBOTTYPE = new HashMap<>();
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