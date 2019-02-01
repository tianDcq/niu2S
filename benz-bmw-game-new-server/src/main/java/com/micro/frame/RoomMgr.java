package com.micro.frame;

import java.util.HashMap;

import lombok.Getter;

public class RoomMgr {
    private @Getter HashMap<String, Room> rooms;

    void doStop() {
        for (Room room : rooms.values()) {
            room.doStop();
        }
    }

    void doTerminate() {
        for (Room room : rooms.values()) {
            room.doTerminate();
        }
    }

    void doDestroy() {
        for (Room room : rooms.values()) {
            room.doDestroy();
        }
    }
}