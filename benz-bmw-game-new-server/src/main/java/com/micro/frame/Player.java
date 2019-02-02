package com.micro.frame;

import java.util.HashMap;

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

	/**
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
	public boolean accountBet;

	public String playId;

	public abstract void onMsg(Request req);

	void init(HashMap<String, Object> data) {
		account = (String) data.get("account");
		state = (int) data.get("state");
		levelId = Integer.parseInt((String) (data.get("level_id")));
		identity = (int) data.get("identity");
		accountType = (int) data.get("account_type");
		accountBet = (boolean) data.get("account_bet");
		playId = (String) data.get("play_id");

		super.init(data);
	}

	public void sendMsg(Response res) {

	}
}