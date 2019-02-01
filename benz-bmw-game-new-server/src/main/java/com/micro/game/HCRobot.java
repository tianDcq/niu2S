package com.micro.game;

import com.micro.frame.Robot;

import lombok.Getter;

class HCRobot extends Robot implements HCRoleInterface {
    public @Getter ChipStruct[] chipList=new ChipStruct[8];;
    public HCRobot(String uniqueId) {
        super(uniqueId);
    }
}