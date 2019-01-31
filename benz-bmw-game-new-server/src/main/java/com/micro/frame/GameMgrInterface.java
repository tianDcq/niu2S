package com.micro.frame;

public interface GameMgrInterface {
    public Player createPlayer(String uniqueId);

    public Robot createRobot(String uniqueId);

    public Table createTable(float time);
}