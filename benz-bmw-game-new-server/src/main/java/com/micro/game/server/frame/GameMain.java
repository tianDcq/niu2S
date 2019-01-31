package com.micro.game.server.frame;

import com.micro.game.server.queue.MsgQueue;
import com.micro.game.server.vo.common.Request;

import lombok.Getter;

public abstract class GameMain {

    public GameMain() {
        instance = this;
    }

    public enum Status {
        END, START, RUN, STOP, TERMINATE,
    }

    private @Getter Status status = Status.END;
    private static @Getter GameMain instance;

    private final static long RATE = 1000 * 1000 / 60;

    private @Getter RoleMgr roleMgr;
    private @Getter HallMgr hallMgr;
    private @Getter GameTimerMgr timerMgr;
    protected @Getter GameMgrInterface gameMgr;
    protected @Getter MsgQueue msgQueue;

    private @Getter long millisecond;
    private long lastUpdate;
    private @Getter float delta;

    private void start() {
        status = Status.START;
        roleMgr = new RoleMgr();
        hallMgr = new HallMgr();
        timerMgr = new GameTimerMgr();
        msgQueue = new MsgQueue();

        onStart();
    }

    protected abstract void onStart();

    private void step() {
        dealRequests();
        timerMgr.update();
        hallMgr.update();
    }

    // 停机步骤1：挂维护，不再创建新房间
    public void stop() {
        if (status == Status.STOP || status == Status.TERMINATE || status == Status.END) {
            return;
        }

        status = Status.STOP;
        doStop();
    }

    // 停机步骤2：发送终止消息，做最后挣扎
    public void terminate() {
        if (status == Status.TERMINATE || status == Status.END) {
            return;
        }
        status = Status.TERMINATE;
        doTerminate();
    }

    private void doStop() {

    }

    private void doTerminate() {

    }

    // 停机步骤3：心跳骤停，死亡横线---------------
    private void doDestory() {

    }

    // 处理接受的消息
    private void dealRequests() {
        Iterable<Request> it = msgQueue.getAll();

        for (Request req : it) {
            
        }
    }

    public void run() {
        start();
        while (status != Status.END) {
            millisecond = System.currentTimeMillis();
            if (millisecond - lastUpdate >= RATE) {
                delta = millisecond - lastUpdate;
                step();
                lastUpdate = millisecond;
            }
        }

        doDestory();
    }
}