package com.micro.common.constant;

public class MQMsgConstant {
	/**
	 * 这个值需要在rabbitmq管理控制台配置，如果不存在会导致生产者 消费者无法通信， 甚至消费者无法启
	 */
	public static final String exchangeName = "hundred-cattle-new-server-exchange";
	
	/**
	 * 这个值需要在rabbitmq管理控制台配置，如果不存在会导致生产者 消费者无法通信， 甚至消费者无法启
	 */
	public static final String queueName = "hundred-cattle-new-server-queue";

	public static final String ExchangeName = null;

	public static String mgs2GameSericeExchangeName = "mgs2GameSericeExchangeName";
	 
}
