package com.micro.frame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.micro.common.bean.GlobeResponse;
import com.micro.frame.http.ComCallback;
import com.micro.frame.http.Communication;
import lombok.Getter;

public final class HallMgr {
    private @Getter HashMap<Long, Hall> halls = new HashMap<Long, Hall>();

    public void add(long id, Hall hall) {
        halls.put(id, hall);
    }

    public Hall get(long id) {
        return halls.get(id);
    }

    void doPrepare() {

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

    // 查询数据库,获取所有的id
    void init() {

        Map<String, Object> map = new HashMap<>();
        // 1.奔驰宝马
        map.put("gameId", 1);
        Map<String, ComCallback> gameServiceMap = null;
        ComCallback callback = gameServiceMap.get("/game/getWildGameRoomConfigVo2");
        GlobeResponse func = (GlobeResponse) callback.func(map);

        String jsonString = JSON.toJSONString(func.getData());

        Map map1 = JSON.parseObject(jsonString, Map.class);
        System.out.println(map1);

        Set set = map1.keySet();

        set.stream().forEach(key -> {

            String jsonString1 = JSON.toJSONString(map1.get(key));
            List list = JSON.parseObject(jsonString1, List.class);
            System.out.println(list);


            Hall hall1 = new Hall();
            RoomMgr roomMgr = hall1.getRoomMgr();
            HashMap<String, Room> rooms = roomMgr.getRooms();

            // 一个房间
            list.stream().forEach(configvo -> {

                Room room = new Room();
                Map<String, Object> roomConfig = room.getRoomConfig();

                Map config = JSON.parseObject(JSON.toJSONString(configvo), Map.class);

                Map tbRoomConfig = JSON.parseObject(JSON.toJSONString(config.get("tbRoomConfig")), Map.class);
                Map tbGameRoom = JSON.parseObject(JSON.toJSONString(config.get("tbGameRoom")), Map.class);

                roomConfig.putAll(tbGameRoom);
                roomConfig.putAll(tbRoomConfig);

                Object id = tbGameRoom.get("id");
                rooms.put(String.valueOf(id), room);

            });

            add(Long.valueOf(key.toString()), hall1);

        });


    }
}