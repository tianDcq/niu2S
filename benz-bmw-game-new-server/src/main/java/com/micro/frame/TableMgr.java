package com.micro.frame;

import java.util.Map;

import lombok.Getter;

/**
 * TableMgr
 */
public class TableMgr {
    private Hall hall;
    private @Getter Map<Integer, Table> tables;

    private int index;
    // 目前只做单个桌子排队等待机制
    private Table wait;

    private Table createTable() {
        Table table = GameMain.getInstance().getGameMgr().createTable();

        // TODO
        return table;
    }

    Table getWait() {
        if (wait == null) {
            wait = createTable();
        }
        return wait;
    }
}