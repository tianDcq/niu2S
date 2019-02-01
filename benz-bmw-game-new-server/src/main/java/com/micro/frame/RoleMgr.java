package com.micro.frame;

import java.util.HashMap;
import java.util.Map;

public final class RoleMgr {
    private Map<String, Player> players;
    private Map<Long, Map<String, Robot>> robots;

    public Player createPlayer(String uniqueId) {
        Player player = GameMain.getInstance().getGameMgr().createPlayer();
        player.uniqueId = uniqueId;
        addPlayer(player);
        return player;
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