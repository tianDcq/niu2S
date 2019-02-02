package com.micro.frame;

import com.micro.frame.socket.Request;
import com.micro.frame.socket.Response;

import lombok.Getter;
import lombok.Setter;

public abstract class Player extends Role {

	/**
	 * 账号
	 */
	public String account;

	/**
	 * 账户状态：0-正常 1-冻结
	 */
	public Integer state;

	/**
	 * 会员等级id
	 */
	public Integer levelId;

	/**
	 * 1,正式玩家,2临时玩家,默认1
	 */
	public Integer identity;

	/**
	 * 0：总控账号，1：厅主账号，2：会员账号，11厅主子账号
	 */
	public Integer accountType;

	/**
	 * 是否启用会员下注 1：启用、0：禁用
	 */
	public Integer accountBet;

	public String playId;

	public abstract void onMsg(Request req);

	public void sendMsg(Response res) {

	}
}