package com.micro.game.server.frame;

import lombok.Getter;
import lombok.Setter;

public abstract class Role{
	/**
	 * 厅主id
	 */
	protected Long siteId;

    /**
     * 性别
     */
    protected String gender;

	/**
	 * 昵称
	 */
	protected String nickName;

	/**
	 * 用户头像
	 */
	protected String portrait;

	/**
	 * 用户余额
	 */
    protected int money;

    private @Getter @Setter String uniqueId;
}