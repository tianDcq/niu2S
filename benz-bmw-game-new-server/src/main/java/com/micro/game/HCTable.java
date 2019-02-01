package com.micro.game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.micro.frame.*;
import com.micro.old.server.vo.common.ErrRespone;
import com.micro.old.server.vo.common.Response;

import lombok.Getter;

final class HCTable extends Table {
    private HashSet<Role> roles;
    private @Getter int time;
    private @Getter int gameStae;
    private @Getter int gameIndex;
    private List<String> bankerList;
    private int openTime;
    private int waitTime;
    private int chipTime;

    public HCTable(float time) {
        super(time);
        Map<String, Object> roomConfig = room.getRoomConfig();
        openTime = (int) roomConfig.get("openTime");
        waitTime = (int) roomConfig.get("waitTime");
        chipTime = (int) roomConfig.get("chipTime");
    }

    public void addRole(Role role) {
        roles.add(role);
        Response ownMsg = new Response(2001,1);
        Response mm = new Response(2007,1);
        Map<String, Object> msg = new HashMap<>();
        msg.put("playerName", role.nickName);
        msg.put("playerCoins", role.money);
        msg.put("portrait", role.portrait);
        msg.put("token", role.token);
        msg.put("uniqueId", role.uniqueId);
        mm.msg = msg;
        pushMsgToOther(mm, ownMsg, role.uniqueId);
    };

    public void removeRole(Role role) {
        roles.remove(role);
        Map<String, Object> msg = new HashMap<>();
        Response ownMsg = new Response(2010,1);
        msg.put("playerName", role.nickName);
        msg.put("uniqueId", role.uniqueId);
        msg.put("token", role.token);
        msg.put("playerCoins", role.money);
        ownMsg.msg=msg;

        Response mm = new Response(2008,1);
        mm.msg=new HashMap<>(msg);
        pushMsgToOther(mm,ownMsg,role.uniqueId);
    };

    public HashSet<Role> getRoles() {
        return roles;
    };

    public void playerUpBanker(Role role){
        Map<String, Object> roomConfig = room.getRoomConfig();
        boolean up=(boolean) roomConfig.get("hostAble");  //获取是否允许上庄
        if(up){
            long coin=(long)roomConfig.get("hostAble");  //获取上庄的钱
            if(role.money<coin){
                ErrRespone res=new ErrRespone(2009,0,"钱不够不能上庄");
                role.sendMsg(res);
            }else{
                bankerList.add(role.uniqueId);
                String size= String.valueOf(bankerList.size());
                ErrRespone ownMsg=new ErrRespone(2009,1,size);
                Response otherMsg=new Response(2004,1);
                Map<String,Object> msg=new HashMap<>();
                msg.put("playerName", role.nickName);
                msg.put("playerCoins", role.money);
                msg.put("portrait ", role.portrait);
                msg.put("position",size);
                msg.put("token",role.token);
                msg.put("uniqueId",role.uniqueId);
                otherMsg.msg=msg;
                pushMsgToOther(otherMsg,ownMsg,role.uniqueId);
            }
        }
    };

    public void start() {

    };
    public void pushMsgToOther(Response otherMsg, Response ownMsg, String oId) {
        for (Role rr : roles) {
            String id = rr.uniqueId;
            if (oId.equals(id)) {
                rr.sendMsg(ownMsg);
            } else {
                rr.sendMsg(otherMsg);
            }
        }
    }
}