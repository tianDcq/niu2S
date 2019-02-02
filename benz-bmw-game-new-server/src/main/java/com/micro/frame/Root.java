package com.micro.frame;

import lombok.Getter;
import lombok.Setter;

public abstract class Root {
    private @Setter boolean isDestroy;

    public boolean getIsDestroy() {
        return isDestroy;
    }
}