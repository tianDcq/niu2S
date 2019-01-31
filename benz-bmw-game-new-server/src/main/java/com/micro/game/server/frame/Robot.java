package com.micro.game.server.frame;

import lombok.Getter;
import lombok.Setter;

public class Robot extends Role {
    protected @Getter @Setter Long hallId;

    public Robot(String uniqueId) {
        super(uniqueId);
    }
}