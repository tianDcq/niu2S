package com.micro.game;

import java.util.ArrayList;
import java.util.HashMap;
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
    private final int[] bankPre={1,2,3,4};
    private int roomId;
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
        int[] temp = { 0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70, 0x80, 0x90, 0xa0, 0xb0, 0xc0, 0xd0, 0x11, 0x21, 0x31,
            0x11, 0x51, 0x61, 0x71, 0x81, 0x91, 0xa1, 0xb1, 0xc1, 0xd1, 0x12, 0x22, 0x32, 0x42, 0x52, 0x62, 0x72,
            0x82, 0x92, 0xa2, 0xb2, 0xc2, 0xd2, 0x13, 0x23, 0x33, 0x43, 0x53, 0x63, 0x73, 0x83, 0x93, 0xa3, 0xb3,
            0xc3, 0xd3 };
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
                ((TNRoleInterface) role).setPlayerState(1);
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
            if (schedule != null) {
                schedule.stop();
            }
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
                chip = num;
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
        time = chipTime;
    }

    public int getCow(List<Integer> cards) {
        List<Integer> cardDate = new ArrayList<>();
        // 判断是不是五小牛
        int cow;
        int[] cardsList = cards.stream().mapToInt(Integer::valueOf).toArray();
        for (int v : cards) {
            if (pukeUtil.getValue(v) > 10) {
                cardDate.add(10);
            } else {
                cardDate.add(pukeUtil.getValue(v));
            }
        }
        cow = NiuUtil.getNiu(cardDate).cow;
        if (cow == 10) {
            cow = NiuUtil.getNiuType(cardsList);
        }
        return cow;
    }

    private boolean countStack(List<Integer> cows, int winPos, long win, int startSit) {
        long sysWin = 0;
        int size = cows.size();
        int winSet = (startSit + winPos) % size;
        for (int i = 0; i < size; ++i) {
            int mSit = (startSit + i) % size;
            if (mSit != winSet) {
                if (playerList[winSet] instanceof Player) {
                    if (playerList[mSit] instanceof Robot) {
                        sysWin -= win;
                    }
                } else if (playerList[mSit] instanceof Player) {
                    sysWin += win;
                }
            }
        }
        return room.probeJudge(sysWin);
    }

    private void countResoult(List<List<Integer>> cardList) {
        List<Integer> cows = new ArrayList<>();
        int winSit = 0;
        int maxCow = 0;
        for (int i = 0; i < cardList.size(); ++i) {
            List<Integer> cards = cardList.get(i);
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
        int bankP=bankPre[((TNRoleInterface) banker).getBankNum()];
        long win=ant[chip]*bei*bankP;
        int start = 0;
        while (!countStack(cows, winSit, win, start++));
        gameStae = 3;
        time = chipTime;
        Response response = new Response(8015, 1);
        Map<String, Object> msg = new HashMap<>();
        TNGameHistory history = new TNGameHistory();
        history.bankNum=bankP;
        history.downNum=ant[chip];
        history.bankSit=((TNRoleInterface)banker).getSit();
        Object[] playerHList = new Object[playerList.length];
        history.player=playerHList;

        response.msg = msg;
        msg.put("fieldNum", roomId);
        msg.put("gameCode", gameUUID);
        msg.put("tableNum", id);
        msg.put("stage", gameStae);
        Object[] pokers = new Object[playerList.length];
        int size = playerList.length;
        long taxN=0;
        long stackWin=0;
        for(int i=0;i<size;++i){
            int csit=(start+i)%size;
            TNRoleInterface player = (TNRoleInterface) playerList[csit];
            // player.setCards(cardList.get(i));
            Map<String, Object> playerInfo = new HashMap<>();
            Map<String, Object> playerhis = new HashMap<>();
            if(i==winSit){
                 player.setWin(win);
                 playerInfo.put("coins", win*(1-tax));
                 playerInfo.put("winner", 1);
                 if(playerList[csit] instanceof Player){
                    taxN+=win*tax;
                 }else{
                    stackWin+=win*(1-tax);
                 }
            }else{
                player.setWin(0-win);
                playerInfo.put("coins", 0 - win);
                playerInfo.put("winner", 0);
                if(playerList[csit] instanceof Player){
                    stackWin-=win;
                }
            }
            //历史纪录
            playerhis.put("pokers", cardList.get(i));
            playerhis.put("pattern", cows.get(i));
            playerhis.put("chip", player.getChipNum());
            playerhis.put("bank", bankP);
            playerhis.put("sit", player.getSit());
            playerHList[i]=playerhis;

            //发送的
            playerInfo.put("pokers", cardList.get(i));
            playerInfo.put("pattern", (cows.get(i)>>8)&0xf);
            playerInfo.put("seatNum", playerList[csit].uniqueId);
            pokers[i]=playerInfo;
        }
        
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
        for(int i=1;i<playerList.length;++i){
            if(((TNRoleInterface)playerList[i]).getBankNum()>((TNRoleInterface)banker).getBankNum()){
                banker=playerList[i];
            }
        }        
    }

    public void clearGame() {
        schedule.stop();
    }

    public void getUpdateTable(Role role) {
        Response response = new Response(8019, 1);
        Map<String, Object> msg = new HashMap<>();
        response.msg = msg;
        msg.put("antBets", ant);
        msg.put("callType", bankPre[((TNRoleInterface) banker).getBankNum()]);
        if (banker != null) {
            msg.put("dealerSeatNum", banker.uniqueId);
        }
        msg.put("fieldNum", roomId);
        msg.put("myCoin", role.money);
        msg.put("gameCode", gameUUID);
        Map<String, Object> stageTimerConfig = new HashMap<>();
        stageTimerConfig.put("callDealer", bankTime);
        stageTimerConfig.put("chipIn", chipTime);
        stageTimerConfig.put("dealPoker", chipTime);
        stageTimerConfig.put("ready", chipTime);
        msg.put("stageTimerConfig", stageTimerConfig);
        msg.put("stage ", gameStae);
        msg.put("seatNum", ((TNRoleInterface) role).getSit());
        msg.put("restSeconds", time);
        List<Object> seatsInfo = new ArrayList<>();
        for (Role player : playerList) {
            Map<String, Object> seat = new HashMap<>();
            seat.put("seatNum", ((TNRoleInterface) player).getSit());
            seat.put("portrait", player.portrait);
            seat.put("uniqueId", player.uniqueId);
            seat.put("nickName", player.nickName);
            seat.put("coins", player.money);
            seat.put("callType", bankPre[((TNRoleInterface) player).getBankNum()]);
            seatsInfo.add(seat);
        }
        msg.put("seatsInfo", seatsInfo);
        msg.put("currPlayer", currRole.uniqueId);
        msg.put("dealerSeatNum", banker.uniqueId);
        msg.put("antBetAmount ", ant[chip]);
        msg.put("antBets", ant);
        role.send(response);
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