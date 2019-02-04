package com.micro.frame;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import com.alibaba.fastjson.JSON;

public class RoomMgr {
    private @Getter HashMap<String, Room> rooms = new HashMap<>();
    private @Setter Hall hall;

    Room createRoom(Map configvo) {
        Map<String, Object> roomConfig = new HashMap<String, Object>();
        Map config = JSON.parseObject(JSON.toJSONString(configvo), Map.class);
        Map tbRoomConfig = JSON.parseObject(JSON.toJSONString(config.get("tbRoomConfig")), Map.class);
        // Map tbGameRoom = JSON.parseObject(JSON.toJSONString(config.get("tbGameRoom")), Map.class);
        
        roomConfig.put("roomType", 1);
        roomConfig.put("gameRoomId", tbRoomConfig.get("gameRoomId"));
        roomConfig.put("roomName", "覅欸發");
        roomConfig.put("bottomRed1", 20);
        roomConfig.put("bottomRed2", 100);
        roomConfig.put("betTime", 20);
        roomConfig.put("freeTime", 5);
        roomConfig.put("taxRatio", 2);
        roomConfig.put("bankerTime", 8);
        roomConfig.put("bankerCond", 1000);
        roomConfig.put("sysGold", 1);
        roomConfig.put("shangzhuangSwitch", 1);

        // System.out.print(roomConfig);

        Room room = new Room();
        room.setHall(hall);
        room.init(roomConfig);
        rooms.put(String.valueOf(roomConfig.get("gameRoomId")), room);

        return room;
    }

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