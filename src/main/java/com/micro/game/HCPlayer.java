package com.micro.game;

import frame.Callback;
import frame.Config;
import frame.Player;
import frame.Room;
import frame.socket.ErrResponse;
import frame.socket.Request;
import frame.socket.Response;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                if (this.enterRoom() == Config.ERR_SUCCESS) {
                    break;
                }
            } else {
                if (this.enterRoom(roomId.toString()) == Config.ERR_SUCCESS) {
                    break;
                }
            }
            ErrResponse msg = new ErrResponse(Config.ERR_TABLE_DESTORY);
            saveReconnectState(false);
            send(msg);
            exitHall();
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
            if (((HCTable) table).checkBanker(this)) {
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
                System.out.println("玩家下注  ");
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
            int curr = (int) map.get("requestPage");
            getPlayerHistory(curr, 6, BenChiGameHistory.class, new Callback() {

                @Override
                public void func() {
                    Map<String, Object> mm = (Map<String, Object>) this.getData();
                    sendPlayerRecord(curr, (int) mm.get("count"), (List<BenChiGameHistory>) mm.get("games"));
                }
            });
            break;
        }
        case "2023":
            getGameHistory((String) map.get("requestId"), BenChiGameHistory.class, new Callback() {

                @Override
                public void func() {
                    sendGameRecord((BenChiGameHistory) this.getData());
                }
            });
            break;
        }
    }
    /**
     * 发送游戏纪录详情
     * @param game  数据库里面的游戏信息
     */
    private void sendGameRecord(BenChiGameHistory game) {
        if (game != null) {
            Response recordMsg = new Response(2023, 1);
            Map<String, Object> msg = new HashMap<>();
            recordMsg.msg = msg;
            Map<String, Object> sum = new HashMap<>();
            sum.put("roomName", game.roomName);
            List<Object> chipL = (List) (game.playerbetParts.get(uniqueId));
            long chips = 0;
            for (Object cp : chipL) {
                chips += ((ChipStruct) cp).betAmount;
            }
            sum.put("playerBet", chips);
            sum.put("selfResult", game.wins.get(uniqueId));
            sum.put("totalWin", game.opens.get(uniqueId));
            sum.put("tax", game.tax);
            msg.put("sum", sum);
            msg.put("gameCode", game.gameId);
            msg.put("isHost", game.sysHost == uniqueId);
            msg.put("reward", game.open);
            Map<String, Object>[] rewardZone = new HashMap[8];

            List<Object> ownChip = (List) (game.playerbetParts.get(uniqueId));
            ChipStruct[] gameChip = game.chipList;
            for (int i = 0; i < 8; ++i) {
                Map<String, Object> chipInfo = new HashMap<>();
                chipInfo.put("zone", i);
                chipInfo.put("totalBet", gameChip[i].betAmount);
                chipInfo.put("selfBet", ((ChipStruct) ownChip.get(i)).betAmount);
                rewardZone[i] = chipInfo;
            }
            msg.put("rewardZone", rewardZone);
            send(recordMsg);
        }
    }

    private void sendPlayerRecord(int curr, int count, List<BenChiGameHistory> games) {
        Response recordMsg = new Response(2022, 1);
        Map<String, Object> msg = new HashMap<>();
        msg.put("currentPage", curr);
        msg.put("totalPage", count);
        List<Map<String, Object>> gameRecord = new ArrayList<>();
        if (games != null) {
            for (BenChiGameHistory game : games) {
                Map<String, Object> gameinfo = new HashMap<>();
                gameinfo.put("id", game.gameId);
                gameinfo.put("roomName", game.roomName);
                gameinfo.put("endTime", game.endTime);
                gameinfo.put("result", game.wins.get(uniqueId));
                gameRecord.add(gameinfo);
            }
        }
        msg.put("gameRecord", gameRecord);
        recordMsg.msg = msg;
        send(recordMsg);
    }
    /**
     * 检查玩家是否有下注
     * @return
     */
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

    }
}