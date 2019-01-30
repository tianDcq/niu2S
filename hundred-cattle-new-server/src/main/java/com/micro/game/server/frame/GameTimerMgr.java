package com.micro.game.server.frame;

import java.util.HashSet;

public class GameTimerMgr{

    private HashSet<GameTimer> timers;

    public GameTimer createTimer(int time, Callback callback)
    {
        GameTimer timer = new GameTimer(time, callback);
        timers.add(timer);
        return timer;
    }

    public void update(float delta)
    {
        
    }
}