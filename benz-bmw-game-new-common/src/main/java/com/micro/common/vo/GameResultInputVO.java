package com.micro.common.vo;


import java.math.BigDecimal;


import lombok.Data;

@Data
public class GameResultInputVO {
	
	 /**
     * 会员ID
     */
    private String playId;
	
	
	private Long siteId;
	
	private String account;
	
	private String token;
	/**
	 * 房间名称
	 */
	private String roomName;
	
	//结算最后输赢，
	private BigDecimal money;
	
	private Integer accountType;

	/**
	 * 游戏id,1:奔驰宝马,2:飞禽走兽,3:捕鱼,4:斗地主,5:牛牛
	 */
	private int gameId;

	/**
	 * 局号
	 */
	private String gameIndex;
	
	/**
	 * 收费类型 1:房间税收，2:玩家输赢
	 */
	private String type;

	/**
	 * 开奖结果（默认是输赢，奔驰宝马记录开的结果）
	 */
	private String  lotteryResult;

	/**
	 * 桌号
	 */
	private String  tableNumber;

	/**
	 * 行为:下注，上庄，捕鱼，地主，农民
	 */
	private String  behavior;
	
	/**
	 * 交易税收
	 */
	private BigDecimal chargeValue;

	/**
     * 游戏详情的JSON格式数据,表字段没有但是统计报表时又需要的数据就以JSON格式存在这里
     */
    private String dataDetails;


    /**
	 * 游戏有效下注金额
	 */
	private BigDecimal betAmount = BigDecimal.ZERO;

	/**
	 * 游戏中奖金额
	 */
	private BigDecimal awardAmount = BigDecimal.ZERO;
	
	
	/**
	 * 自己当局的下注总额
	 */
	private BigDecimal totalBet = BigDecimal.ZERO;
}
