package com.micro.game.server.frame;

import lombok.Setter;

public abstract class Callback {
    private @Setter Object data;

    public abstract void func();
}