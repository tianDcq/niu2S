package com.micro.frame;

import lombok.Getter;

/**
 * Schedule
 */
public class Schedule extends Task {
    private @Getter float delay;
    private @Getter int repeat = -1;

    private @Getter float interval = 0;
    private float delta = 0;

    public Schedule(Callback callback) {
        this(callback, 0, -1, 0);
    }

    public Schedule(Callback callback, float interval) {
        this(callback, interval, -1, 0);
    }

    public Schedule(Callback callback, float interval, int repeat) {
        this(callback, interval, repeat, 0);
    }

    public Schedule(Callback callback, float interval, int repeat, float delay) {
        super(callback);
        this.interval = interval;
        this.repeat = repeat;
        this.delay = delay;
    }

    public void update() {
        if (expired) {
            return;
        }
        if (target != null && target.getIsDestroy()) {
            expired = true;
            return;
        }

        if (delay > 0) {
            delay -= GameMain.getInstance().getDelta();
        }

        if (delay > 0) {
            return;
        }

        if (repeat != 0) {
            if (interval <= 0) {
                repeat--;
                callback.func();
            } else {
                delta += GameMain.getInstance().getDelta();
                while (delta > interval) {
                    delta -= interval;
                    callback.func();
                }
            }
        }

        expired = repeat == 0;
    }
}