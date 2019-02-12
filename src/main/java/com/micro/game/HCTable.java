package com.micro.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import frame.*;
import frame.socket.ErrResponse;
import frame.socket.Response;

import frame.socket.SuccessResponse;

import lombok.Getter;

final class HCTable extends Table {
    private @Getter int time;
    private @Getter int gameStae;
    private @Getter int gameIndex = 0;
    private float revenue = 0;
    private ChipStruct[] chipList;               //桌子下注信息
    private long[] playerChipList = { 0, 0, 0, 0, 0, 0, 0, 0 };       //每个台上玩家下注量不包含机器人
    private int[] weightsList = { 3, 24, 4, 24, 4, 24, 12, 24 };
    public List<Integer> history;
    private int maxBanker;
    private int relMaxBank;
    private int extBanker;
    private long extBankerMoney;
    private int bankerIndex = 0;
    private long bankMoney;

    private int minChip;
    private int maxChip;
    private @Getter Role banker;
    private @Getter List<Role> bankerList;
    private Schedule schedule;
    private int openTime;
    private int waitTime;
    private int chipTime;
    private boolean allowBank = false;
    private HashSet<Role> chipPlayer;       //下过注的玩家

    @Override
    protected void onInit() {
        Map<String, Object> roomConfig = room.getRoomConfig();
        openTime = Integer.valueOf((String) roomConfig.get("betTime"));
        waitTime = Integer.valueOf((String) roomConfig.get("freeTime"));
        chipTime = Integer.valueOf((String) roomConfig.get("betTime"));
        roomName = (String) roomConfig.get("roomName");
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
        relMaxBank = maxBanker = Integer.valueOf((String) roomConfig.get("bankerTime"));
        bankMoney = Integer.valueOf((String) roomConfig.get("bankerCond"));
        extBanker = Integer.valueOf((String) roomConfig.get("addedTime"));
        extBankerMoney = Integer.valueOf((String) roomConfig.get("addedCond"));

        minChip = Integer.valueOf((String) roomConfig.get("bottomRed1")) * 100;
        maxChip = Integer.valueOf((String) roomConfig.get("bottomRed2")) * 100;
        if (roomConfig.get("shangzhuangSwitch") != null) {
            allowBank = (Integer) roomConfig.get("shangzhuangSwitch") == 1;
        }
        chipList = new ChipStruct[8];
        for (int i = 0; i < 8; ++i) {
            chipList[i] = new ChipStruct(i);
        }
        bankerList = new ArrayList<>();
        chipPlayer = new HashSet<>();
        history = new ArrayList<>();
    }
    /**
     * 玩家进入房间
     */
    @Override
    protected void onEnter(Role role) {
        SuccessResponse ownMsg = new SuccessResponse(2001, "45544454");
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
    /**
     * 玩家重连进入房间
     */
    @Override
    protected void onReEnter(Role role) {
        SuccessResponse ownMsg = new SuccessResponse(2001, "45544454");
        role.send(ownMsg);
    };
    /**
     * 玩家退出桌子
     */
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
    /**
     * 玩家下注
     * @param role
     * @param map   //客户端发过来的下注消息
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean playerChip(Role role, Map<String, Object> map) {
        if (gameStae != 1) {
            ErrResponse msg = new ErrResponse("不在下注阶段");
            role.send(msg);
            System.out.println("不在下注阶段  ");
            return false;
        }
        if (banker == role) {
            ErrResponse msg = new ErrResponse("庄家不能下注");
            role.send(msg);
            System.out.println("庄家不能下注  ");
            return false;
        }
        if ((int) map.get("gameIndex") != gameIndex) {
            ErrResponse msg = new ErrResponse("局数不对");
            role.send(msg);
            System.out.println("局数不对  ");
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
                ErrResponse msg = new ErrResponse("钱不够下注");
                System.out.println("钱不够下注  ");
                role.send(msg);
                return false;
            }
            long tempChip = ((HCRoleInterface) role).getChip() + nMoney;
            if (tempChip < minChip || tempChip > maxChip) {
                ErrResponse msg = new ErrResponse("下注不在允许范围");
                System.out.println("下注不在允许范围  ");
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

    /**
     * 玩家上庄
     * @param role
     */
    public void playerUpBanker(Role role) {
        if (allowBank) {
            if (role.money < bankMoney) {
                ErrResponse res = new ErrResponse("钱不够不能上庄");
                role.send(res);
            } else if (bankerList.contains(role)) {
                ErrResponse res = new ErrResponse("你已经在列表里面了");
                role.send(res);
            } else {
                bankerList.add(role);
                String size = String.valueOf(bankerList.size());
                SuccessResponse ownMsg = new SuccessResponse(2009, size);
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
    /**
     * 玩家下庄
     */
    public void playerDownBanker(Role role) {
        if (banker == role) {
            if (gameStae != 2) {
                ErrResponse msg = new ErrResponse("现在不能下庄");
                role.send(msg);
            } else {
                banker = null;
                SuccessResponse ownMsg = new SuccessResponse(2011, "离开庄家");
                Response otherMsg = new Response(2016, 1);
                otherMsg.msg = new HashMap<String, Object>();
                otherMsg.msg.put("playerName", role.nickName);
                otherMsg.msg.put("playerCoins", role.money);
                otherMsg.msg.put("token", role.token);
                otherMsg.msg.put("uniqueId", role.uniqueId);
                broadcast(ownMsg, otherMsg, role.uniqueId);
            }
        } else if (bankerList.remove(role)) {
            SuccessResponse ownMsg = new SuccessResponse(2011, "离开庄家列表");
            Response otherMsg = new Response(2016, 1);
            otherMsg.msg = new HashMap<String, Object>();
            otherMsg.msg.put("playerName", role.nickName);
            otherMsg.msg.put("playerCoins", role.money);
            otherMsg.msg.put("token", role.token);
            otherMsg.msg.put("uniqueId", role.uniqueId);
            broadcast(ownMsg, otherMsg, role.uniqueId);
        }
    };
    /**
     * 玩家亲求桌子
     * @param role
     */
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
            currentHost.put("restHost", relMaxBank - bankerIndex);
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
        msg.put("hostable", allowBank);
        response.msg = msg;
        role.send(response);
    };
    /**
     * 获取所有下注金额
     * @return
     */
    public long getCountMoney() {
        long money = 0;
        for (int i = 0; i < chipList.length; ++i) {
            money += chipList[i].betAmount;
        }
        return money;
    }
    /**
     * 检查玩家是不是庄家
     * @param role
     * @return
     */
    public boolean checkBanker(Role role) {
        return banker == role;
    };

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
    /**
     * 游戏主循环
     */
    private void mainLoop() {
        if (time < 0) {
            System.out.println(time);
        }
        time--;
        if (time <= 0) {
            toNextState();
        }
    };
    /**
     * 切换游戏状态
     */
    private void toNextState() {
        switch (gameStae) {
        case 2:
            if (begin()) {
                runChipPeriod();
                sendChanegGameState();
            } else {
                schedule.stop();
                return;
            }
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
        room.getHall().broadcast(response);
    };
    /**
     * 发送游戏状态改变给桌子上的人
     */
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
            currentHost.put("system", false);
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
    /**
     * 开奖
     */
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
            int b = getOpenNumber(list);
            int p = list.remove(b);
            if (banker instanceof Player) {
                HCGameMain game = (HCGameMain) GameMain.getInstance();
                long win = 0;
                for (int j = 0; j < playerChipList.length; ++j) {
                    if (i != b) {
                        win += playerChipList[j];
                    }
                }
                long lose = playerChipList[b] * game.progress[b];
                win = win - lose;
                if (room.getHall().stock + win >= 0) {
                    sendLottoryMessage(p, win);
                    return;
                }
            } else {
                sendLottoryMessage(p, 0);
                return;
            }
        }
    };
    /**
     * 通过权重计算开的奖
     * @param list  可以开的号码
     * @return
     */
    private int getOpenNumber(List<Integer> list) {
        int weights = 0;
        for (int i = 0; i < list.size(); ++i) {
            weights += weightsList[list.get(i)];
        }
        int open = (int) (Math.random() * weights);
        int temp = 0;
        int i = 0;
        for (; i < list.size(); ++i) {
            temp += weightsList[list.get(i)];
            if (temp > open) {
                break;
            }
        }
        return i;
    }
    /**
     * 发送开奖结果
     * @param p     开的点数
     * @param stock   库存变化
     */
    public void sendLottoryMessage(int p, long stock) {
        history.add(p);
        if (history.size() > 10) {
            history.remove(0);
        }
        int pos = ((int) Math.random() * 4) * 8 + p;
        Response response = new Response(2014, 1);
        Map<String, Object> msg = new HashMap<>();
        msg.put("rewardPosition", pos);
        msg.put("gameIndex", gameIndex);
        int bei = ((HCGameMain) GameMain.getInstance()).progress[p];
        long bankerWin = 0;
        long sysTax = 0;
        // long playerTatle = 0;
        List<Map<String, Object>> otherPlayers = new ArrayList<>();
        Map<String, Long> wins = new HashMap<>();
        Map<String, Object> betParts = new HashMap<>();
        HashMap<String, Long> opens = new HashMap<>();
        for (Role player : chipPlayer) {
            Map<String, Object> playerInfo = new HashMap<>();
            playerInfo.put("playerName", player.nickName);
            ChipStruct[] playerChip = ((HCRoleInterface) player).getChipList();
            long playerWin = 0;
            long getMon = 0;
            for (int i = 0; i < playerChip.length; ++i) {
                if (i == p) {
                    playerWin += playerChip[i].betAmount * bei;
                    getMon += playerChip[i].betAmount * bei;
                } else {
                    playerWin -= playerChip[i].betAmount;
                }
            }
            opens.put(player.uniqueId, getMon);
            betParts.put(player.uniqueId, playerChip);
            bankerWin -= playerWin;
            if (playerWin > 0) {
                sysTax += playerWin * revenue / 100;
                playerWin -= playerWin * revenue / 100;
            }
            player.money += getMon;
            playerInfo.put("playerCoins", player.money);
            playerInfo.put("selfSettlement", playerWin);
            playerInfo.put("uniqueId", player.uniqueId);
            otherPlayers.add(playerInfo);
            // playerTatle += playerWin;
            wins.put(player.uniqueId, playerWin);
            if (player instanceof Player) {
                // Map<String, Object> palyerHistory = new HashMap<>();
                // palyerHistory.put(player.uniqueId, gameUUID);
                player.savePlayerHistory(gameUUID);
            }
        }
        if (banker != null) {
            if (bankerWin > 0) {
                sysTax += bankerWin * revenue / 100;
                bankerWin -= bankerWin * revenue / 100;
                banker.money += bankerWin;
            }
            wins.put(banker.uniqueId, bankerWin);
            Map<String, Object> bankerInfo = new HashMap<>();
            bankerInfo.put("playerCoins", banker.money);
            bankerInfo.put("selfSettlement", bankerWin);
            bankerInfo.put("uniqueId", banker.uniqueId);
            otherPlayers.add(bankerInfo);
            if (banker instanceof Player) {
                banker.savePlayerHistory(gameUUID);
            }
        }

        // if (playerTatle > 0) {
        // playerTatle -= playerTatle * revenue/100;
        // }
        msg.put("otherPlayers", otherPlayers);
        msg.put("bankerSettlement", bankerWin);
        // msg.put("playerSettlement", playerTatle);
        response.msg = msg;
        broadcast(response);
        Response hallResponse = new Response(2020, 1);
        Map<String, Object> hallMsg = new HashMap<>();
        hallMsg.put("roomId", room.getRoomConfig().get("gameRoomId"));
        hallMsg.put("newReward", p);
        hallResponse.msg = hallMsg;
        room.getHall().broadcast(hallResponse);

        BenChiGameHistory gameHistory = new BenChiGameHistory();
        gameHistory.wins = wins;
        gameHistory.playerbetParts = betParts;
        gameHistory.chipList = chipList;
        gameHistory.sysHost = banker == null ? null : banker.uniqueId;
        gameHistory.tax = String.valueOf(revenue);
        gameHistory.opens = opens;
        gameHistory.open = p;
        result(stock, sysTax, gameHistory);

    };
    /**
     * 进入等待状态
     */
    private void runWaitPeriod() {
        changeBanker();
        for (int i = 0; i < 8; ++i) {
            playerChipList[i] = 0;
            chipList[i].betAmount = 0;
        }

        for (Role role : chipPlayer) {
            ((HCRoleInterface) role).endGame();
        }

        chipPlayer.clear();
        time = waitTime;
        gameStae = 2;
    };
    /**
     * 计算庄家
     */
    private void changeBanker() {
        if (bankerIndex == relMaxBank) {
            if (maxBanker < relMaxBank) {
                if (banker.money >= extBankerMoney) {
                    relMaxBank += extBanker;
                }
            } else {
                banker = null;
                bankerIndex = 0;
            }
        }
    }
    /**
     * 进入开奖状态
     */
    private void runOpenPeriod() {
        time = openTime;
        gameStae = 0;
    };
    /**
     * 进入下注状态
     */
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