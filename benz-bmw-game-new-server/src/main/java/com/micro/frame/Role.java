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

	private boolean inited;

	boolean getInited() {
		return inited;
	}

	protected @Getter Hall hall;
	protected @Getter Room room;
	protected @Getter Table table;

	void init() {
		onInit();
		inited = true;
	}

	protected void onInit() {

	}

	void enterTable(Table table) {
		this.table = table;
		onEnterTable();
	}

	void enterHall(Hall hall) {
		this.hall = hall;
		onEnterHall();
	}

	void exitTable() {
		onExitTable();
		this.table = null;
	}

	void enterRoom(Room room) {
		this.room = room;
		onEnterRoom();
	}

	void exitRoom() {
		onExitRoom();
		this.room = null;
	}

	void onEnterTable() {

	}

	void onExitTable() {

	}

	void onEnterRoom() {

	}

	void onExitRoom() {

	}

	void onEnterHall() {

	}

	void doStop() {
		onStop();
	}

	void doTerminate() {
		onTerminate();
	}

	void doDestroy() {
		onDestroy();
	}

	public void sendMsg(Response res) {

	}

	protected void onStop() {
	}

	protected void onTerminate() {

	}

	protected void onDestroy() {

	}
}