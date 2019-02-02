package com.micro.frame.event;

import com.micro.frame.Callback;
import com.micro.frame.Root;

import lombok.Getter;

public class Event{
    private @Getter Root target;
    private @Getter Callback call;
    private @Getter String name;

    public Event(String name,Callback cb,Root tar){
        target=tar;
        call=cb;
        this.name=name;
    }

    public boolean emit(){
        if(target != null && target.getIsDestroy()){
            call.func();
            return true;
        }else{
            return false;
        }
    }
}