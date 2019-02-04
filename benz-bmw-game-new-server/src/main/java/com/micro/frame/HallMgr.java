package com.micro.frame;

import com.alibaba.fastjson.JSON;
import com.micro.common.bean.GlobeResponse;
import com.micro.frame.http.ComCallback;
import com.micro.frame.http.ThreadPool;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

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
        // // @test
        // list = list.subList(0, 1);
        list.stream().forEach(configvo -> {
            Map config = JSON.parseObject(JSON.toJSONString(configvo), Map.class);
            hall.getRoomMgr().createRoom(config);
        });
        add(Long.valueOf(key.toString()), hall);

        return hall;

    }

    // 查询数据库,获取所有的id
    void init(Callback callbackOut) {

        Map<String, Object> map = new HashMap<>();
        map.put("gameId", GameMain.getInstance().getGameMgr().getGameId());
        Call call = GameMain.getInstance().getCallMgr().create("/game/getWildGameRoomConfigVo2", map);
        call.setSuccess(new Callback() {

            @Override
            public void func() {

                Map map1 = (Map) this.getData();
                for (Object key : map1.keySet()) {
                    String jsonString1 = JSON.toJSONString(map1.get(key));
                    List list = JSON.parseObject(jsonString1, List.class);
                    System.out.println(list);

                    createHall(Long.valueOf(key.toString()), list);
                }
            }
        });
        call.done();

    }
}