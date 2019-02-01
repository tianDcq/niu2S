package com.micro.frame;

import com.micro.frame.socket.Response;

import lombok.Getter;
import lombok.Setter;

public abstract class Role {
	/**
	 * 厅主id
	 */
	public long siteId;

	/**
	 * 性别
	 */
	public String gender;

	/**
	 * 昵称
	 */
	public String nickName;

	/**
	 * 用户头像
	 */
	public String portrait;

	/**
	 * 用户余额
	 */
	public long money;

	/**
	 * 用户token
	 */
	public String token;

	public String uniqueId;

	protected @Getter Hall hall;
	protected @Getter Room room;
	protected @Getter Table table;

	protected void onInit() {

	}

	void enterTable(Table table) {
		this.table = table;
		onEnterTable();
	}

	void leaveTable() {
		onLeaveTable();
		this.table = null;
	}

	void onEnterTable() {

	}

	void onLeaveTable() {

	}

	public void sendMsg(Response res) {

	}
}