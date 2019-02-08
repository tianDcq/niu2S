package com.micro.game;

import frame.Player;
import frame.Room;
import frame.socket.ErrResponse;
import frame.socket.Request;
import frame.socket.Response;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashMap;
import java.util.Map;


@Slf4j
class HCPlayer extends Player implements HCRoleInterface {
    public @Getter ChipStruct[] chipList = new ChipStruct[8];

    @Override
    protected void onInit() {
        for (int i = 0; i < 8; ++i) {
            chipList[i] = new ChipStruct(i);
        }
    }

    @Override
    public void onMsg(Request req) {
        Map<String, Object> map = req.msg;
        String msgType = (String) map.get("msgType");
        switch (msgType) {
        case "2001": {

            Object roomId = map.get("roomId");
            // 此处包kong
            if (roomId == null) {
                log.error("roomId为空=========================");
                break;
            }
            this.enterRoom(roomId.toString());
            break;
        }
        case "2019": {
            Response mm = new Response(2019, 1);
            Map<String, Object> msg = new HashMap<>();
            Map<String, Object> selfData = new HashMap<>();
            selfData.put("coins", money);
            msg.put("selfData", selfData);
            HashMap<String, Room> rooms = hall.getRoomMgr().getRooms();
            Object[] roomData = new Object[rooms.size()];
            int i = 0;
            for (Map.Entry<String, Room> entry : rooms.entrySet()) {
                Room room = entry.getValue();
                Map<String, Object> roomConfig = room.getRoomConfig();
                Map<String, Object> roomC = new HashMap<>();
                roomC.put("roomType", roomConfig.get("roomType"));
                roomC.put("roomId", room.getRoomConfig().get("gameRoomId"));
                roomC.put("roomName", roomConfig.get("roomName"));
                boolean bb = false;
                if (roomConfig.get("shangzhuangSwitch") != null) {
                    bb = (Integer) roomConfig.get("shangzhuangSwitch") == 1;
                }
                roomC.put("hostAble", bb);
                roomC.put("minBet", Integer.valueOf((String) roomConfig.get("bottomRed1")) * 100);
                roomC.put("maxBet", Integer.valueOf((String) roomConfig.get("bottomRed2")) * 100);

                HCTable table = (HCTable) room.getTableMgr().getTables().get(0);
                roomC.put("currentPlayer", room.getRoles().size());
                Map<String, Object> phaseData = new HashMap<>();
                phaseData.put("status", table.getGameStae());
                phaseData.put("restTime", table.getTime());
                roomC.put("phaseData", phaseData);
                roomC.put("history", table.history);
                roomData[i] = roomC;
                ++i;
            }
            msg.put("roomData", roomData);
            mm.msg = msg;
            send(mm);
            break;
        }
        case "2010": {
            if (checkChip()) {
                ErrResponse msg = new ErrResponse("已经下注不能退出");
                send(msg);
                return;
            }
            this.exitRoom();
            break;
        }
        case "2009": {
            if (table != null) {
                ((HCTable) table).playerUpBanker(this);
            }
            break;
        }
        case "2002": {
            if (table != null) {
                ((HCTable) table).playerChip(this, map);
            }
            break;
        }
        case "2011": {
            if (table != null) {
                ((HCTable) table).playerDownBanker(this);
            }
            break;
        }
        case "2018": {
            if (table != null) {
                ((HCTable) table).requstTableScene(this);
            }
            break;
        }

        case "2022": {
            MongoTemplate mogo = GameMain.getInstance().getMongoTemplate();
            Query query = new Query(Criteria.where("playerID").is(uniqueId));
            int count = (int) mogo.count(query, PlayerID_gameID.class);
            PageRequest pageable = PageRequest.of(0, 6);
            query.with(pageable);
            query.with(new Sort(new Order(Direction.DESC, "id")));
            List<PlayerID_gameID> pp = mogo.find(query, PlayerID_gameID.class);
            List<GameID_game> games = new ArrayList<>();
            if (pp.size() > 0) {
                Criteria gameCri = new Criteria();
                for (int i = 0; i < pp.size(); ++i) {
                    gameCri.orOperator(Criteria.where("gameId").is(pp.get(i).gameID));
                    System.out.println("游戏id    "+pp.get(i).gameID);
                }
                Query gameQuery = new Query(gameCri);
                games = mogo.find(gameQuery, GameID_game.class);
            }
            Response recordMsg = new Response(2022, 1);
            Map<String, Object> msg = new HashMap<>();
            msg.put("currentPage", map.get("requestPage"));
            msg.put("totalPage", count);
            Map<String, Object> gameRecord = new HashMap<>();
            for (GameID_game game : games) {
                gameRecord.put("id", game.gameId);
                gameRecord.put("roomName", game.roomName);
                gameRecord.put("endTime", game.endTime);
                gameRecord.put("result", game.wins.get(uniqueId));
            }
            msg.put("gameRecord", gameRecord);
            recordMsg.msg = msg;
            send(recordMsg);
            break;
        }
        case "2023":
            MongoTemplate mogo = GameMain.getInstance().getMongoTemplate();
            Query query = new Query(Criteria.where("gameID").is((String) map.get("requestId")));
            GameID_game game=  mogo.findOne(query, GameID_game.class);
            Response recordMsg = new Response(2023, 1);
            Map<String, Object> msg = new HashMap<>();
            recordMsg.msg=msg;
            Map<String, Object> sum = new HashMap<>();
            sum.put("roomName", game.roomName);
            ChipStruct[] chipL=(ChipStruct[])game.playerbetParts.get(uniqueId);
            long chips=0;
            for(ChipStruct cp:chipL){
                chips+=cp.betAmount;
            }
            sum.put("playerBet", chips);
            sum.put("selfResult",game.wins.get(uniqueId));
            sum.put("totalWin",game.opens.get(uniqueId));
            sum.put("tax",game.tax);
            msg.put("sum", sum);
            msg.put("gameCode", game.gameId);
            msg.put("isHost", game.sysHost);
            msg.put("reward", game.open);
            Map<String, Object>[] rewardZone=new HashMap[9];
            ChipStruct[] ownChip= ( ChipStruct[])(game.playerbetParts.get(uniqueId));
            ChipStruct[] gameChip=game.chipList;
            for(int i=0;i<8;++i){
                Map<String, Object> chipInfo = new HashMap<>();
                chipInfo.put("zone", i);
                chipInfo.put("totalBet", gameChip[i].betAmount);
                chipInfo.put("selfBet", ownChip[i].betAmount);
                rewardZone[i]=chipInfo;
            }
            msg.put("rewardZone", rewardZone);
            send(recordMsg);
            break;
        }
    }

    private boolean checkChip() {
        for (int i = 0; i < 8; ++i) {
            if (chipList[i].betAmount > 0) {
                return true;
            }
        }
        return false;
    };

    public long getChip() {
        long chip = 0;
        for (int i = 0; i < 8; ++i) {
            chip += chipList[i].betAmount;
        }
        return chip;
    };

    @Override
    public void endGame() {
        for (int i = 0; i < 8; ++i) {
            chipList[i].betAmount = 0;
        }
    }

    @Override
    protected void onDisconnect() {
        if (!checkChip()) {
            exitRoom();
        }
    }

    @Override
    protected void onExitTable() {
        save();
    }
}