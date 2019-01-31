package com.micro.game.server.frame;

import com.micro.common.vo.GameRequestVO;

import lombok.Getter;
import lombok.Setter;

public abstract class Player extends Role{

	/**
	 * 账号
	 */
	protected String account;

	/**
	 * 账户状态：0-正常 1-冻结
	 */
	protected Integer state;

	/**
	 * 会员等级id
	 */
	protected Integer levelId;

	/**
	 * 1,正式玩家,2临时玩家,默认1
	 */
	protected Integer identity;
	
	/**
	 * 0：总控账号，1：厅主账号，2：会员账号，11厅主子账号
	 */
	protected Integer accountType;

	/**
	 * 用户token
	 */
	protected String token;
	
	/**
	 * 是否启用会员下注 1：启用、0：禁用
	 */
	protected Integer accountBet;
	
	protected String playId;

    public abstract void onMsg(GameRequestVO object);

    public void sendMsg(GameRequestVO object)
    {
        
    }
}