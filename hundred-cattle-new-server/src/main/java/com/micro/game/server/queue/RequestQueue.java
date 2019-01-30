package com.micro.game.server.queue;

import java.util.ArrayDeque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.micro.common.vo.GameRequestVO;

/**
 * 请求数据队列
 * @author henry
 * @ClassName: TasksQueue
 * @Description: 业务消息
 * @date 2018-07-24
 */
public class RequestQueue {
	
	private RequestQueue() {}
	
	private static Lock lock = new ReentrantLock();
	/**
	 * 队列1
	 */
	private static final ArrayDeque<GameRequestVO> tasksQueue1 = new ArrayDeque<>();
	
	/**
	 * 队列2
	 */
	private static final ArrayDeque<GameRequestVO> tasksQueue2 = new ArrayDeque<>();
	
	/**
	 * 当前操作的队列
	 */
	private static ArrayDeque<GameRequestVO> currentQueue = tasksQueue1;
	
	/**
	 * 之前的队列
	 */
	private static ArrayDeque<GameRequestVO> preQueue = tasksQueue2;
	
	 /**
	 * 获取当前队列 
	 * @author Henry  
	 * @date 2019年1月30日
	 */
	 public static ArrayDeque<GameRequestVO> getAndSwapQueue(){
		 ArrayDeque<GameRequestVO> preQueue1 = preQueue;
		 //之前的队列
		 preQueue.clear();
		 preQueue = currentQueue;
		 currentQueue = preQueue1;
		 return preQueue;
	 }
	
	/**
	 * 获取消息
	 * @return
	 */
	public static GameRequestVO poll() {
		try {
			lock.lock();
			return currentQueue.poll();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 插入消息
	 * @param value
	 * @return
	 */
	public static boolean add(GameRequestVO value) {
		try {
			lock.lock();
			return currentQueue.add(value);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 清理
	 */
	public static void clear() {
		try {
			lock.lock();
			currentQueue.clear();
		} finally {
			lock.unlock();
		}
	}
}