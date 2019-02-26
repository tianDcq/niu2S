package com.micro.game;

import java.util.List;

interface TNRoleInterface {
    void setPlayerState(int s);
    int getPlayerState();
    void setBankNum(int sit);
    int getBankNum();
    void setWin(long sit);
    long getWin();
    void setCards(List<Integer> cards);
    List<Integer> getCards();
    void setChipNum(int sit);
    int getChipNum();
    void setSit(int sit);
    int getSit();
    void endGame();
    void setCow(int sit);
    int getCow();
}