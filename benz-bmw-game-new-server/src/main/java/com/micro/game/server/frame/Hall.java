package com.micro.game.server.frame;

import lombok.Getter;
import java.util.HashSet;

public class Hall {
    private @Getter RoomMgr roomMgr;
    private @Getter HashSet<Player> players;
    public void enter(Role palyer) {
        
    };
    public void playerToRoom(Role player,String id){
        players.remove(player);
        roomMgr.getRooms().get(id).enter(player);;
    }
}