package com.micro.game.server.game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.micro.game.server.frame.*;
import com.micro.game.server.vo.common.Response;

final class HCTable extends Table {
    private HashSet<Role> roles;
    public HCTable(float time) {
        super(time);
    }
    public void addRole(Role role)
    {
        roles.add(role);
        String oId=role.getUniqueId();
        for(Role rr : roles){
            Response mm=new Response();
            String id=rr.getUniqueId();
            if(oId.equals(id)){
                mm.msgType="2001";
                mm.status="1";
            }else{
                mm.msgType="2007";
                mm.status="1";
                Map<String,Object> msg=new HashMap<>();
                msg.put("playerName", role.nickName);
                msg.put("playerCoins", role.money);
                msg.put("portrait", role.portrait);
                msg.put("token", role.token);
                msg.put("uniqueId", role.getUniqueId());
                mm.msg=msg;
            }
            rr.sendMsg(mm);
        }
    };
    public void removeRole(Role role)
    {

    };
    public HashSet<Role> getRoles()
    {
        return roles;
    };

    public void start() {

    }
}