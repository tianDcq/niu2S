package com.micro.game.server.frame;

import lombok.Getter;
import lombok.Setter;

public final class GameMain{
    public enum Status{
        END,
        START,
        RUN,
        STOP,
        TERMINATE,
    }

    public @Getter Status status = Status.END;
    public static GameMain instance;
    public static void on(){
        instance = new GameMain();
        instance.run();
    }

    public final static long RATE = 1000*1000/60;
    private long lastUpdate;
    
    private @Getter RoleMgr roleMgr;
    private @Getter HallMgr hallMgr;
    private @Getter GameTimerMgr timerMgr;

    private void start(){
        status = Status.START;
        roleMgr = new RoleMgr();
        hallMgr = new HallMgr();

    }

    private void step(float delta){
        hallMgr.update(delta);
    }

    // 停止服务器步骤1：不再创建新房间
    public void stop()
    {
        if(status == Status.STOP || status == Status.TERMINATE || status == Status.END) { return; }

        status = Status.STOP;
        doStop();
    }

    // 停止服务器步骤2：发送终止消息
    public void terminate()
    {
        if(status == Status.TERMINATE || status == Status.END) { return; }
        status = Status.TERMINATE;
        doTerminate();
    }

    private void doStop(){

    }

    private void doTerminate(){

    }

    private void doDestory(){

    }

    private void run(){
        start();
        while(status != Status.END){
            long current = System.currentTimeMillis();
            if(current - lastUpdate >= RATE)
            {
                step(current - lastUpdate);
                lastUpdate = current;
            }
        }

        doDestory();
    }
}