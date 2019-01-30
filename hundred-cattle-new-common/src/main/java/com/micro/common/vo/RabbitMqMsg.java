package com.micro.common.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 消息 
 * @author Henry  
 * @date 2018年12月29日
 */
@Setter
@Getter
@ToString
public class RabbitMqMsg {
	/**
	 * 收消息的人
	 */
	private List<String> uniqueIds;
	/**
	 * 具体的消息
	 */
	private Object msg;
	/**
	 * 消息编号
	 */
	private int msgType;
	/**
	 * 消息类别
	 */
	private int sendType;
	
	/**
	 * 需要排除的人，被排除的人收不到消息
	 */
	private List<String> excludeUniqueIds = new ArrayList<>();
	/**
	 * 玩家uniqueId以及对应的消息
	 */
	private Map<String,Object> uniqueId_msg;
	/**
	 * 老token，用户重新登陆时用到
	 */
	private String oldToken;
	
	
}
