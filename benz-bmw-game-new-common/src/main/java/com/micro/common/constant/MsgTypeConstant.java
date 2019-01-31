package com.micro.common.constant;

public class MsgTypeConstant {
	
	/**
	 * 一条消息发给多个人
	 */
	public static final int sendType_one2many = 1;
	/**
	 * 不同的消息发给不同的人
	 */
	public static final int sendType_many2many = 2;

	/**
	 * 获取场次列表
	 */
	public static final int room_list = 2500;
	
	/**
	 * 进入游戏
	 */
	public static final int msg_type_enter_game = 2501;
	
	/**
	 * 玩家掉线
	 */
	public static final int msg_type_BrokenLine = 0;
	
	
	
	/**
	 * 重复登陆消息号
	 */
	public static final int msg_type_reConnect = 8999;
	
	
	/**
	 * 玩家主动退出游戏
	 */
	public static final int msg_type_leaveGame = 2502;
	
	/**
	 * 开始匹配
	 */
	public static final int msg_type_changeTable = 2503;
	
	
	/**
	 * 进入桌子(匹配到玩家)
	 */
	public static final int msg_type_enter_table = 2504;
	
	/**
	 * 抢庄消息
	 */
	public static final int msg_type_banker = 2505;
	
	/**
	 * 抢庄请求
	 */
	public static final int msg_type_bankerRequest = 2506;
	
	/**
	 * 服务器下发下注消息
	 */
	public static final int msg_type_bets = 2507;
	/**
	 * 玩家下注请求消息
	 */
	public static final int msg_type_betsApply = 2508;
	/**
	 * 游戏结果
	 */
	public static final int msg_type_shwoCard = 2509; 
	/**
	 * 获取游戏纪录
	 */
	public static final int msg_type_getGameRecord = 2510;
	/**
	 * 强行离开桌子
	 */
	public static final int msg_type_leaveTable = 2511;
	/**
	 * 系统维护消息号
	 */
	public static final int msg_type_system_maintenance = 2512;

	/**
	 * 刷新,重新获取玩家信息
	 */
	public static final int msg_type_refresh = 2513;
	/**
	 * 更新金币
	 */
	public static final int msg_type_update_money = 1;
	/**
	 * 刷新信息
	 */
	public static final int REFRESH = 6016;
	
}
