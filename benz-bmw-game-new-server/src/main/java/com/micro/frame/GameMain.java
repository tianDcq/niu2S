package com.micro.frame;

import com.micro.frame.http.GameHttpRequest;
import com.micro.frame.socket.MsgQueue;
import com.micro.frame.socket.Request;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class GameMain {

    public GameMain() {
        instance = this;
    }

    public enum Status {
        END, START, RUN, STOP, TERMINATE
    }

    private @Getter Status status = Status.END;
    private static @Getter GameMain instance;

    private @Getter RoleMgr roleMgr;
    private @Getter HallMgr hallMgr;
    private @Getter TaskMgr taskMgr;
    protected @Getter GameMgr gameMgr;
    protected @Getter MsgQueue msgQueue;

    private @Getter long millisecond;
    private long lastUpdate;
    private @Getter float delta;

    private void prepare() {

        // TODO

        status = Status.RUN;

        roleMgr.doPrepare();
        hallMgr.doPrepare();
        taskMgr.doPrepare();
        msgQueue.doPrepare();
        gameMgr.onPrepare();
        onPrepare();
    }

    // 注册站点下的所有游戏,和所有房间
    private void register() {
        this.hallMgr.init();
    }

    private void start() {
        status = Status.START;
        roleMgr = new RoleMgr();
        hallMgr = new HallMgr();
        taskMgr = new TaskMgr();
        msgQueue = new MsgQueue();

        register();
        onStart();
        prepare();
    }

    protected abstract void onStart();

    protected void onPrepare() {
    }

    private void step() {
        dealRequests();
        taskMgr.update();
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

        roleMgr.doStop();
        hallMgr.doStop();
        taskMgr.doStop();
        msgQueue.doStop();
        gameMgr.onStop();
    }

    private void doTerminate() {

        roleMgr.doTerminate();
        hallMgr.doTerminate();
        taskMgr.doTerminate();
        msgQueue.doTerminate();
        gameMgr.onTerminate();

    }

    // 停机步骤3：心跳骤停，死亡横线---------------
    private void doDestroy() {

        roleMgr.doDestroy();
        hallMgr.doDestroy();
        taskMgr.doDestroy();
        msgQueue.doDestroy();
        gameMgr.onDestroy();

    }

    // 处理接受的消息
    private void dealRequests() {
        Iterable<Request> it = msgQueue.getAll();

        if (it != null) {
            for (Request req : it) {
                if (millisecond - req.millisecond > Config.TIMEOUT) {
                    log.error("消息处理超时!! uniqueId:{} msg:{}", req.uniqueId, req.msg);
                    continue;
                }

                Role r = roleMgr.getRole(req.uniqueId);
                if (r == null) {
                    roleMgr.createPlayer(req.uniqueId);
                    msgQueue.receive(req);
                    continue;
                }

                if (r instanceof Player) {
                    Player p = (Player) r;
                    // 玩家未初始化，数据重回队列延迟处理
                    if (!p.getInited()) {
                        msgQueue.receive(req);
                        continue;
                    }

                    p.onMsg(req);
                }
            }
        }
    }

    public void run() {
        start();
        while (status != Status.END) {
            millisecond = System.currentTimeMillis();
            if (millisecond - lastUpdate >= Config.RATE) {
                delta = millisecond - lastUpdate;
                step();
                lastUpdate = millisecond;
            }
        }

        doDestroy();
    }
}