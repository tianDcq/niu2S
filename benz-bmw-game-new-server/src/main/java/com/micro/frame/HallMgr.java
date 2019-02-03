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

    Hall createHall(Long key, List list) {

        Hall hall = new Hall();
        list.stream().forEach(configvo -> {
            Map config = JSON.parseObject(JSON.toJSONString(configvo), Map.class);
            hall.getRoomMgr().createRoom(config);
        });
        add(Long.valueOf(key.toString()), hall);

        return hall;

    }

    // 查询数据库,获取所有的id
    void init() {

        Map<String, Object> map = new HashMap<>();
        // 1.奔驰宝马
        map.put("gameId", GameMain.getInstance().getGameMgr().getGameId());
        Map<String, ComCallback> gameServiceMap = Communication.getGameServiceMap();
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

            createHall(Long.valueOf(key.toString()), list);
        });

    }
}