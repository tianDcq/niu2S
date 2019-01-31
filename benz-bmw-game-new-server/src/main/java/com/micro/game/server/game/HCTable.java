package com.micro.game.server.game;

import java.util.HashSet;
import java.util.Map;

import com.micro.game.server.frame.*;
import com.micro.game.server.vo.common.Response;
import com.sun.tools.classfile.Annotation.element_value;

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
                Map msg=Map<String,String>;
                mm.msgType="2007";
                mm.status="1";
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