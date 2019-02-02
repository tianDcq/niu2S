package com.micro.frame;

import java.util.HashMap;
import java.util.Map;

import com.micro.frame.http.GameHttpRequest;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class RoleMgr {
    private HashMap<String, Role> roles = new HashMap<>();
    private @Getter int playerCount;
    private @Getter int robotCount;

    public Player createPlayer(String uniqueId) {
        Player player = GameMain.getInstance().getGameMgr().createPlayer();
        player.uniqueId = uniqueId;
        roles.put(uniqueId, player);
        requestPlayerInfo();
        ++playerCount;
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

    public Role getRole(String uniqueId) {
        return roles.get(uniqueId);
    }

    public Robot createRobot() {
        Robot robot = GameMain.getInstance().getGameMgr().createRobot();
        ++robotCount;
        robot.init();
        roles.put(robot.uniqueId, robot);
        return robot;
    }

    public void removeRobot(Robot robot) {
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

    void doPrepare() {
        // TODO
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