package com.micro.game.server.frame;

import lombok.Getter;
import lombok.Setter;

class GameTimer {
    private @Setter GameTimerMgr timerMgr;
    private @Getter boolean expired;
    private @Getter Callback callback;
    private @Getter float time;
    private @Getter long endTimeMillisecond;

    public GameTimer(float time, Callback callback) {
        this.time = time;
        this.callback = callback;
        endTimeMillisecond = GameMain.getInstance().getMillisecond() + (long) (time * 1000 * 1000);
    }

    public void stop() {
        expired = true;
    }

    public void update() {
        if (expired) {
            return;
        }
        expired = endTimeMillisecond >= GameMain.getInstance().getMillisecond();
        if (expired) {
            callback.func();
        }
    }
}