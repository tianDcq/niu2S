package com.micro.game.server.frame;

import java.util.HashMap;
import java.util.HashSet;

import com.sun.xml.internal.xsom.impl.scd.Iterators.Map;

import lombok.Getter;


public class RoomMgr {
    private @Getter HashMap<String,Room> rooms;
}