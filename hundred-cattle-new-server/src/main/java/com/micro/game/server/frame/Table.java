package com.micro.game.server.frame;

public abstract class Table{

    protected float pairTime;

    public Table(float pairTime)
    {
        this.pairTime = pairTime;
    }

    public abstract void start();
}