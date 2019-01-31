import java.util.concurrent.LinkedBlockingQueue;

import com.micro.game.server.vo.common.Request;
import com.micro.game.server.vo.common.Response;

public class MsgQueue {
    private LinkedBlockingQueue<Request> receiveQ1 = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Request> receiveQ2 = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Response> sendQ1 = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Response> sendQ2 = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Request> currentReveiveQ = receiveQ1;
    private LinkedBlockingQueue<Response> currentSendQ = sendQ1;

    public MsgQueue() {

    }

    public void receive(Request o) {
        currentReveiveQ.add(o);
    }

    public void send(Response o) {
        currentSendQ.add(o);
    }

    public Request get() {
        return currentReveiveQ.poll();
    }

    public Iterable<Request> getAll() {
        Iterable<Request> ret = currentReveiveQ;
        currentReveiveQ = currentReveiveQ == receiveQ1 ? receiveQ2 : receiveQ1;
        currentReveiveQ.clear();
        return ret;
    }
}