package com.micro.common.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GameRequestVO  {
	
	/**voiceId*/
	private int voiceId;
	/**性别*/
	private int gender;
	
	private Integer msgType;
	private String token;
	
	/**
	 * 玩家唯一标识,token不能作为玩家唯一标识,token只作为登录校验
	 */
	private String uniqueId;
	
	/**
	 * 历史纪录
	 */
	private Integer recordCount;
	
	/**
	 * 游戏编号,用来标识每一局游戏
	 */
	private String gameCode;
	
	/**房间类型 0:4倍场,1:10倍场*/
	private int roomType;
	/**
	 * 房间编号
	 */
	private String roomNum;
	
	/**
	 * 下注金额
	 */
	private String chipInAmount;

	/**
	 * 下注区域 0=天,1=地,2=玄,3=黄
	 */
	//private Integer chipInPool;
	
	/**
	 * 用户信息
	 */
	private String userMsg;
	
	/**
	 * 机器人标识
	 */
	private boolean isRobot;
	
	/**
	 * 分页查询当前页数
	 */
	private long pageNo;
	
	/**
	 * 分页查询每页展示多少条
	 */
	private int pageSize;
	
	/**
	 * 场次4:4倍场，10:10倍场
	 */
	private int oddType;
	
}
