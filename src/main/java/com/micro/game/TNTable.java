package com.micro.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import frame.game.*;
import frame.game.proto.Game.PlayerMoney;
import frame.game.proto.GameControl.*;
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
    private @Getter int gameStae; // 1叫庄 2 下注 3 开拍 4开完
    private final int[] bankPre = { 1, 1, 2, 3 };
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
    private long taxDoor = 20; //最低开始金额  0.02
    private long taxMinMoney = 10; // 最低抽水金额 0.01
    @Override
    protected void onInit() {
        int[] temp = { 0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70, 0x80, 0x90, 0xa0, 0xb0, 0xc0, 0xd0, 0x11, 0x21, 0x31,
                0x41, 0x51, 0x61, 0x71, 0x81, 0x91, 0xa1, 0xb1, 0xc1, 0xd1, 0x12, 0x22, 0x32, 0x42, 0x52, 0x62, 0x72,
                0x82, 0x92, 0xa2, 0xb2, 0xc2, 0xd2, 0x13, 0x23, 0x33, 0x43, 0x53, 0x63, 0x73, 0x83, 0x93, 0xa3, 0xb3,
                0xc3, 0xd3 };
        puke = new pukeUtil(temp);
    }

    @Override
    protected void onUpdateConfig() {
        PkRoomCfg cfg = getPkRoomCfg();
        bankTime = cfg.getCallTime();
        chipTime = cfg.getBetTime();
        tax = cfg.getTaxRatio();
        ant = new int[4];
        if (room.isFreeRoom()) {
            minMoney = 5000000;
        } else {
            minMoney = (int) (cfg.getMinMoney());
        }
    }

    @Override
    public ArrayList<LotteryModel> getAllLotteryModel(ArrayList<Player> playerL) {
        ArrayList<LotteryModel> modelList = new ArrayList<LotteryModel>();

        if (gameLotteryControl != null) {
            List<List<Integer>> cardsList = new ArrayList<>();
            List<ArrayPuke> cardsAll = gameLotteryControl.getN2().getCardsList();
            int start = gameLotteryControl.getN2().getPos();
            for (int i = 0; i < playerList.length; ++i) {
                cardsList.add(cardsAll.get(i).getListList());
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
            LotteryModel lotteryModel = new LotteryModel();
            Map<String, Object> lotteryResult = new HashMap<>();
            lotteryResult.put("cards", cardsList);
            lotteryResult.put("cows", cows);
            lotteryResult.put("start", start);
            lotteryResult.put("win", win);
            lotteryResult.put("winSit", winSit);
            lotteryModel.lotteryResult = lotteryResult;
            lotteryModel.lotteryWeight = 1;
            long[] www = new long[2];
            www[0] = 0;www[1] = 0;
            lotteryModel.systemWin = countStack(cows, winSit, win, start, www);
            lotteryResult.put("www",www);
            for(Player pw:playerL){
                lotteryModel.controlPlayerWins.put(pw, www[((TNPlayer) pw).getSit()]);
            }
            modelList.add(lotteryModel);

        } else {
            puke.shuffle();
            List<List<Integer>> cardsList = new ArrayList<>();
            for (int k = 0; k < playerList.length; ++k) {
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
                long[] www = new long[2];
                www[0] = 0;www[1] = 0;
                lotteryModel.systemWin = countStack(cows, winSit, win, start, www);
                lotteryResult.put("www",www);
                for(Player pw:playerL){
                    lotteryModel.controlPlayerWins.put(pw, www[((TNPlayer) pw).getSit()]);
                }
                modelList.add(lotteryModel);
            }
        }
        return modelList;
    }

    @Override
    public void onStart() {
        if (begin()) {
            // 1. 每一局开始游戏时，先要确定到底有几个机器人执行以上的攻击判断。在0-机器人数量中随机。
            // 3. 主动攻击的时间是不固定的，在公共CD -- 10 秒内进行随机。
            // a. 游戏开始的时候如果桌子里面有机器人则机器人可主动攻击
            log.info("游戏开始,机器人攻击逻辑开始###########");
            this.robotAttactRoles();
            puke.shuffle();
            playerList = new Role[2];
            ResStart.Builder res = ResStart.newBuilder();
            int i = 0;
            for (Role role : roles.values()) {
            	role.money = role.money /10 * 10;
                ((TNRoleInterface) role).setSit(i);
                ((TNRoleInterface) role).setPlayerState(1);
                playerList[i] = role;
                playerInfo.Builder player = playerInfo.newBuilder();
                player.setName(role.nickName);
                player.setHead(role.portrait);
                player.setCoin(role.money);
                player.setUniqueId(role.uniqueId);
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
        if (min / minMoney > 2) {
            y = 2 * x;
        } else {
            y = (int) min / 15;
        }
        int m = RandomUtil.ramdom(x, y);
        //log.info("x {},y {}  bet {}",x,y,m);
        for (int i = 3; i >= 0; --i) {
            ant[i] = m/10*10;
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
            if (num == 3) {
                res.setCurrPos(-1);
                broadcast(new Response(TwoNiuConfig.ResBnaker, res.build().toByteArray()));
                choseBanker();
                ChipPeriod();
            } else if (sit < playerList.length) {
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
            log.info(role.nickName + "强制开牌");
            ((TNRoleInterface) role).setPlayerState(6);
            if (openN == playerList.length) {
                for (Role pp : playerList) {
                    ((TNRoleInterface) pp).setPlayerState(0);
                }
                clearGame();
                end();
            }
        }
    }

    private void disCard() {
        gameStae = 3;
        time = chipTime + 2;
        Map<String, Object> resoult = (Map<String, Object>) (room.getStorageMgr().lottery(this).lotteryResult);
        int bankP = bankPre[((TNRoleInterface) banker).getBankNum()];
        int start = (int) resoult.get("start");
        List<List<Integer>> cardList = (List<List<Integer>>) resoult.get("cards");
        List<Integer> cows = (List<Integer>) resoult.get("cows");
        int winSit = (int) resoult.get("winSit");
        //long win = (long) resoult.get("win");
        long[] www = (long[]) resoult.get("www");
        Map<String, Object> history = new HashMap<>();

        List<PlayerMoney> playerMoneys = new ArrayList<>();
        history.put("bankNum", bankP);
        history.put("downNum", ant[chip]);
        history.put("bankId", banker.userId);
        Map<String, Object> players = new HashMap<>();
        history.put("players", players);
        history.put("sysBank", banker.userId);

        int size = playerList.length;
        long taxN = 0;
        for (int i = 0; i < size; ++i) {
            int csit = (start + i) % size;
            TNRoleInterface player = (TNRoleInterface) playerList[csit];
            player.setCards(cardList.get(i));
            player.setCow(cows.get(i));
            player.setPlayerState(5);
            Map<String, Object> playerhis = new HashMap<>();
            long oTax = 0;
            long win = www[csit];
            //log.info("{} win {},牌型{}",playerList[csit].nickName,win,cardList.get(i));
            if (win >= taxDoor) {
                oTax = (long) (win * tax);
                // 对战游戏0.02开始抽水，不足0.01按0.01抽
                if(oTax < taxMinMoney) {
                	oTax = taxMinMoney;
                }
                // 向上取整
                if(oTax % 10 > 0) {
                	oTax = (oTax / 10 +1 )*10;
                }
                win = win-oTax;
            }
            player.setWin(win);
            playerList[csit].money += win;
            playerhis.put("win", win);
            if (playerList[csit] instanceof Player) {
                PlayerMoney.Builder rMoney = PlayerMoney.newBuilder();
                rMoney.setAward(www[csit] > 0 ? www[csit] : 0);
                rMoney.setUserid(playerList[csit].userId);
                rMoney.setBet(Math.abs(win));
                rMoney.setDeltaMoney(win);
                rMoney.setValidBet(Math.abs(www[csit]));
                rMoney.setTax(oTax);
                taxN += oTax;// win * tax;
                playerMoneys.add(rMoney.build());
            }
//            if (i == winSit) {
//            	
//                long ww = (long) (www[i] * (1 - tax));
//                player.setWin(ww);
//                playerList[csit].money += ww;
//                playerhis.put("win", win);
//                if (playerList[csit] instanceof Player) {
//                    PlayerMoney.Builder rMoney = PlayerMoney.newBuilder();
//                    rMoney.setAward(win > 0 ? win : 0);
//                    rMoney.setUserid(playerList[csit].userId);
//                    rMoney.setBet(win);
//                    rMoney.setDeltaMoney(ww);
//                    rMoney.setValidBet(win);
//                    rMoney.setTax((long) (win * tax));
//                    taxN += win * tax;
//                    playerMoneys.add(rMoney.build());
//                }
//            } else {
//                player.setWin(0 - win);
//                playerList[csit].money -= win;
//                if (playerList[csit] instanceof Player) {
//                    PlayerMoney.Builder rMoney = PlayerMoney.newBuilder();
//                    rMoney.setUserid(playerList[csit].userId);
//                    rMoney.setBet(win);
//                    rMoney.setDeltaMoney(-win);
//                    rMoney.setValidBet(win);
//                    playerMoneys.add(rMoney.build());
//                }
//                playerhis.put("win", 0 - win);
//            }
            // 历史纪录
            playerhis.put("pokers", cardList.get(i));
            playerhis.put("cow", cows.get(i));
            playerhis.put("name", playerList[csit].nickName);
            playerhis.put("bet", player.getChipNum());
            playerhis.put("bank", player.getBankNum());
            playerhis.put("money", playerList[csit].money);
            playerhis.put("sit", player.getSit());
            playerhis.put("cardPre", getbei(cows.get(i)>>8));

            if (playerList[csit] instanceof Player) {
                playerhis.put("account", ((Player) playerList[csit]).account);
            }
            players.put(String.valueOf(playerList[csit].userId), playerhis);
        }
        result(taxN, playerMoneys, JSON.toJSONString(history));
        ResDisCards.Builder res = ResDisCards.newBuilder();
        for (int i = 0; i < playerList.length; ++i) {
            TNRoleInterface pp = (TNRoleInterface) playerList[i];
            res.addCards(ArrayInt.newBuilder().addAllList(pp.getCards()));
            res.addCows(pp.getCow());
            res.addWins(pp.getWin());
        }
        broadcast(new Response(TwoNiuConfig.ResDisCards, res.build().toByteArray()));

    }

    private long countStack(List<Integer> cows, int winPos, long win, int startSit, long[] www) {
        long sysWin = 0;
        int size = cows.size();
        int winSet = (startSit + winPos) % size;
        int loseSet = 0;
        for (int i = 0; i < size; ++i) {
            int mSit = (startSit + i) % size;
            if (mSit != winSet) {
                www[mSit] = -win;
                loseSet = mSit;
            } else {
                www[mSit] = win;
            }
        } 
        // 防止以小博大
        // 计算能不能赢这么多
        if(www[winSet] > playerList[winSet].money) {
        	www[winSet] = playerList[winSet].money;
        }
        // 计算够不够输
        if (www[winSet] > playerList[loseSet].money) {
			www[loseSet] = -1*playerList[loseSet].money;
			www[winSet] = playerList[loseSet].money;
		}
        //log.info("{} win {}, {} win {} ",playerList[winSet].nickName,www[winSet],playerList[loseSet].nickName,www[loseSet]);
        for (int i = 0; i < size; ++i) {
            if (playerList[i] instanceof Player) {
				sysWin -= www[i];
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
        for (Role role : playerList) {
            ((TNRoleInterface) role).endGame();
        }
        playerList = null;
        currRole = null;
        banker = null;
        openN = 0;
        chip = 0;
        bankNum = 0;
        gameStae = 4;
        time = 0;
        schedule.stop();
    }

    public void getUpdateTable(Role role) {
        ResTableInfo.Builder res = ResTableInfo.newBuilder();
        PkRoomCfg cfg = getPkRoomCfg();
        res.setGameState(gameStae);
        ResTableInfo.timeConfig.Builder timec = ResTableInfo.timeConfig.newBuilder();
        timec.setPairTime(cfg.getStartTime());
        timec.setBankerTime(bankTime);
        timec.setChipTime(chipTime);
        timec.setShowTime(chipTime);
        if (gameStae == 0) {
            timec.setTime(3115616);
        } else {
            timec.setTime(time);
        }
        res.setTimeCf(timec);
        res.setGameState(gameStae);
        if (currRole != null) {
            res.setCurrPos(((TNRoleInterface) currRole).getSit());
        }
        res.setBet(chip);
        res.setOwnPos(((TNRoleInterface) role).getSit());
        res.addAllBets(Arrays.stream(ant).boxed().collect(Collectors.toList()));
        if (banker != null) {
            res.setBankNum(bankNum);
            res.setBankerPos(((TNRoleInterface) banker).getSit());
        }
        for (Role mm : roles.values()) {
            TNRoleInterface pl = (TNRoleInterface) mm;
            playerInfo.Builder player = playerInfo.newBuilder();
            player.setBankerNum(pl.getBankNum());
            player.setName(mm.nickName);
            player.setHead(mm.portrait);
            player.setCoin(mm.money);
            player.setPosId(pl.getSit());
            player.setBet(pl.getChipNum());
            player.addAllCards(pl.getCards());
            player.setPlayerState(pl.getPlayerState());
            player.setWin((int) pl.getWin());
            player.setCow(pl.getCow());
            player.setUniqueId(mm.uniqueId);
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

    @Override
    public void onPairFail() {

    }
}