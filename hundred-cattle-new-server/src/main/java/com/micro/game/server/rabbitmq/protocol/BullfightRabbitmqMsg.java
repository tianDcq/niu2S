package com.micro.game.server.rabbitmq.protocol;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @ClassName: BullfightRabbitmqMsgRequest  
 * @Description: 牛牛游戏请求
 * @author Henry  
 * @date 2018年8月15日
 */
@Getter
@Setter
public class BullfightRabbitmqMsg {
	 private String username;
	  private String nickName;
	  private String token;
	  private String roomNum;
	  private Integer msgType;
	  private String coin;
	  
	  private Integer index;
	  
	  /**
	   * 厅主id
	   */
	  private Long sitId;
	  
	  /**
	   * 是否是庄家
	   */
	  private boolean isBanker;
	  
	  /**
	   * 游戏状态 1：游戏中 2：离开游戏
	   */
	  private Integer gameStatus;
	  
	  /**
	   * 在线状态  1：在线  2：离线
	   */
	  private Integer onlineStatus;
	  /**
	   * 最大连庄次数，随着条件改变而改变
	   */
	  private Integer maxHost;

	  /**
	   * 剩余连庄数
	   */
	  private Integer restHost;

	  /**
	   * 是否机器人
	   */
	  private boolean isRobot;
}
