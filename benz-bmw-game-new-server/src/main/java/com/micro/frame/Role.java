package com.micro.frame;

import java.math.BigDecimal;
import java.util.HashMap;

import com.micro.frame.socket.Response;

import lombok.Getter;
import lombok.Setter;

public abstract class Role extends Root {
	/**
	 * 厅主id
	 */
	public int siteId;

	/**
	 * 性别
	 */
	public int gender;

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

	void init(HashMap<String, Object> data) {
		siteId = (int) data.get("site_id");
		gender = Integer.parseInt((String) data.get("gender"));
		nickName = (String) data.get("nick_name");
		portrait = (String) data.get("portrait");
		money = ((BigDecimal) data.get("money")).multiply(new BigDecimal("100")).longValue();
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

	protected void save(){

	}
	protected void checkMoney(){

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