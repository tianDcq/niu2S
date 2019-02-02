package com.micro.frame;

import lombok.Getter;
import lombok.Setter;

public class Robot extends Role {
    protected @Getter @Setter Long hallId;

    enum Type {
        Bold, Nomal, Timid
    };

    public Type type;
}