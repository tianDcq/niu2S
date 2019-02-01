package com.micro.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.micro.frame.*;
import com.micro.frame.socket.ErrRespone;
import com.micro.frame.socket.Response;

import lombok.Getter;

final class HCTable extends Table {
    private @Getter int time;
    private @Getter int gameStae;
    private @Getter int gameIndex;
    private ChipStruct[] chipList;
    private int maxBanker;
    private int bIndex = 0;

    private List<String> bankerList;
    private int openTime;
    private int waitTime;
    private int chipTime;

    protected void onInit() {
        Map<String, Object> roomConfig = room.getRoomConfig();
        openTime = (int) roomConfig.get("openTime");
        waitTime = (int) roomConfig.get("waitTime");
        chipTime = (int) roomConfig.get("chipTime");
        chipList = new ChipStruct[8];
    }

    public void onEnter(Role role) {
        Response ownMsg = new Response(2001, 1);
        Response mm = new Response(2007, 1);
        Map<String, Object> msg = new HashMap<>();
        msg.put("playerName", role.nickName);
        msg.put("playerCoins", role.money);
        msg.put("portrait", role.portrait);
        msg.put("token", role.token);
        msg.put("uniqueId", role.uniqueId);
        mm.msg = msg;
        pushMsgToOther(mm, ownMsg, role.uniqueId);
    };

    public void onExit(Role role) {
        Map<String, Object> msg = new HashMap<>();
        Response ownMsg = new Response(2010, 1);
        msg.put("playerName", role.nickName);
        msg.put("uniqueId", role.uniqueId);
        msg.put("token", role.token);
        msg.put("playerCoins", role.money);
        ownMsg.msg = msg;
        Response mm = new Response(2008, 1);
        mm.msg = new HashMap<>(msg);
        pushMsgToOther(mm, ownMsg, role.uniqueId);
    };

    @SuppressWarnings("unchecked")
    public boolean playerChip(Role role, Map<String, Object> map) {
        if ((int) map.get("gameIndex") != gameIndex) {
            ErrRespone msg = new ErrRespone(2002, 0, "局数不对");
            role.sendMsg(msg);
            return false;
        } else {
            Object obj = map.get("betInfo");
            long nMoney = 0;
            List<Map<String, Long>> list = (List<Map<String, Long>>) obj;
            for (int i = 0; i < list.size(); ++i) {
                Map<String, Long> info = list.get(i);
                nMoney += info.get("betAmount");
            }
            if (nMoney > role.money) {
                ErrRespone msg = new ErrRespone(2002, 0, "钱不够下注");
                role.sendMsg(msg);
                return false;
            }
            for (int i = 0; i < list.size(); ++i) {
                Map<String, Long> info = list.get(i);
                long pos = info.get("betTarget");
                ((HCRoleInterface) role).getChipList()[(int) pos].betAmount = info.get("betAmount");
                chipList[(int) pos].betAmount += info.get("betAmount");
            }
            Response ownMsg = new Response(2002, 1);
            ownMsg.msg = new HashMap<String, Object>();
            ownMsg.msg.put("betInfo", map.get("betInfo"));
            Response otherMsg = new Response(2003, 1);
            List<Object> playerInfo = new ArrayList<Object>();
            Map<String, Object> bet = new HashMap<>();
            bet.put("uniqueId", role.uniqueId);
            bet.put("betInfo", map.get("betInfo"));
            playerInfo.add(playerInfo);
            otherMsg.msg = new HashMap<String, Object>();
            otherMsg.msg.put("playerInfo", playerInfo);
            pushMsgToOther(otherMsg, ownMsg, role.uniqueId);
            return true;
        }
    }

    public void playerUpBanker(Role role) {
        Map<String, Object> roomConfig = room.getRoomConfig();
        boolean up = (boolean) roomConfig.get("hostAble"); // 获取是否允许上庄
        if (up) {
            long coin = (long) roomConfig.get("hostAble"); // 获取上庄的钱
            if (role.money < coin) {
                ErrRespone res = new ErrRespone(2009, 0, "钱不够不能上庄");
                role.sendMsg(res);
            } else {
                bankerList.add(role.uniqueId);
                String size = String.valueOf(bankerList.size());
                ErrRespone ownMsg = new ErrRespone(2009, 1, size);
                Response otherMsg = new Response(2004, 1);
                Map<String, Object> msg = new HashMap<>();
                msg.put("playerName", role.nickName);
                msg.put("playerCoins", role.money);
                msg.put("portrait ", role.portrait);
                msg.put("position", size);
                msg.put("token", role.token);
                msg.put("uniqueId", role.uniqueId);
                otherMsg.msg = msg;
                pushMsgToOther(otherMsg, ownMsg, role.uniqueId);
            }
        }
    };

    public void playerDownBanker(Role role) {
        if (bankerList.remove(role.uniqueId)) {
            ErrRespone ownMsg = new ErrRespone(2011, 1, "离开庄家");
            Response otherMsg = new Response(2016, 1);
            otherMsg.msg = new HashMap<String, Object>();
            otherMsg.msg.put("playerName", role.nickName);
            otherMsg.msg.put("playerCoins", role.money);
            otherMsg.msg.put("token", role.token);
            otherMsg.msg.put("uniqueId", role.uniqueId);
            pushMsgToOther(otherMsg, ownMsg, role.uniqueId);
        }
    };

    public void requstTableScene(Role role) {
        Response response = new Response(2018, 1);
        Map<String, Object> msg = new HashMap<>();

        Map<String, Object> isObserve = new HashMap<>();
        // ttttttttttt
        msg.put("isObserve", isObserve);

        List<Object> players = new ArrayList<>();
        for (Role rr : roles.values()) {
            Map<String, Object> player = new HashMap<>();
            player.put("playerName", rr.nickName);
            player.put("playerCoins", rr.money);
            player.put("portrait ", rr.portrait);
            player.put("token", rr.token);
            player.put("uniqueId", rr.uniqueId);
            players.add(player);
        }
        msg.put("Players", players);
        msg.put("selfCoins", role.money);
        Map<String, Object> hostSqeunce = new HashMap<>();
        for (int i = 0; i < bankerList.size(); ++i) {
            Map<String, Object> host = new HashMap<>();
            //
            // host.put("playerName", bankerList[i].nickName);
            // host.put("playerCoins", rr.money);
            // host.put("portrait ", rr.portrait);
            // host.put("token", rr.token);
            // host.put("uniqueId", rr.uniqueId);
        }
    }

    public void onStart() {

    };

    protected void onStop() {

    }

    protected void onTerminate() {

    }

    protected void onDestroy() {

    }

    public void pushMsgToOther(Response otherMsg, Response ownMsg, String tarID) {

        for (Role rr : roles.values()) {
            String id = rr.uniqueId;
            if (tarID.equals(id)) {
                rr.sendMsg(ownMsg);
            } else {
                rr.sendMsg(otherMsg);
            }
        }
    }
}