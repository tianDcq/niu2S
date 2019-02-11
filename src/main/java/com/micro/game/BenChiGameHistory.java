package com.micro.game;

import java.util.Map;

import frame.history.GameHistory;

class BenChiGameHistory extends GameHistory{
    public String tax;
    public int open;
    public String sysHost;
    public Map<String,Long> wins;          //结算结果 id-money
    public Map<String,Long> opens;         //中将结果 id-money
    public ChipStruct[] chipList;      //总下注 id-ChipStruct
    public Map<String,Object> playerbetParts;    //geren id-ChipStruct
}