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
        requestPlayerInfo(player);
        ++playerCount;
        return player;
    }

    void requestPlayerInfo(Player player) {
        String siteId = player.uniqueId.split("_")[0];
        String account = player.uniqueId.split("_")[1];
        GameHttpRequest httpRequest = GameHttpRequest.buildRequest();
        httpRequest.setSuccessCallback(new Callback() {
            @Override
            public void func() {
                if (roles.get(player.uniqueId) == player) {
                    player.init((HashMap<String, Object>) this.getData());
                }
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
        map.put("account", account);
        map.put("uniqueId", player.uniqueId);
        httpRequest.sendForm("/acc/getPlayer", map);
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

    public static void main(String[] args) {
        String str = "1_abc";
        String s = str.split("_")[0];
        System.out.println(s);
    }
}