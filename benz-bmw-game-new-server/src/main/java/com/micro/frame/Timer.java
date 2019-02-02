package com.micro.frame;

import lombok.Getter;
import lombok.Setter;

public class Timer extends Task {
    private @Getter float time;
    private @Getter long endTimeMillisecond;

    Timer(float time, Callback callback) {
        super(callback);
        this.time = time;
        endTimeMillisecond = GameMain.getInstance().getMillisecond() + (long) (time * 1000 * 1000);
    }

    public void update() {
        if (expired) {
            return;
        }
        if (target != null && target.getIsDestroy()) {
            expired = true;
            return;
        }
        expired = endTimeMillisecond >= GameMain.getInstance().getMillisecond();
        if (expired) {
            callback.func();
        }
    }
}