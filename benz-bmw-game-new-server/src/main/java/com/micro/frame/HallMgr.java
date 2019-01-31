package com.micro.frame;

import java.util.Map;

import lombok.Getter;

public final class HallMgr {
    private @Getter Map<Long, Hall> halls;

    public void add(long id, Hall hall) {
        halls.put(id, hall);
    }

    public Hall get(long id) {
        return halls.get(id);
    }

    public void update() {

    }
}