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

		return Config.ERR_ROOM_NOT_EXIST;
	}

	public Config.Error enterRoom(Room room) {
		Config.Error err = room.enter(this);
		if (err == Config.ERR_SUCCESS) {
			this.room = room;
			onEnterRoom();
			if (GameMain.getInstance().getGameMgr().getRobotPairType().type == Config.RobotPairType.Type.One) {
				err = room.pair(this);
				if (err == Config.ERR_SUCCESS) {
					if (hall != null) {
						hall.exit(this);
					}
					return Config.ERR_SUCCESS;
				}

				return err;
			}

			return Config.ERR_SUCCESS;
		}

		return err;
	}

	private Config.Error exitTable() {
		if (this.table != null) {
			Config.Error err = this.table.exit(this);
			if (err != Config.ERR_SUCCESS) {
				return err;
			}

			onExitTable();
			this.table = null;
		}
		return Config.ERR_SUCCESS;
	}

	public Config.Error exitRoom() {
		Config.Error err = exitTable();
		if (err != Config.ERR_SUCCESS) {
			return err;
		}
		if (this.room != null) {
			err = this.room.exit(this);
			if (err != Config.ERR_SUCCESS) {
				return err;
			}

			onExitRoom();
			this.room = null;
		}

		return Config.ERR_SUCCESS;
	}

	public Config.Error pair() {
		Config.Error err = exitTable();
		if (err != Config.ERR_SUCCESS) {
			return err;
		}
		if (this.room != null) {
			return this.room.pair(this);
		}

		return Config.ERR_ROOM_NOT_EXIST;
	}

	public abstract Config.Error exitHall();

	protected void save() {

	}

	protected void checkMoney() {

	}

	public void addMoney(long win) {
		money += win;
	}

	protected void onEnterTable() {

	}

	protected void onExitTable() {

	}

	protected void onEnterRoom() {

	}

	protected void onExitRoom() {

	}

	protected void onEnterHall() {

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