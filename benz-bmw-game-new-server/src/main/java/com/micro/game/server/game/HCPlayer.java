package com.micro.game.server.game;

import com.micro.game.server.frame.Player;
import com.micro.game.server.frame.Role;
import com.micro.game.server.vo.common.Request;

class HCPlayer extends Player implements HCRoleInterface {
    public HCPlayer() {
        super();
    }

    public void onMsg(Request req, Role Role) {
        
    }
}