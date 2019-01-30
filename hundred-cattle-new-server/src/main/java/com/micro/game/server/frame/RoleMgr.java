package com.micro.game.server.frame;

import java.util.HashMap;
import java.util.Map;

class RoleMgr{
    private Map<String, Player> players;
    private Map<Long, Map<String, Robot>> robots;
   

    public void addPlayer(Player player)
    {
        players.put(player.getUniqueId(), player);
    }

    public Player getPlayer(String uniqueId)
    {
        return players.get(uniqueId);
    }

    public void addRobot(Robot robot)
    {
        if(!robots.containsKey(robot.getHallId()))
        {
            robots.put(robot.getHallId(), new HashMap<>());
        }

        robots.get(robot.getHallId()).put(robot.getUniqueId(), robot);
    }

    public void removeRobot(Robot robot)
    {
        // @TODO
    }

    public void removeHallRobots(String uniqueId)
    {
        // @TODO
    }

    public void removeAllRobots()
    {
        // @TODO
    }

    public void removePlayer(Player player)
    {
        // @TODO
    }

    public void removeAllPlayers()
    {
        // @TODO
    }
}