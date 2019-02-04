package com.micro.frame;

import java.util.HashMap;
import java.util.Map;

import com.micro.frame.http.GameHttpRequest;
import com.micro.frame.socket.BaseRespone;
import com.micro.frame.socket.Request;

import io.netty.channel.ChannelHandlerContext;
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
	 * int 会员等级id
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

	private long sqlMoney;

	private @Getter @Setter ChannelHandlerContext ctx;

	public boolean isOnline(){
		return ctx != null;
	}

	@Override
	public void save() {
		// sql.save((money-sqlMoney)/100);
		// ReqMgr req=GameMain.getInstance().getReqMgr();
		// GameHttpRequest httpRequest = GameHttpRequest.buildRequest();
        // httpRequest.setSuccessCallback(new Callback() {
        //     @Override
        //     public void func() {
        //         // if (roles.get(player.uniqueId) == player) {
        //         //     player.init((HashMap<String, Object>) this.getData());
        //         //     Hall hall = GameMain.getInstance().getHallMgr().get(player.siteId);
        //         //     if (hall != null) {
        //         //         player.enterHall(hall);
        //         //     }
        //         // }
        //     }
        // });
        // httpRequest.setFailCallback(new Callback() {
        //     @Override
        //     public void func() {
        //         System.out.println(2);
        //     }
        // });
        // final Map<String, Object> map = new HashMap<>();
        // map.put("siteId", Long.valueOf(siteId));
        // map.put("account", account);
        // map.put("money",money);
        // httpRequest.sendForm("/acc/addMoney", map);
	}

	@Override
	public void checkMoney() {
		money = sqlMoney;
	}

	@Override
	public void addMoney(long win) {
		sqlMoney += win;
		money += win;
	}

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
		siteId = 1;
	}

	@Override
	public void send(BaseRespone res) {
		GameMain.getInstance().getMsgQueue().send(this, res);
	}

	public Config.Error exitHall() {
		if (this.table != null) {
			Config.Error err = this.table.exit(this);
			if (err != Config.ERR_SUCCESS) {
				return err;
			}
		}
		if (this.room != null) {
			Config.Error err = this.room.exit(this);
			if (err != Config.ERR_SUCCESS) {
				return err;
			}
		}

		if (this.hall != null) {
			this.hall.exit(this);
		}

		GameMain.getInstance().getRoleMgr().removeRole(this);

		return Config.ERR_SUCCESS;
	}

	protected void onDisconnect() {

	}

	protected void onReconnect() {

	}
}