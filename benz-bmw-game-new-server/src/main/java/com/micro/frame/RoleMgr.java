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
        requestPlayerInfo();
        return player;
    }

    void requestPlayerInfo() {
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
        Map<String, String> map = new HashMap<>();
        map.put("siteId", "1");
        map.put("gameId", "12");
        Callback send = httpRequest.sendForm("http://localhost:9501/game/getWildGameRoomConfigVo", map);
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
}