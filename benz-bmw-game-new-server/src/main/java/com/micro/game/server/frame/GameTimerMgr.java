package com.micro.game.server.frame;

import java.util.HashSet;
import java.util.Iterator;

public class GameTimerMgr {

    private HashSet<GameTimer> timers;

    public GameTimerMgr() {
        timers = new HashSet<GameTimer>();
    }

    public GameTimer createTimer(int time, Callback callback) {
        GameTimer timer = new GameTimer(time, callback);
        timers.add(timer);
        timer.setTimerMgr(this);
        return timer;
    }

    public void removeTimer(GameTimer timer) {
        timers.remove(timer);
    }

    public void removeAllTimers() {
        timers.clear();
    }

    public void stopTimer(GameTimer timer) {
        timer.stop();
    }

    public void stopAllTimers() {
        for (GameTimer timer : timers) {
            timer.stop();
        }

        timers.clear();
    }

    public void update() {
        Iterator<GameTimer> it = timers.iterator();
        while (it.hasNext()) {
            GameTimer timer = it.next();
            timer.update();
            if (timer.isExpired()) {
                it.remove();
            }
        }
    }
}