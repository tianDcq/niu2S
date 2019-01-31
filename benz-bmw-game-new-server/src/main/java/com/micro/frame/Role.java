package com.micro.frame;

import com.micro.old.server.vo.common.Response;

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

	public Role(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public void sendMsg(Response res) {

	}
}