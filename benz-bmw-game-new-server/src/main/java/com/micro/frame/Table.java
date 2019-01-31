package com.micro.frame;

public abstract class Table {

    protected float pairTime;
    public Room room;

    public Table(float pairTime) {
        this.pairTime = pairTime;
    }

    public abstract void addRole(Role role);

    public abstract void removeRole(Role role);

    public abstract Iterable<Role> getRoles();

    public abstract void start();
}