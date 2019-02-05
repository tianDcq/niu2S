package com.micro.frame.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.micro.frame.Callback;
import com.micro.frame.Root;


public final class EventMgr{
    private Map<String,List<Event>> eventMap=new HashMap<>();

    private boolean del=false;

    public Event regist(String eventName,Callback cb,Root tar){
        List<Event> list=eventMap.get(eventName);
        Event event=new Event(eventName,cb,tar);
        if(list==null){
            list=new ArrayList<>();
            eventMap.put(eventName, list);
        }
        list.add(event);
        return event;
    }
    public void emit(String eventName){
        List<Event> list=eventMap.get(eventName);
        if(list!=null){
            for(Event event : list){
                if(!event.emit()){
                    removeEvent(event);
                }
            }
        }
    }

    public void emit(String eventName,Object obj){
        List<Event> list=eventMap.get(eventName);
        if(list!=null){
            for(Event event : list){
                event.getCall().setData(obj);
                if(!event.emit(obj)){
                    removeEvent(event);
                }
            }
        }
    }

    public boolean removeEvent(Event event){
        List<Event> list=eventMap.get(event.getName());
        return list.remove(event);
    }
    public void clearEvent(String eventName){
        List<Event> list=eventMap.get(eventName);
        list.clear();
    }
}