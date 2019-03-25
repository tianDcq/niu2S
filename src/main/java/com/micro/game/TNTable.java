package com.micro.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import frame.game.*;
import frame.*;
import frame.socket.Response;
import frame.socket.common.proto.LobbySiteRoom.PkRoomCfg;
import frame.storageLogic.LotteryModel;
import frame.util.NiuUtil;
import frame.util.RandomUtil;
import frame.util.pukeUtil;
import lombok.Getter;

import com.alibaba.fastjson.JSON;
import com.micro.game.TowNiuMessage.*;

final class TNTable extends Table {
    private @Getter int gameStae; // 1叫庄 2 下注 3 开拍
    private final int[] bankPre = { 1, 2, 3, 4 };
    private Schedule schedule;
    private int time;
    public int bankTime;
    public int chipTime;
    private int minMoney;
    private float tax;
    private int[] ant;
    private pukeUtil puke;
    public Role[] playerList;
    private Role currRole;
    private Role banker;
    private int openN = 0;
    private int chip = 0;
    private int bankNum = 0;

    @Override
    protected void onInit() {
        PkRoomCfg cfg = getPkRoomCfg();
        bankTime = cfg.getCallTime();
        chipTime = cfg.getBetTime();
        tax = cfg.getTaxRatio();
        minMoney = (int) (cfg.getMinMoney() * 1000);
        int[] temp = { 0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70, 0x80, 0x90, 0xa0, 0xb0, 0xc0, 0xd0, 0x11, 0x21, 0x31,
                0x11, 0x51, 0x61, 0x71, 0x81, 0x91, 0xa1, 0xb1, 0xc1, 0xd1, 0x12, 0x22, 0x32, 0x42, 0x52, 0x62, 0x72,
                0x82, 0x92, 0xa2, 0xb2, 0xc2, 0xd2, 0x13, 0x23, 0x33, 0x43, 0x53, 0x63, 0x73, 0x83, 0x93, 0xa3, 0xb3,
                0xc3, 0xd3 };
        puke = new pukeUtil(temp);
    }

    @Override
    public ArrayList<LotteryModel> getAllLotteryModel() {
        ArrayList<LotteryModel> modelList = new ArrayList<LotteryModel>();
        puke.shuffle();
        List<List<Integer>> cardsList = new ArrayList<>();
        for (int k = 0; k < playerList.length; ++k) {
            ((TNRoleInterface) playerList[k]).setPlayerState(5);
            List<Integer> cards = new ArrayList<>();
            for (int j = 0; j < 5; ++j) {
                cards.add(puke.getPuke());
            }
            cardsList.add(cards);
        }

        List<Integer> cows = new ArrayList<>();
        int winSit = 0;
        int maxCow = 0;
        for (int i = 0; i < cardsList.size(); ++i) {
            List<Integer> cards = cardsList.get(i);
            int cow = NiuUtil.getNiu(cards).cow;
            if (cow == 10) {
                cow = NiuUtil.getNiuType(cards.stream().mapToInt(Integer::valueOf).toArray());
            }

            int maxCard = pukeUtil.getMxa(cards.stream().mapToInt(Integer::valueOf).toArray());
            int cardType = cow << 8 | maxCard;
            if (cardType > maxCow) {
                maxCow = cardType;
                winSit = i;
            }
            cows.add(cardType);
        }
        int bei = getbei(maxCow >> 8);
        int bankP = bankPre[((TNRoleInterface) banker).getBankNum()];
        long win = ant[chip] * bei * bankP;
        for (int start = 0; start < cardsList.size(); ++start) {
            LotteryModel lotteryModel = new LotteryModel();
            Map<String, Object> lotteryResult = new HashMap<>();
            lotteryResult.put("cards", cardsList);
            lotteryResult.put("cows", cows);
            lotteryResult.put("start", start);
            lotteryResult.put("win", win);
            lotteryResult.put("winSit", winSit);
            lotteryModel.lotteryResult = lotteryResult;
            lotteryModel.lotteryWeight = 1;
            lotteryModel.systemWin = countStack(cows, winSit, win, start);
            modelList.add(lotteryModel);
        }

        return modelList;
    }

    @Override
    public void onStart() {
        if (begin()) {
            puke.shuffle();
            playerList = new Role[2];
            ResStart.Builder res = ResStart.newBuilder();
            int i = 0;
            for (Role role : roles.values()) {
                ((TNRoleInterface) role).setSit(i);
                ((TNRoleInterface) role).setPlayerState(1);
                playerList[i] = role;
                playerInfo.Builder player = playerInfo.newBuilder();
                player.setName(role.nickName);
                player.setHead(role.portrait);
                player.setCoin(role.money);
                player.setPosId(i);
                player.setPlayerState(1);
                res.addPlayers(player);
                i++;
            }
            res.setCurrPos(0);
            currRole = playerList[0];
            for (int j = 0; j < playerList.length; ++j) {
                res.setOwnPos(j);
                playerList[j].send(new Response(TwoNiuConfig.ResStart, res.build().toByteArray()));
            }
            BankPeriod();
            if (schedule != null) {
                schedule.stop();
            }
            schedule = UtilsMgr.getTaskMgr().createSchedule(new Callback() {
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
        time = bankTime;
    }

    private void ChipPeriod() {
        gameStae = 2;
        setAntBet();
        int currSit = ((TNRoleInterface) banker).getSit() + 1;
        if (currSit > playerList.length - 1) {
            currSit = 0;
        }
        currRole = playerList[currSit];
        ((TNRoleInterface) currRole).setPlayerState(3);
        ((TNRoleInterface) banker).setPlayerState(4);
        ResBetProd.Builder res1 = ResBetProd.newBuilder();
        res1.setBanker(((TNRoleInterface) banker).getSit());
        res1.setCurrPos(currSit);
        res1.addAllAllBets(Arrays.stream(ant).boxed().collect(Collectors.toList()));
        broadcast(new Response(TwoNiuConfig.ResBetProd, res1.build().toByteArray()));
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
                ResBet.Builder res = ResBet.newBuilder();
                res.setPosId(((TNRoleInterface) currRole).getSit());
                res.setBet(num);
                broadcast(new Response(TwoNiuConfig.ResBet, res.build().toByteArray()));
                chip = num;
                openN = 0;
                disCard();
            }
        }
    }

    public void playerBanker(Role role, int num) {
        if (role == currRole) {
            ((TNRoleInterface) role).setPlayerState(2);
            ((TNRoleInterface) role).setBankNum(num);
            int sit = ((TNRoleInterface) role).getSit() + 1;
            ResBnaker.Builder res = ResBnaker.newBuilder();
            res.setBankNum(num);
            res.setPosId(sit - 1);
            if (sit < playerList.length) {
                TNRoleInterface next = (TNRoleInterface) playerList[sit];
                next.setPlayerState(1);
                currRole = (Role) next;
                res.setCurrPos(sit);
                broadcast(new Response(TwoNiuConfig.ResBnaker, res.build().toByteArray()));
                time = bankTime;
            } else {
                res.setCurrPos(-1);
                broadcast(new Response(TwoNiuConfig.ResBnaker, res.build().toByteArray()));
                choseBanker();
                ChipPeriod();
            }
        }
    }

    public void playerOpen(Role role) {
        if (gameStae == 3) {
            ResShowCard.Builder res = ResShowCard.newBuilder();
            res.setPosId(((TNRoleInterface) role).getSit());
            broadcast(new Response(TwoNiuConfig.ResShowCard, res.build().toByteArray()));
            openN++;
            ((TNRoleInterface) role).setPlayerState(6);
            if (openN == playerList.length) {
                for (Role pp : playerList) {
                    ((TNRoleInterface) pp).setPlayerState(0);
                    pp.savePlayerHistory(gameUUID);
                }
                clearGame();
                end();
            }
        }
    }

    private void disCard() {
        gameStae = 3;
        time = chipTime;
        Map<String, Object> resoult = (Map<String, Object>) (room.getStorageMgr().lottery(this).lotteryResult);
        int bankP = bankPre[((TNRoleInterface) banker).getBankNum()];
        int start = (int) resoult.get("start");
        List<List<Integer>> cardList = (List<List<Integer>>) resoult.get("cards");
        List<Integer> cows = (List<Integer>) resoult.get("cows");
        int winSit = (int) resoult.get("winSit");
        long win = (long) resoult.get("win");

        Map<String, Object> history = new HashMap<>();
        Map<Integer, Long> wins = new HashMap<>();
        history.put("bankNum", bankP);
        history.put("downNum", ant[chip]);
        history.put("bankSit", ((TNRoleInterface) banker).getSit());

        Object[] playerHList = new Object[playerList.length];
        int size = playerList.length;
        long taxN = 0;
        for (int i = 0; i < size; ++i) {
            int csit = (start + i) % size;
            TNRoleInterface player = (TNRoleInterface) playerList[csit];
            player.setCards(cardList.get(i));
            player.setCow(cows.get(i));
            Map<String, Object> playerhis = new HashMap<>();
            if (i == winSit) {
                long ww = (long) (win * (1 - tax));
                player.setWin(ww);
                playerhis.put("win", win);
                if (playerList[csit] instanceof Player) {
                    taxN += win * tax;
                    wins.put(playerList[i].userId, win);
                }
            } else {
                player.setWin(0 - win);
                if (playerList[csit] instanceof Player) {
                    wins.put(playerList[i].userId, 0 - win);
                }
                playerhis.put("win", 0 - win);
            }
            // 历史纪录
            playerhis.put("pokers", cardList.get(i));
            playerhis.put("cow", cows.get(i));
            playerhis.put("bet", player.getChipNum());
            playerhis.put("bank", player.getBankNum());
            playerhis.put("sit", player.getSit());
            playerHList[i] = playerhis;
        }
        result(taxN, wins, JSON.toJSONString(history));
        ResDisCards.Builder res = ResDisCards.newBuilder();
        for (int i = 0; i < playerList.length; ++i) {
            TNRoleInterface pp = (TNRoleInterface) playerList[i];
            res.addCards(ArrayInt.newBuilder().addAllList(pp.getCards()));
            res.addCows(pp.getCow());
            res.addWins(pp.getWin());
        }
        broadcast(new Response(TwoNiuConfig.ResDisCards, res.build().toByteArray()));

    }

    private long countStack(List<Integer> cows, int winPos, long win, int startSit) {
        long sysWin = 0;
        int size = cows.size();
        int winSet = (startSit + winPos) % size;
        for (int i = 0; i < size; ++i) {
            int mSit = (startSit + i) % size;
            if (mSit != winSet) {
                if (playerList[winSet] instanceof Player) {
                    if (playerList[mSit] instanceof Robot) {
                        sysWin -= win * (1 - tax);
                    }
                } else if (playerList[mSit] instanceof Player) {
                    sysWin += win;
                }
            }
        }
        return sysWin;
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

        for (int i = 1; i < playerList.length; ++i) {
            if (((TNRoleInterface) playerList[i]).getBankNum() > ((TNRoleInterface) banker).getBankNum()) {
                banker = playerList[i];
            }
        }
        bankNum = ((TNRoleInterface) banker).getBankNum();
    }

    public void clearGame() {
        playerList = null;
        currRole = null;
        banker = null;
        openN = 0;
        chip = 0;
        bankNum = 0;
        gameStae = 0;
        schedule.stop();
    }

    public void getUpdateTable(Role role) {
        ResTableInfo.Builder res = ResTableInfo.newBuilder();
        res.setGameState(0);
        ResTableInfo.timeConfig.Builder timec = ResTableInfo.timeConfig.newBuilder();
        timec.setPairTime(bankTime);
        timec.setBankerTime(bankTime);
        timec.setChipTime(chipTime);
        timec.setShowTime(chipTime);
        timec.setTime(time);
        res.setTimeCf(timec);
        res.setGameState(gameStae);
        res.setCurrPos(((TNRoleInterface) currRole).getSit());
        res.setBet(chip);
        res.addAllBets(Arrays.stream(ant).boxed().collect(Collectors.toList()));
        if (banker != null) {
            res.setBankNum(bankNum);
            res.setBankerPos(((TNRoleInterface) banker).getSit());
        }
        for (int i = 0; i < playerList.length; ++i) {
            playerInfo.Builder player = playerInfo.newBuilder();
            TNRoleInterface pp = (TNRoleInterface) playerList[i];
            player.setBankerNum(pp.getBankNum());
            player.setName(playerList[i].nickName);
            player.setHead(playerList[i].portrait);
            player.setCoin(playerList[i].money);
            player.setPosId(pp.getSit());
            player.setBet(pp.getChipNum());
            player.addAllCards(pp.getCards());
            player.setPlayerState(pp.getPlayerState());
            res.addPalyers(player);
        }
        role.send(new Response(TwoNiuConfig.ResTableInfo, res.build().toByteArray()));
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
        role.send(new Response(TwoNiuConfig.ResEnter, ResEnter.newBuilder().setEnter(true).build().toByteArray()));
    };

    @Override
    protected void onReEnter(Role role) {
    };

    @Override
    protected void onExit(Role role) {

    };

    @Override
    protected void onDestroy() {
        if (schedule != null) {
            schedule.stop();
            schedule = null;
        }
    };

    @Override
    protected void onStop() {

    };

    @Override
    protected void onTerminate() {

    };
}