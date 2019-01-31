package com.micro.frame;

import lombok.Setter;

public abstract class Callback {
    private @Setter Object data;

    public abstract void func();
}