import java.util.concurrent.LinkedBlockingQueue;

import com.micro.common.vo.GameRequestVO;

public class MsgQueue
{
    private LinkedBlockingQueue<GameRequestVO> receiveQ1 = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<GameRequestVO> receiveQ2 = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<GameRequestVO> sendQ1 = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<GameRequestVO> sendQ2 = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<GameRequestVO> currentReveiveQ = receiveQ1;
    private LinkedBlockingQueue<GameRequestVO> currentSendQ = sendQ1;

    public MsgQueue()
    {

    }

    public void receive(GameRequestVO o)
    {
        currentReveiveQ.add(o);
    }

    public void send(GameRequestVO o)
    {
        currentSendQ.add(o);
    }

    public GameRequestVO get()
    {
        return currentReveiveQ.poll();
    }

    public Iterable<GameRequestVO> getAll()
    {
        Iterable<GameRequestVO> ret = currentReveiveQ;
        currentReveiveQ = currentReveiveQ == receiveQ1 ? receiveQ2 : receiveQ1;
        currentReveiveQ.clear();
        return ret;
    }
}