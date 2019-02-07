package com.micro.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.micro.frame.*;
import com.micro.frame.socket.ErrRespone;
import com.micro.frame.socket.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

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
    private @Getter List<Role> bankerList;
    private Schedule schedule;
    private int openTime;
    private int waitTime;
    private int chipTime;
    private boolean allowBank = false;
    private long bankMoney;
    private String roomName;
    private HashSet<Role> chipPlayer;

    private boolean callReady = false;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    protected void onInit() {
        Map<String, Object> roomConfig = room.getRoomConfig();
        openTime = Integer.valueOf((String) roomConfig.get("betTime"));
        waitTime = Integer.valueOf((String) roomConfig.get("freeTime"));
        chipTime = Integer.valueOf((String) roomConfig.get("betTime"));
        roomName = (String)roomConfig.get("roomName");
        if (openTime < 15) {
            openTime = 15;
        }
        if (waitTime < 5) {
            waitTime = 5;
        }
        if (chipTime < 15) {
            chipTime = 15;
        }
        if (roomConfig.get("taxRatio") != null) {
            revenue = Float.valueOf((String) roomConfig.get("taxRatio"));
        } else {
            revenue = 0;
        }
        maxBanker = Integer.valueOf((String) roomConfig.get("bankerTime"));
        minChip = Integer.valueOf((String) roomConfig.get("bottomRed1")) * 100;
        maxChip = Integer.valueOf((String) roomConfig.get("bottomRed2")) * 100;
        if (roomConfig.get("shangzhuangSwitch") != null) {
            allowBank = (Integer) roomConfig.get("shangzhuangSwitch") == 1;
        }

        bankMoney = Integer.valueOf((String) roomConfig.get("bankerCond"));
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
        bankerList.remove(role);
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
            List<Map<String, Integer>> list = (List<Map<String, Integer>>) obj;
            for (int i = 0; i < list.size(); ++i) {
                Map<String, Integer> info = list.get(i);
                nMoney += info.get("betAmount").longValue();
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
                Map<String, Integer> info = list.get(i);
                long pos = info.get("betTarget");
                long chipT = info.get("betAmount");
                if (pos >= 0 && pos < 8) {
                    ((HCRoleInterface) role).getChipList()[(int) pos].betAmount += chipT;
                    role.money -= chipT;
                    chipList[(int) pos].betAmount += chipT;
                }

                if (role instanceof Player) {
                    playerChipList[i] += chipT;
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
            playerInfo.add(bet);
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
            } else if (bankerList.contains(role)) {
                ErrRespone res = new ErrRespone(2009, 0, "你已经在列表里面了");
                role.send(res);
            } else {
                bankerList.add(role);
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
        if (bankerList.remove(role)) {
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
        isObserve.put("canPlay", minChip < role.money);
        isObserve.put("minMoney", minChip);
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
        msg.put("players", players);
        msg.put("selfCoins", role.money);
        Map<String, Object> hostSqeunce = new HashMap<>();
        for (Role player : bankerList) {
            Map<String, Object> host = new HashMap<>();
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
                    e.printStackTrace();
                    // TODO: handle exception
                }

            }
        }, 1, this);
    };

    private void mainLoop() {
        time--;
        System.out.println(time);
        if (time <= 0) {
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
        Response response = new Response(2021, 1);
        Map<String, Object> msg = new HashMap<>();
        msg.put("roomId", room.getRoomConfig().get("gameRoomId"));
        msg.put("currentPlayer", room.getRoles().size());
        Map<String, Object> phaseData = new HashMap<>();
        phaseData.put("status", gameStae);
        phaseData.put("restTime", time);
        msg.put("phaseData", phaseData);
        response.msg = msg;
        room.getHall().senToAll(response);
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
        Map<String, Object> msg = new HashMap<>();
        msg.put("betable", gameStae);
        msg.put("hostUpdate", currentHost);
        msg.put("gameIndex", gameIndex);
        response.msg = msg;
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
            int p = (int) (Math.random() * list.size());
            if (banker instanceof Player) {
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
            } else {
                snedLottoryMessage(p);
                return;
            }
        }

    };

    public void snedLottoryMessage(int p) {
        history.add(p);
        if (history.size() > 10) {
            history.remove(0);
        }
        int pos = 31 - ((int) Math.random() * 4) * 8 - p;
        Response response = new Response(2014, 1);
        Map<String, Object> msg = new HashMap<>();
        msg.put("rewardPosition", pos);
        msg.put("gameIndex", gameIndex);
        int bei = ((HCGameMain) GameMain.getInstance()).progress[p];
        long bankerWin = 0;
        long playerTatle = 0;
        List<Map<String, Object>> otherPlayers = new ArrayList<>();
        Map<String,Long> wins=new HashMap<>();
        Map<String,Object> betParts=new HashMap<>();
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
            betParts.put(player.uniqueId, playerChip);
            bankerWin -= playerWin;
            if (playerWin > 0) {
                playerWin -= playerWin * revenue / 100;
            }
            player.money += playerWin;
            playerInfo.put("playerCoins", player.money);
            playerInfo.put("selfSettlement", playerWin);
            playerInfo.put("uniqueId", player.uniqueId);
            otherPlayers.add(playerInfo);
            playerTatle += playerWin;
            wins.put(player.uniqueId, playerWin);
            Map<String,Object> palyerHistory=new HashMap<>();
            palyerHistory.put(player.uniqueId, gameUUID);
            mongoTemplate.save(palyerHistory, "benchi_playID_gameID");
        }
        if (banker != null && bankerWin > 0) {
            bankerWin -= bankerWin * revenue / 100;
            banker.money += bankerWin;
        }
        if (playerTatle > 0) {
            playerTatle -= playerTatle * revenue;
        }
        msg.put("otherPlayers", otherPlayers);
        msg.put("bankerSettlement", bankerWin);
        msg.put("playerSettlement", playerTatle);
        response.msg = msg;
        broadcast(response);
        Response hallResponse = new Response(2020, 1);
        Map<String, Object> hallMsg = new HashMap<>();
        hallMsg.put("roomId", room.getRoomConfig().get("gameRoomId"));
        hallMsg.put("newReward", p);
        hallResponse.msg = hallMsg;
        room.getHall().senToAll(hallResponse);
        
        //存入游戏记录的结构
        Map<String,Object> gameHistory=new HashMap<>();
        gameHistory.put("gameId", gameUUID);
        gameHistory.put("wins", wins);
        gameHistory.put("roomName", roomName);
        gameHistory.put("startTime", startTime);
        gameHistory.put("endTime", endTime);
        gameHistory.put("playerbetParts", betParts);
        gameHistory.put("chipList", chipList);
        gameHistory.put("tax", revenue);
        gameHistory.put("pos", p);
        mongoTemplate.save(gameHistory, "benchi_gameID_gameHistory");
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
        time = openTime;
        gameStae = 0;
    };

    private void runChipPeriod() {
        if (banker == null) {
            if (bankerList.size() > 0) {
                banker = bankerList.remove(0);
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