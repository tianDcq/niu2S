package com.micro.frame;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public final class HallMgr {
    private @Getter HashMap<Long, Hall> halls = new HashMap<Long, Hall>();

    public void add(long id, Hall hall) {
        halls.put(id, hall);
    }

    public Hall get(long id) {
        return halls.get(id);
    }

    void doStop() {
        for (Hall hall : halls.values()) {
            hall.doStop();
        }
    }

    void doTerminate() {
        for (Hall hall : halls.values()) {
            hall.doTerminate();
        }
    }

    void doDestroy() {
        for (Hall hall : halls.values()) {
            hall.doDestroy();
        }
    }

    public void update() {

    }
}