package com.micro.frame;

import java.util.HashMap;
import java.util.Map;

import com.micro.frame.http.GameHttpRequest;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class RoleMgr {
    private HashMap<ChannelHandlerContext, Player> queuePlayers = new HashMap<>();
    private HashMap<String, Player> players = new HashMap<>();
    private HashMap<Long, HashMap<String, Robot>> robots = new HashMap<>();

    public Player createPlayer(String uniqueId) {
        Player player = GameMain.getInstance().getGameMgr().createPlayer();
        player.uniqueId = uniqueId;
        players.put(uniqueId, player);
        requestPlayerInfo(player.uniqueId.split("_")[0]);
        return player;
    }

    void requestPlayerInfo(String siteId) {
        GameHttpRequest httpRequest = GameHttpRequest.buildRequest();
        httpRequest.setSuccessCallback(new Callback() {
            @Override
            public void func() {
                System.out.println(1);
            }
        });
        httpRequest.setFailCallback(new Callback() {
            @Override
            public void func() {
                System.out.println(2);
            }
        });
        // 发起请求
        final Map<String, Object> map = new HashMap<>();
        map.put("siteId", Long.valueOf(siteId));
        map.put("gameId", 14);
        httpRequest.sendForm("/game/getWildGameRoomConfigVo", map);
//        new Thread(()->{
//        });
//        Callback send = httpRequest.sendForm("http://localhost:9501/game/getWildGameRoomConfigVo", map);
    }

    public void addPlayer(Player player) {
        players.put(player.uniqueId, player);
    }

    public Player getPlayer(String uniqueId) {
        return players.get(uniqueId);
    }

    public void addRobot(Robot robot) {
        if (!robots.containsKey(robot.getHallId())) {
            robots.put(robot.getHallId(), new HashMap<>());
        }

        robots.get(robot.getHallId()).put(robot.uniqueId, robot);
    }

    public void removeRobot(Robot robot) {
        // @TODO
    }

    public void removeHallRobots(String uniqueId) {
        // @TODO
    }

    public void removeAllRobots() {
        // @TODO
    }

    public void removePlayer(Player player) {
        // @TODO
    }

    public void removeAllPlayers() {
        // @TODO
    }

    void doDestroy() {
        // TODO
    }

    void doStop() {
        // TODO
    }

    void doTerminate() {
        // TODO
    }

    public static void main(String[] args) {
        String str = "1_abc";
        String s = str.split("_")[0];
        System.out.println(s);
    }
}