package com.micro.frame;

import com.micro.frame.socket.MsgQueue;
import com.micro.frame.socket.Request;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class GameMain {

    public GameMain() {
        instance = this;
    }

    public enum Status {
        END, START, RUN, STOP, TERMINATE
    }

    private @Getter @Setter Status status = Status.END;
    private static @Getter GameMain instance;

    private @Getter RoleMgr roleMgr;
    private @Getter HallMgr hallMgr;
    private @Getter TaskMgr taskMgr;
    protected @Getter GameMgr gameMgr;
    protected @Getter MsgQueue msgQueue;
    protected @Getter CallMgr callMgr;

    private @Getter long millisecond;
    private long lastUpdate;
    private @Getter float delta;

    private void prepare() {

        this.hallMgr.init(new Callback() {
            @Override
            public void func() {
                status = Status.RUN;
                roleMgr.doPrepare();
                hallMgr.doPrepare();
                taskMgr.doPrepare();
                msgQueue.doPrepare();
                gameMgr.onPrepare();
                onPrepare();
            }
        });
    }

    private void register() {
        Register.calls();
        callMgr.start();
    }

    private void start() {

        status = Status.START;
        roleMgr = new RoleMgr();
        hallMgr = new HallMgr();
        taskMgr = new TaskMgr();
        msgQueue = new MsgQueue();
        callMgr = new CallMgr();

        register();
        onStart();
        prepare();
    }

    protected abstract void onStart();

    protected void onPrepare() {
    }

    private void step() {
        if (status == Status.START) {
            taskMgr.update();
        } else {
            dealRequests();
            taskMgr.update();
            hallMgr.update();
        }
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
        // System.out.println("=========>");
        Iterable<Request> it = msgQueue.getAll();

        if (it != null) {
            for (Request req : it) {
                if (millisecond - req.millisecond > Config.TIMEOUT) {
                    log.error("消息处理超时!! uniqueId:{} msg:{}", req.uniqueId, req.msg);
                    continue;
                }

                Role r = roleMgr.getRole(req.uniqueId);
                if (r == null) {
                    roleMgr.createPlayer(req.uniqueId, req.ctx);
                    msgQueue.receive(req);
                    continue;
                }

                if (r instanceof Player) {
                    Player p = (Player) r;
                    if (p.getCtx() != req.ctx) {
                        roleMgr.reconnect(p, req.ctx);
                    }
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
                delta = (millisecond - lastUpdate) / 1000.0f;
                step();
                lastUpdate = millisecond;
            }
        }

        doDestroy();
    }
}