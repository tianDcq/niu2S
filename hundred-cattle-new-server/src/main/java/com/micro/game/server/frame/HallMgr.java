package com.micro.game.server.frame;

import java.util.Map;

import lombok.Getter;

public final class HallMgr{
    private @Getter Map<Long, HallMain> halls;

    public void add(long id, HallMain hall)
    {
        halls.put(id, hall);
    }

    public HallMain get(long id)
    {
        return halls.get(id);
    }

    public void update(float delta)
    {

    }
}