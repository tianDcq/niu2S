package com.micro.game.server.frame;

import lombok.Getter;

/**
 * GameTrigger
 */
public class Trigger extends Task {
    private boolean trigger;

    public Trigger(Callback callback) {
        super(callback);
        this.callback = callback;
    }

    public void fire() {
        trigger = true;
    }

    public void update() {
        if (expired) {
            return;
        }
        expired = trigger;
        if (expired) {
            callback.func();
        }
    }
}