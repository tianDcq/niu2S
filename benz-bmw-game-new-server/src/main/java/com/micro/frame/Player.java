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
	public int state;

	/**int
	 * 会员等级id
	 */
	public int levelId;

	/**
	 * 1,正式玩家,2临时玩家,默认1
	 */
	public int identity;




	/**
	 * 0：总控账号，1：厅主账号，2：会员账号，11厅主子账号
	 */
	public int accountType;

	/**
	 * 是否启用会员下注 1：启用、0：禁用
	 */
	public int accountBet;

	public String playId;

	private long sqlMoney;

	public void save(){
		// sql.save((money-sqlMoney)/100);
	}
	public void checkMoney(){
		money=sqlMoney;
	}
	public void addMoney(long win){
		sqlMoney+=win;
		money+=win;
	}

	public abstract void onMsg(Request req);

	public void sendMsg(Response res) {

	}
}