package com.micro.frame;

import com.micro.frame.socket.MsgQueue;
import com.micro.frame.socket.Request;
import com.micro.game.History;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;

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
    protected @Getter CallRegisterMgr callRegisterMgr;
    protected @Getter CallMgr callMgr;
    protected @Getter MultiCallMgr multiCallMgr;

    private @Getter long millisecond;
    private long lastUpdate;
    private @Getter float delta;
    private boolean callReady = false;

    private void prepare() {

        this.hallMgr.init(new Callback() {
            @Override
            public void func() {
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
        multiCallMgr.start();
    }

    private void start() {

        status = Status.START;
        roleMgr = new RoleMgr();
        hallMgr = new HallMgr();
        taskMgr = new TaskMgr();
        msgQueue = new MsgQueue();
        callRegisterMgr = new CallRegisterMgr();
        callMgr = new CallMgr();
        multiCallMgr = new MultiCallMgr();

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
            if (!callReady && multiCallMgr.isDone()) {
                callReady = true;
                if (gameMgr.getRobotPairType().type == Config.RobotPairType.Type.One) {
                    taskMgr.createTimer(Config.ROOMCREATETIME + 5, new Callback() {

                        @Override
                        public void func() {
                            log.info("游戏服务启动");
                            setStatus(GameMain.Status.RUN);
                        }
                    });
                } else {
                    log.info("游戏服务启动");
                    setStatus(GameMain.Status.RUN);
                }
            }
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
        Iterable<Request> it = msgQueue.getAll();

        if (it != null) {
            for (Request req : it) {
                try {
                    if (millisecond - req.millisecond > Config.TIMEOUT) {
                        log.error("消息处理超时!! uniqueId:{} msg:{}", req.uniqueId, req.msg);
                        continue;
                    }

                    Role r = roleMgr.getRole(req.uniqueId);
                    if (r == null) {
                        try {
                            roleMgr.createPlayer(req.uniqueId, req.ctx);
                            msgQueue.receive(req);
                        } catch (Exception err) {
                            log.error("玩家初始化失败！！ uuid:" + req.uniqueId + "  err:" + err.toString());
                            log.error(err.getStackTrace().toString());
                        }
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
                } catch (Exception err) {
                    log.error("消息处理错误！！！ uniqueId:{} msg:{}", req.uniqueId, req.msg);
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
                try {
                    step();
                } catch (Exception err) {
                    log.error("服务器未知错误！！！ err:" + err.getMessage());
                    log.error(err.getStackTrace().toString());
                }
                lastUpdate = millisecond;
            }
        }

        doDestroy();
    }
}