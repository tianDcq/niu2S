package com.micro.frame;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class RoleMgr {
    private HashMap<String, Role> roles = new HashMap<>();
    private HashMap<ChannelHandlerContext, Player> ctxs = new HashMap<>();
    private @Getter int playerCount;
    private @Getter int robotCount;

    Player createPlayer(String uniqueId, ChannelHandlerContext ctx) {
        Player player = GameMain.getInstance().getGameMgr().createPlayer();
        player.uniqueId = uniqueId;
        player.setCtx(ctx);
        ctxs.put(ctx, player);
        roles.put(uniqueId, player);
        requestPlayerInfo(player);
        ++playerCount;
        return player;
    }

    void requestPlayerInfo(Player player) {
        String siteId = player.uniqueId.split("_")[0];
        String account = player.uniqueId.split("_")[1];

        final Map<String, Object> params = new HashMap<>();
        params.put("siteId", Long.valueOf(siteId));
        params.put("account", account);
        params.put("uniqueId", player.uniqueId);
        Call call = GameMain.getInstance().getCallMgr().create("/acc/getPlayer", params);
        call.setSuccess(new Callback() {

            @Override
            public void func() {
                if (roles.get(player.uniqueId) == player) {
                    player.init((HashMap<String, Object>) this.getData());
                    Hall hall = GameMain.getInstance().getHallMgr().get(player.siteId);
                    if (hall != null) {
                        player.enterHall(hall);
                    }
                }
            }
        });
        call.done();
    }

    public Role getRole(String uniqueId) {
        return roles.get(uniqueId);
    }

    Robot createRobot() {
        Robot robot = GameMain.getInstance().getGameMgr().createRobot();
        ++robotCount;
        robot.init();
        roles.put(robot.uniqueId, robot);
        return robot;
    }

    public void disconnect(ChannelHandlerContext ctx) {
        Player player = ctxs.get(ctx);
        if (player != null) {
            player.setCtx(null);
            ctxs.remove(ctx);

            // 只有桌子存在的时候才会通知
            if (player.table != null) {
                player.onDisconnect();
            } else {
                player.exitHall();
                removeRole(player);
            }
        }
    }

    public void reconnect(Player player, ChannelHandlerContext ctx) {
        if (player.getCtx() != null) {
            if (player.getCtx() == ctx) {
                return;
            }
            disconnect(ctx);
        }
        player.setCtx(ctx);
        player.onReconnect();
    }

    void removeRole(Role role) {
        if (role instanceof Player) {
            removePlayer((Player) role);
        } else {
            removeRobot((Robot) role);
        }
    }

    private void removeRobot(Robot robot) {
        roles.remove(robot.uniqueId);
    }

    private void removePlayer(Player player) {
        if (player.getCtx() != null) {
            ctxs.remove(player.getCtx());
        }
        roles.remove(player.uniqueId);
    }

    public void removeAllRobots() {
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