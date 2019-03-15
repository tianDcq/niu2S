package com.micro.game;

import frame.game.*;
import frame.*;

class TNGameMgr extends GameMgr {
    TNGameMgr() {
        robotPairType = new Config.RobotPairType(Config.RobotPairType.Type.Fix, 2, 2);
        setGameId(7);
    }

    @Override
    public Player createPlayer() {
        return new TNPlayer();
    }

    @Override
    public Robot createRobot() {
        return new TNRobot();
    }

    @Override
    public Table createTable() {
        return new TNTable();
    }
}