package com.micro.game.server.frame;

public interface GameMgrInterface {
    public Player createPlayer(String uniqueId);

    public Robot createRobot(String uniqueId);

    public Table createTable(float time);
}