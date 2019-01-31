package com.micro.game.server.frame;

public interface GameMgrInterface{
    public Player createPlayer();
    public Robot createRobot();
    public Table createTable(float time);
}