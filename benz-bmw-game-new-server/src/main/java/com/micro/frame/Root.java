package com.micro.frame;

import lombok.Getter;
import lombok.Setter;

abstract class Root {
    private @Setter boolean isDestroy;

    public boolean getIsDestroy() {
        return isDestroy;
    }
}