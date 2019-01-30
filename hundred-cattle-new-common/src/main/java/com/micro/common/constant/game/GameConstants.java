package com.micro.common.constant.game;

/**
 * @ClassName: GameConstants  
 * @Description: 游戏id和名称对应枚举
 * @author Allen  
 * @date 2018年8月11日
 */
public enum GameConstants
{
    // 大厅中游荡
    FREE(0,"我在大厅游荡"),
    //    奔驰宝马  gameId:1
	BCBM(1,"奔驰宝马"),
	// 飞禽走兽  gameId:2
	FQZS(2,"飞禽走兽"),
	// 捕鱼  gameId:3
	FISH(3,"金蝉捕鱼"),
	// 斗地主  gameId:4
	DDZ(4,"斗地主"),
	// 牛牛 gameId:5
	NN(5,"百人牛牛"),
	// 炸金花 gameId:6
	ZJH(6,"红黑大战"),
	ER_REN_PIN_SHI(7, "二人牛牛"),
	BAI_JIA_LE(8, "百家乐"),
	QIANG_ZANG_PIN_SHI(9,"抢庄拼十"),
	ER_REN_MA_JIANG(10,"二人麻将"),
	SAN_ZHANG_PAI(11, "三张牌"),
	YAO_YI_YAO(12, "摇一摇"),
	LI_KUI_PI_YU(13, "李逵劈鱼"),
	DA_NAO_TIAN_GONG(14, "大闹天宫"),
	DE_ZHOU_PU_KE(15, "德州扑克"),
	XUN_LONG_DUO_BAO(16, "寻龙夺宝"),
	SHI_SAN_SHUI(17, "十三水"),
	GRAB_THE_CARD(23, "抢庄牌九");
	
	
	// 成员变量
	private String gameName;
	private int gameId;
	// 构造方法
	private GameConstants(int gameId, String gameName) {
		this.gameId = gameId;
		this.gameName = gameName;
	}
	public int getGameId() {
		return gameId;
	}
	public String getGameName() {
		return gameName;
	}
	
	/**
	 * @Title: getGameName  
	 * @Description: 根据游戏Id获取对应游戏名
	 * @param gameId
	 * @return String  
	 * @author Allen  
	 * @date 2018年8月11日
	 */
	public static String getGameName(int gameId){
		 for(GameConstants c :values()){
			 if(c.getGameId()==gameId){
				 return c.getGameName();
			 }
		 }
		return "";
	}

}
