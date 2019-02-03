package com.micro.frame;

import java.math.BigDecimal;
import java.util.HashMap;

import com.micro.frame.socket.BaseRespone;
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
		portrait = (String) data.get("image");
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
		hall.enter(this);
		this.hall = hall;
		onEnterHall();
	}

	public Config.Error enterRoom() {
		// TODO 断线重连
		return Config.ERR_SUCCESS;
	}

	public Config.Error enterRoom(String id) {
		if (this.hall != null) {
			Room room = hall.getRoomMgr().getRooms().get(id);
			if (room != null) {
				return enterRoom(room);
			}
		}

		return Config.ERR_FAILURE;
	}

	public Config.Error enterRoom(Room room) {
		Config.Error err = room.enter(this);
		if (err == Config.ERR_SUCCESS) {
			hall.exit(this);
			this.room = room;
			onEnterRoom();
			return Config.ERR_SUCCESS;
		}

		return err;
	}

	public Config.Error exitRoom() {

		if (this.table != null) {
			Config.Error err = this.table.exit(this);
			if (err != Config.ERR_SUCCESS) {
				return err;
			}

			onExitTable();
			this.table = null;
		}
		if (this.room != null) {
			Config.Error err = this.room.exit(this);
			if (err != Config.ERR_SUCCESS) {
				return err;
			}

			onExitRoom();
			this.room = null;
		}

		return Config.ERR_SUCCESS;
	}

	public abstract Config.Error exitHall();

	protected void save() {

	}

	protected void checkMoney() {

	}

	public void addMoney(long win) {
		money += win;
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

	public void send(BaseRespone res) {

	}

	protected void onStop() {
	}

	protected void onTerminate() {

	}

	protected void onDestroy() {

	}
}