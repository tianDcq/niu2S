package com.micro.common.bean.account.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.micro.common.bean.BaseModel;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class TbAccAccount extends BaseModel{

	private static final long serialVersionUID = 7304551884174246433L;

	public TbAccAccount(){
	}

	/**
	 * @Author: Len
	 * @Date: 2018/8/29 14:13 
	 */
	public TbAccAccount(Long siteId,String account){
		this.siteId = siteId;
		this.account = account;
	}

	/**
	 * 厅主id
	 */
	private Long siteId;

	/**
	 * 上级账号id
	 */
	private Long parentAccountId;

	/**
	 * 账号
	 */
	private String account;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 账户余额
	 */
	private BigDecimal money;

	private String createBy;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createDate;

	private String updateBy;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;

	/**
	 * 账户状态：1-正常 2-冻结
	 */
	private Integer state;

	/**
	 * 会员等级id
	 */
	private String levelId;

	/**
	 * 会员标签id
	 */
	private String labelId;


	/**
	 * 玩家的手机mac地址
	 */
	private String mac;

	/**
	 * 1,正式玩家,2临时玩家,默认1
	 */
	private Integer identity;

	/**
	 * 0：总控账号，1：厅主账号，2：会员账号，11厅主子账号
	 */
	private Integer accountType;

	/**
	 * 头像编号   默认：'0'
	 */
	private String portrait;

	/**
	 * 推荐人ID
	 */
	private String playId;

	/**
	 * 所需经验
	 */
	private Integer experience;

}