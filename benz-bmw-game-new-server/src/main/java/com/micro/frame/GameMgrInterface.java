package com.micro.frame;

public interface GameMgrInterface {
    public Player createPlayer();

    public Robot createRobot();

    public Table createTable();

    public void onPrepare();

    public void onStop();

    public void onTerminate();

    public void onDestroy();
}