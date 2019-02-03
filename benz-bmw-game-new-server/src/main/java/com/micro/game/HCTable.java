package com.micro.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.micro.frame.*;
import com.micro.frame.socket.ErrRespone;
import com.micro.frame.socket.Response;

import lombok.Getter;

final class HCTable extends Table {
    private @Getter int time;
    private @Getter int gameStae;
    private @Getter int gameIndex = 0;
    private float revenue = 0;
    private ChipStruct[] chipList;
    private long[] playerChipList = { 0, 0, 0, 0, 0, 0, 0, 0 };
    public List<Integer> history;
    private int maxBanker;
    private int bankerIndex = 0;
    private int minChip;
    private int maxChip;
    private @Getter Role banker;
    private @Getter List<String> bankerList;
    private Schedule schedule;
    private int openTime;
    private int waitTime;
    private int chipTime;
    private boolean allowBank;
    private long bankMoney;
    private HashSet<Role> chipPlayer;

    @Override
    protected void onInit() {
        Map<String, Object> roomConfig = room.getRoomConfig();
        openTime = (int) roomConfig.get("betTime");
        waitTime = (int) roomConfig.get("freeTime");
        chipTime = (int) roomConfig.get("betTime");
        revenue = (int) roomConfig.get("taxRatio");
        maxBanker = (int) roomConfig.get("bankerTime");
        minChip = (int) roomConfig.get("bottomRed1");
        maxChip = (int) roomConfig.get("bottomRed2");
        allowBank = (int) roomConfig.get("shangzhuangSwitch") == 1;

        bankMoney = (int) roomConfig.get("bankerCond");
        chipList = new ChipStruct[8];
        for (int i = 0; i < 8; ++i) {
            chipList[i] = new ChipStruct(i);
        }
        bankerList = new ArrayList<>();
        chipPlayer = new HashSet<>();
        history = new ArrayList<>();
    }

    @Override
    protected void onEnter(Role role) {
        ErrRespone ownMsg = new ErrRespone(2001, 1, "45544454");
        Response mm = new Response(2007, 1);
        Map<String, Object> msg = new HashMap<>();
        msg.put("playerName", role.nickName);
        msg.put("playerCoins", role.money);
        msg.put("portrait", role.portrait);
        msg.put("token", role.token);
        msg.put("uniqueId", role.uniqueId);
        mm.msg = msg;
        broadcast(ownMsg, mm, role.uniqueId);
    };

    @Override
    protected void onExit(Role role) {
        Map<String, Object> msg = new HashMap<>();
        Response ownMsg = new Response(2010, 1);
        msg.put("playerName", role.nickName);
        msg.put("uniqueId", role.uniqueId);
        msg.put("token", role.token);
        msg.put("playerCoins", role.money);
        ownMsg.msg = msg;
        Response mm = new Response(2008, 1);
        mm.msg = new HashMap<>(msg);
        broadcast(ownMsg, mm, role.uniqueId);
    };

    @SuppressWarnings("unchecked")
    public boolean playerChip(Role role, Map<String, Object> map) {
        if (gameStae != 1) {
            ErrRespone msg = new ErrRespone(2002, 0, "不在下注阶段");
            role.send(msg);
            return false;
        }
        if (banker == role) {
            ErrRespone msg = new ErrRespone(2002, 0, "庄家不能下注");
            role.send(msg);
            return false;
        }
        if ((int) map.get("gameIndex") != gameIndex) {
            ErrRespone msg = new ErrRespone(2002, 0, "局数不对");
            role.send(msg);
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
                role.send(msg);
                return false;
            }
            if (nMoney < minChip || nMoney > maxChip) {
                ErrRespone msg = new ErrRespone(2002, 0, "下注不在允许范围");
                role.send(msg);
                return false;
            }
            for (int i = 0; i < list.size(); ++i) {
                Map<String, Long> info = list.get(i);
                long pos = info.get("betTarget");
                ((HCRoleInterface) role).getChipList()[(int) pos].betAmount = info.get("betAmount");
                chipList[(int) pos].betAmount += info.get("betAmount");
                if (role instanceof Player) {
                    playerChipList[i] += info.get("betAmount");
                }
            }
            chipPlayer.add(role);
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
            broadcast(ownMsg, otherMsg, role.uniqueId);
            return true;
        }
    }

    public void playerUpBanker(Role role) {
        if (allowBank) {
            if (role.money < bankMoney) {
                ErrRespone res = new ErrRespone(2009, 0, "钱不够不能上庄");
                role.send(res);
            } else if (bankerList.contains(role.uniqueId)) {
                ErrRespone res = new ErrRespone(2009, 0, "你已经在列表里面了");
                role.send(res);
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
                broadcast(ownMsg, otherMsg, role.uniqueId);
            }
        }
    };

    public void playerDownBanker(Role role) {
        if (gameStae != 2) {
            ErrRespone msg = new ErrRespone(2002, 0, "现在不能下庄");
            role.send(msg);
            return;
        }
        if (bankerList.remove(role.uniqueId)) {
            ErrRespone ownMsg = new ErrRespone(2011, 1, "离开庄家");
            Response otherMsg = new Response(2016, 1);
            otherMsg.msg = new HashMap<String, Object>();
            otherMsg.msg.put("playerName", role.nickName);
            otherMsg.msg.put("playerCoins", role.money);
            otherMsg.msg.put("token", role.token);
            otherMsg.msg.put("uniqueId", role.uniqueId);
            broadcast(ownMsg, otherMsg, role.uniqueId);
        }
    };

    public void requstTableScene(Role role) {
        Response response = new Response(2018, 1);
        Map<String, Object> msg = new HashMap<>();

        Map<String, Object> isObserve = new HashMap<>();
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
            Role player = roles.get(bankerList.get(i));
            host.put("playerName", player.nickName);
            host.put("playerCoins", player.money);
            host.put("portrait ", player.portrait);
            host.put("token", player.token);
            host.put("uniqueId", player.uniqueId);
            hostSqeunce.put("hostSqeunce", host);
        }
        msg.put("hostSqeunce", hostSqeunce);

        Map<String, Object> currentHost = new HashMap<>();
        if (banker == null) {
            currentHost.put("system", true);
            currentHost.put("playerName", null);
            currentHost.put("playerCoins", null);
            currentHost.put("portrait", null);
            currentHost.put("restHost", 0);
            currentHost.put("maxHost", maxBanker);
            currentHost.put("uniqueId", null);
        } else {
            currentHost.put("system", false);
            currentHost.put("playerName", banker.nickName);
            currentHost.put("playerCoins", banker.money);
            currentHost.put("portrait", banker.portrait);
            currentHost.put("restHost", maxBanker - bankerIndex);
            currentHost.put("maxHost", maxBanker);
            currentHost.put("uniqueId", banker.uniqueId);
        }
        msg.put("currentHost", currentHost);
        msg.put("totalAmount", getCountMoney());
        msg.put("betable", gameStae);
        msg.put("restTime", time);
        msg.put("gameIndex", gameIndex);
        msg.put("history", history);
        msg.put("betInfo", chipList);
        msg.put("selfbetInfo", ((HCRoleInterface) role).getChipList());
        msg.put("betTime", chipTime);
        msg.put("endTime", openTime);
        msg.put("freeTime", waitTime);
        msg.put("tax", revenue);
        response.msg = msg;
        role.send(response);
    };

    public long getCountMoney() {
        long money = 0;
        for (int i = 0; i < chipList.length; ++i) {
            money += chipList[i].betAmount;
        }
        return money;
    }

    @Override
    public void onStart() {
        time = waitTime;
        gameStae = 2;
        GameMain game = GameMain.getInstance();
        schedule = game.getTaskMgr().createSchedule(new Callback() {
            @Override
            public void func() {
                try {
                    mainLoop();
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }, 1, this);
    };

    private void mainLoop() {
        time--;
        System.out.print(time);
        if (time == 0) {
            toNextState();
        }
    };

    private void toNextState() {
        switch (gameStae) {
        case 2:
            begin();
            runChipPeriod();
            sendChanegGameState();
            break;
        case 1:
            runOpenPeriod();
            sendChanegGameState();
            lottory();
            break;
        case 0:
            runWaitPeriod();
            end();
            break;
        }
        // Response response = new Response(2021, 1);
        // Map<String, Object> msg = new HashMap<>();
        // msg.put("roomId", room.roomId);
        // msg.put("currentPlayer", room.getRoles().size());
        // Map<String, Object> phaseData = new HashMap<>();
        // phaseData.put("status", gameStae);
        // phaseData.put("restTime", time);
        // msg.put("phaseData", phaseData);
        // response.msg = msg;
        // room.getHall().senToAll(response);
    };

    private void sendChanegGameState() {
        Response response = new Response(2012, 1);
        Map<String, Object> currentHost = new HashMap<>();
        if (banker == null) {
            currentHost.put("system", true);
            currentHost.put("playerName", null);
            currentHost.put("playerCoins", null);
            currentHost.put("hostCount", null);
            currentHost.put("hostMsg", "1111");
            currentHost.put("uniqueId", null);
        } else {
            currentHost.put("system", true);
            currentHost.put("playerName", banker.nickName);
            currentHost.put("playerCoins", banker.money);
            currentHost.put("hostCount", bankerIndex);
            currentHost.put("hostMsg", "1111");
            currentHost.put("uniqueId", banker.uniqueId);
        }
        response.msg = currentHost;
        broadcast(response);
    };

    private void lottory() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        for (int i = 0; i < 8; ++i) {
            int p = (int) (Math.random() * (list.size() + 1));
            if (banker instanceof Player) {
                snedLottoryMessage(p);
                return;
            } else {
                int b = list.remove(p);
                HCGameMain game = (HCGameMain) GameMain.getInstance();
                long win = 0;
                for (int j = 0; j < playerChipList.length; ++j) {
                    if (i != b) {
                        win += playerChipList[j];
                    }
                }
                long lose = playerChipList[b] * game.progress[b];
                win = win - lose;
                if (game.repertory + win > 0) {
                    snedLottoryMessage(p);
                    return;
                }
            }
        }

    };

    public void snedLottoryMessage(int p) {
        history.add(p);
        if (history.size() > 10) {
            history.remove(0);
        }
        Response response = new Response(2014, 1);
        Map<String, Object> msg = new HashMap<>();
        msg.put("gameIndex", gameIndex);
        int bei = ((HCGameMain) GameMain.getInstance()).progress[p];
        long bankerWin = 0;
        long playerTatle = 0;
        List<Map<String, Object>> otherPlayers = new ArrayList<>();
        for (Role player : chipPlayer) {
            Map<String, Object> playerInfo = new HashMap<>();
            playerInfo.put("playerName", player.nickName);
            ChipStruct[] playerChip = ((HCRoleInterface) player).getChipList();
            long playerWin = 0;
            for (int i = 0; i < playerChip.length; ++i) {
                if (i == p) {
                    playerWin += playerChip[i].betAmount * bei;
                } else {
                    playerWin -= playerChip[i].betAmount;
                }
            }
            bankerWin -= playerWin;
            if (playerWin > 0) {
                playerWin -= playerWin * revenue;
            }
            player.money += playerWin;
            // todo 保存金钱和历史
            playerInfo.put("playerCoins", player.money);
            playerInfo.put("selfSettlement", playerWin);
            playerInfo.put("uniqueId", player.uniqueId);
            otherPlayers.add(playerInfo);
            playerTatle += playerWin;
        }
        if (banker != null && bankerWin > 0) {
            bankerWin -= bankerWin * revenue;
        }
        if (playerTatle > 0) {
            playerTatle -= playerTatle * revenue;
        }
        msg.put("otherPlayers", otherPlayers);
        msg.put("bankerSettlement", bankerWin);
        msg.put("playerSettlement", playerTatle);
        response.msg = msg;
        broadcast(response);
        Response hallResponse = new Response(2021, 1);
        Map<String, Object> hallMsg = new HashMap<>();
        hallMsg.put("roomId", room.getRoomConfig().get("gameRoomId"));
        hallMsg.put("newReward", p);
        hallResponse.msg = hallMsg;
        room.getHall().senToAll(hallResponse);
    };

    private void runWaitPeriod() {
        time = waitTime;
        for (int i = 0; i < 8; ++i) {
            playerChipList[i] = 0;
            chipList[i].betAmount = 0;
        }

        for (Role role : chipPlayer) {
            ((HCRoleInterface) role).endGame();
        }

        chipPlayer.clear();
        if (bankerIndex == maxBanker) {
            banker = null;
            bankerIndex = 0;
        }
        gameStae = 2;
    };

    private void runOpenPeriod() {
        time = waitTime;
        gameStae = 0;
    };

    private void runChipPeriod() {
        if (banker == null) {
            if (bankerList.size() > 0) {
                String id = bankerList.remove(0);
                banker = roles.get(id);
            }
            bankerIndex = 1;
        } else {
            bankerIndex++;
        }
        gameIndex++;
        gameStae = 1;
        time = chipTime;
    };

    @Override
    protected void onDestroy() {
        schedule.stop();
    };

    @Override
    protected void onStop() {

    };

    @Override
    protected void onTerminate() {

    };
}