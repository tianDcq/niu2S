package com.micro.common.bean.account.model;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.micro.common.bean.BaseModel;

import lombok.Data;

/**
 * tb_game_each_record
 * @author 
 */
@Data
public class TbGameEachRecord extends BaseModel implements Serializable {
    /**
     * 厅主id
     */
    private Long siteId;

    /**
     * 游戏id,1:奔驰宝马,2:飞禽走兽,3:捕鱼,4:斗地主,5:牛牛
     */
    private Integer gameId;

    /**
     * 局号,具体某一局游戏的唯一编号
     */
    private String gameCode;

    /**
     * 系统输赢金额（斗地主不需要填）,-表示输,+表示赢
     */
    private BigDecimal changeAmount;

    /**
     * 当局下注总金额
     */
    private BigDecimal gameMoney;

    private String createBy;

    private Date createDate;

    /**
     * 是否系统坐庄,1:不是,2:是
     */
    private Integer isSystem;

    /**
     * 每局游戏的服务费
     */
    private BigDecimal chargeValue;

    private static final long serialVersionUID = 1L;

    /**
     * 桌号
     */
    private String  tableNumber;
    
    /**
     * 房间名称
     */
    private String  roomName;
}