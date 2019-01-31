package com.micro.common.bean.account.model;


import com.micro.common.constant.game.GameConstants;

import lombok.Data;

/**
 * 用户游戏状态，在线状态redis更新
 * 该类名于 9余额6日下午2点被修改，原因是；和其他类名相同，导致mapper 无法正常工作。对本次移植的所有类名进行修改。
 */
@Data
public class UserGameInputVoGamesServer {

	
	private int gameId = GameConstants.NN.getGameId();
	
    /**
     * account
     */
    private String account;

    /**
     * 厅主id
     */
    private Long siteId;

 
    /**
     *  *当前在哪个游戏中  取值范围： com.micro.common.constant.game.GameConstants
     */
    private Integer gameStatus;

    /**
     * *在线状态 1 在线 2 离线
     */
    private Integer onlineStatus;
}
