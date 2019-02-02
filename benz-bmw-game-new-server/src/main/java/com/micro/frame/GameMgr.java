package com.micro.frame;

import lombok.Getter;

public abstract class GameMgr {
    protected @Getter Config.RobotPairType robotPairType;
    protected @Getter int gameId;

    public GameMgr() {

    }

    public abstract Player createPlayer();

    public abstract Robot createRobot();

    public abstract Table createTable();

    public void onPrepare() {
    }

    public void onStop() {
    }

    public void onTerminate() {
    }

    public void onDestroy() {
    }
}