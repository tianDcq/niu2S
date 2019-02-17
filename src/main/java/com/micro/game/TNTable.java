package com.micro.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import frame.*;
import frame.socket.Response;
import frame.util.NiuUtil;
import frame.util.RandomUtil;
import frame.util.pukeUtil;
import lombok.Getter;

final class TNTable extends Table {
    private @Getter int gameStae; // 1叫庄 2 下注 3 开拍
    private int roomId;
    private Schedule schedule;
    private int time;
    private int bankTime;
    private int chipTime;
    private int minMoney;
    private float tax;
    private int[] ant;
    private pukeUtil puke;
    public Role[] playerList;
    private Role currRole;
    private Role banker;
    private int openN = 0;

    @Override
    protected void onInit() {
        Map<String, Object> roomConfig = room.getRoomConfig();
        roomId = (int) roomConfig.get("gameRoomId");
        bankTime = (int) roomConfig.get("callTime");
        chipTime = (int) roomConfig.get("betTime");
        if (roomConfig.get("taxRatio") != null) {
            tax = Float.valueOf((String) roomConfig.get("taxRatio"));
        } else {
            tax = 0;
        }
        minMoney = Integer.valueOf((String) roomConfig.get("minMoney")).intValue();
        int[] temp = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x11, 0x12, 0x13,
                0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27,
                0x28, 0x29, 0x2a, 0x2b, 0x2c, 0x2d, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x3b,
                0x3c, 0x3d };
        puke = new pukeUtil(temp);
    }

    @Override
    public void onStart() {
        if (begin()) {
            puke.shuffle();
            Response mm = new Response(8022, 1);
            Map<String, Object> msg = new HashMap<>();
            playerList = new Role[2];
            Object[] playerInfoList = new Object[2];
            int i = 0;
            for (Role role : roles.values()) {
                Map<String, Object> playerInfo = new HashMap<>();
                ((TNRoleInterface) role).setSit(i);
                playerList[i] = role;
                playerInfo.put("seatNum", i);
                playerInfo.put("nickName", role.nickName);
                playerInfo.put("portrait", role.portrait);
                playerInfo.put("coins", role.money);
                playerInfo.put("uniqueId", role.uniqueId);
                playerInfoList[i] = playerInfo;
                ++i;
            }
            msg.put("playerInfo", playerInfoList);
            mm.msg = msg;
            broadcast(mm);
            BankPeriod();
            schedule = GameMain.getInstance().getTaskMgr().createSchedule(new Callback() {
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
        }
    };

    private void BankPeriod() {
        gameStae = 1;
        Response mm1 = new Response(8015, 1);
        Map<String, Object> msg1 = new HashMap<>();
        Role r = playerList[0];
        currRole = r;
        ((TNRoleInterface) r).setPlayerState(1);
        ((TNRoleInterface) playerList[1]).setPlayerState(6);
        msg1.put("callDealerSeatNum", r.uniqueId);
        msg1.put("fieldNum", roomId);
        msg1.put("gameCode", gameUUID);
        msg1.put("tableNum", id);
        msg1.put("stage", gameStae);
        mm1.msg = msg1;
        broadcast(mm1);
        time = bankTime;
    }

    private void ChipPeriod() {
        gameStae = 2;
        Response oRes = new Response(8015, 1);
        Map<String, Object> msg = new HashMap<>();
        msg.put("fieldNum", roomId);
        msg.put("gameCode", gameUUID);
        msg.put("stage", gameStae);
        msg.put("tableNum", id);
        msg.put("dealerSeatNum", banker.uniqueId);
        setAntBet();
        msg.put("antBets", ant);
        oRes.msg = msg;
        broadcast(oRes);
        int currSit = ((TNRoleInterface) banker).getSit() + 1;
        if (currSit > playerList.length) {
            currSit = 0;
        }
        currRole = playerList[currSit];
        ((TNRoleInterface) currRole).setPlayerState(3);
        ((TNRoleInterface) banker).setPlayerState(4);
        time = chipTime;
    }

    public void setAntBet() {
        ant = new int[4];
        long min = playerList[0].money < playerList[1].money ? playerList[0].money : playerList[1].money;
        int x = minMoney / 15;
        int y;
        if (minMoney / min > 2) {
            y = 2 * x;
        } else {
            y = new Long(min / 15).intValue();
        }
        int m = RandomUtil.ramdom(x, y);
        for (int i = 0; i < 4; ++i) {
            ant[i] = m;
            m = m / 2;
        }
    }

    public void playerChip(Role role, int num) {
        if (currRole == role) {
            if (num < ant.length) {
                ((TNRoleInterface) currRole).setChipNum(num);
                Response ownRes = new Response(8010, 1);
                Response oRes = new Response(8011, 1);
                Map<String, Object> msg = new HashMap<>();
                msg.put("chipInAmount", ant[num]);
                msg.put("uniqueId", role.uniqueId);
                ownRes.msg = oRes.msg = msg;
                broadcast(ownRes, oRes, role.uniqueId);
                disCard();
            }
        }
    }

    public void playerBanker(Role role, int num) {
        if (role == currRole) {
            ((TNRoleInterface) role).setPlayerState(2);
            Response oRes = new Response(8008, 1);
            Response otRes = new Response(8009, 1);
            Map<String, Object> msg = new HashMap<>();
            msg.put("uniqueId", role.uniqueId);
            msg.put("callType", num);
            oRes.msg = msg;
            otRes.msg = msg;
            broadcast(oRes, otRes, role.uniqueId);
            ((TNRoleInterface) role).setBankNum(num);
            int sit = ((TNRoleInterface) role).getSit() + 1;
            if (sit < playerList.length) {
                TNRoleInterface next = (TNRoleInterface) playerList[sit];
                next.setPlayerState(1);
                currRole = (Role) next;
                time = bankTime;
            } else {
                choseBanker();
                ChipPeriod();
            }
        }
    }

    private void disCard() {
        List<List<Integer>> cardsList = new ArrayList<>();
        for (int k = 0; k < playerList.length; ++k) {
            ((TNRoleInterface) playerList[k]).setPlayerState(5);
            List<Integer> cards = new ArrayList<>();
            for (int j = 0; j < 5; ++j) {
                cards.add(puke.getPuke());
            }
            cardsList.add(cards);
        }
        gameStae = 3;
        // 计算
        countResoult(cardsList);
        time=chipTime;
    }

    private void countResoult(List<List<Integer>> cardList) {
        List<Integer> cows = new ArrayList<>();
        for (int i = 0; i < playerList.length; ++i) {
            List<Integer> cards = cardList.get(i);
            ((TNRoleInterface) playerList[i]).setCards(cards);
            ((TNRoleInterface) playerList[i]).setPlayerState(5);
            List<Integer> cardDate = new ArrayList<>();
            for (int v : cards) {
                if (pukeUtil.getValue(v) > 10) {
                    cardDate.add(10);
                } else {
                    cardDate.add(v);
                }
            }

            int cow = NiuUtil.getNiu(cardDate).get(0);
            if (cow == 10) {
                cow = 12;
                for (int j = 0; j < cards.size(); ++j) {
                    if (pukeUtil.getValue(cards.get(j)) < 10) {
                        cow = 10;
                        break;
                    } else if (pukeUtil.getValue(cards.get(j)) < 11) {
                        if (cow == 12) {
                            cow = 11;
                        }
                    }
                }
            }
            cows.add(cow);
        }
        int bankerSit = ((TNRoleInterface) banker).getSit();
        int bankerCow = cows.get(bankerSit);
        List<Integer> bankerCards = cardList.get(bankerSit);
        int bankerMax = bankerCards.get(0);
        for (int i = 1; i < bankerCards.size(); ++i) {
            if (pukeUtil.comPairCard(bankerCards.get(i), bankerMax)) {
                bankerMax = bankerCards.get(i);
            }
        }
        Response response = new Response(8015, 1);
        Map<String, Object> msg = new HashMap<>();
        response.msg = msg;
        msg.put("fieldNum", roomId);
        msg.put("gameCode", gameUUID);
        msg.put("tableNum", id);
        msg.put("stage", gameStae);
        Object[] pokers = new Object[playerList.length];
        Map<String, Object> bankInfo = new HashMap<>();
        Map<String, Object> pukeInfo = new HashMap<>();
        bankInfo.put("pokers", bankerCards);
        bankInfo.put("pattern", bankerCow);
        bankInfo.put("seatNum", banker.uniqueId);
        pokers[0] = bankInfo;
        pokers[1] = pukeInfo;
        int bankNum = ((TNRoleInterface) banker).getBankNum();// 💪背书
        int cardNum = 0; // 🥧
        long playerWin = 0;
        long stackWin = 0;
        int chip = 0;

        TNGameHistory history = new TNGameHistory();
        Map<String, Long> wins = new HashMap<>();
        Map<String, Object> cards = new HashMap<>();
        Map<String, Integer> cardType = new HashMap<>();
        history.tax = tax;
        history.bankNum = bankNum;
        history.downNum = chip;
        history.wins = wins;
        history.cards = cards;
        history.cardType = cardType;

        for (int j = 0; j < playerList.length; ++j) {
            Role player = playerList[j];
            if (player != banker) {
                int playerSit = ((TNRoleInterface) player).getSit();
                int playerCow = cows.get(playerSit);
                List<Integer> playerCards = ((TNRoleInterface) player).getCards();
                pukeInfo.put("pokers", playerCards);
                pukeInfo.put("pattern", playerCow);
                pukeInfo.put("seatNum", player.uniqueId);
                int playerMax = playerCards.get(0);
                for (int i = 1; i < playerCards.size(); ++i) {
                    if (pukeUtil.comPairCard(playerCards.get(i), bankerMax)) {
                        playerMax = playerCards.get(i);
                    }
                }
                if (playerCow > bankerCow) {
                    pukeInfo.put("winner", 1);
                    bankInfo.put("winner", 0);
                    cardNum = getbei(playerCow);
                } else if (playerCow < bankerCow) {
                    pukeInfo.put("winner", 0);
                    bankInfo.put("winner", 1);
                    cardNum = getbei(bankerCow);
                } else {
                    if (pukeUtil.comPairCard(playerMax, bankerMax)) {
                        pukeInfo.put("winner", 1);
                        bankInfo.put("winner", 0);
                        cardNum = getbei(playerCow);
                    } else {
                        pukeInfo.put("winner", 0);
                        bankInfo.put("winner", 1);
                        cardNum = getbei(bankerCow);
                    }
                }
                chip = ant[((TNRoleInterface) player).getChipNum()];
                playerWin = chip * bankNum * cardNum;
                if (banker instanceof Robot) {
                    if (room.getHall().stock - playerWin < 0) {
                        List<List<Integer>> nCards = new ArrayList<>();
                        nCards.add(cardList.get(1));
                        nCards.add(cardList.get(0));
                        countResoult(nCards);
                        return;
                    }
                    stackWin -= playerWin;
                } else if (player instanceof Robot) {
                    if (room.getHall().stock + playerWin < 0) {
                        List<List<Integer>> nCards = new ArrayList<>();
                        nCards.add(cardList.get(1));
                        nCards.add(cardList.get(0));
                        countResoult(nCards);
                        return;
                    }
                    stackWin += playerWin;
                }

                if (playerWin > 0) {
                    player.money += playerWin * (1 - tax);
                    banker.money -= playerWin;
                    pukeInfo.put("coins", playerWin * (1 - tax));
                    bankInfo.put("coins", 0 - playerWin);
                } else {
                    player.money += playerWin;
                    pukeInfo.put("coins", playerWin);
                    banker.money -= playerWin * (1 - tax);
                    bankInfo.put("coins", 0 - playerWin * (1 - tax));
                }

                wins.put(banker.uniqueId, 0 - playerWin);
                wins.put(player.uniqueId, playerWin);
                cards.put(banker.uniqueId, ((TNRoleInterface) banker).getCards());
                cards.put(player.uniqueId, ((TNRoleInterface) player).getCards());
                cardType.put(banker.uniqueId, bankerCow);
                cardType.put(player.uniqueId, playerCow);
            }
        }
        long taxN = 0;
        taxN += playerWin * tax;
        result(stackWin, taxN, history);
        msg.put("pokers", pokers);
        broadcast(response);
    }

    public int getbei(int cardType) {
        switch (cardType) {
        case 12:
        case 11:
        case 10:
            return 5;
        case 9:
            return 4;
        case 8:
            return 3;
        case 7:
            return 2;
        default:
            return 1;
        }
    }

    public void choseBanker() {
        banker = playerList[0];
    }

    public void playerOpen(Role role) {
        if (gameStae == 3) {
            Response ownRes = new Response(8012, 1);
            Response oRes = new Response(8013, 1);
            Map<String, Object> msg = new HashMap<>();
            msg.put("uniqueId", role.uniqueId);
            ownRes.msg = msg;
            oRes.msg = msg;
            openN++;
            broadcast(ownRes, oRes, role.uniqueId);
            ((TNRoleInterface) role).setPlayerState(6);
            if (openN == playerList.length) {
                schedule.stop();
                for (Role pp : playerList) {
                    ((TNRoleInterface) pp).setPlayerState(0);
                    pp.savePlayerHistory(gameUUID);
                }
                end();
            }

        }
    }

    /**
     * 游戏主循环
     */
    private void mainLoop() {
        time--;
        if (time <= 0) {
            toNextState();
        }
    };

    public void toNextState() {
        switch (gameStae) {
        case 1: {
            playerBanker(currRole, 0);
            break;
        }
        case 2: {
            playerChip(currRole, 0);
            break;
        }
        case 3: {
            for (Role role : playerList) {
                if (((TNRoleInterface) role).getPlayerState() == 5) {
                    playerOpen(role);
                }
            }
            break;
        }

        }
    }

    @Override
    protected void onEnter(Role role) {
    };

    @Override
    protected void onReEnter(Role role) {
    };

    @Override
    protected void onExit(Role role) {
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