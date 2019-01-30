package com.micro.game.server.frame;

import lombok.Getter;
import lombok.Setter;

public class GameMain{
    public static GameMain instance;
    public static void on(){
        instance = new GameMain();
        instance.run();
    }

    public final static long RATE = 1000*1000/60;
    private long lastUpdate;
    private boolean isStop = false;
    private boolean isTerminate = false;
    
    private @Getter @Setter RoleMgr roleMgr;

    private void start(){
        roleMgr = new RoleMgr();
    }

    private void step(){

    }

    private void stop(){

    }

    private void destory(){

    }

    private void run(){
        long current = System.currentTimeMillis();
        start();
        while(current - lastUpdate >= RATE){
            step();
            lastUpdate = current;
            if(isStop){
                stop();
                break;
            }
        }

        while(true){
            if(isTerminate){
                destory();
                break;
            }
        }
    }
}