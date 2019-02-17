package com.micro.game;

import java.util.Map;

import frame.history.GameHistory;

class TNGameHistory extends GameHistory{
    public float tax;
    public int bankNum;
    public int downNum;
    public Map<String,Integer> cardType;
    public Map<String,Long> wins;          //结算结果 id-money
    public Map<String,Object> cards;          //结算结果 id-money
}