package com.micro.common.vo;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @ClassName: UserOutputVo  
 * @Description: 用户输出Vo
 * @author Allen  
 * @date 2018年7月12日
 */
@Data
public class UserDetailVO {

	/**
     * 会员ID
     */
    private String playId;
	
	/**
	 * 会员是否被禁用，1：没有被禁用，0：被禁用，
	 */
	private Integer accountBet = -100;
	
	/**
	 * 上级账号
	 */
	private String parentAccount;

	/**
	 * 账号
	 */
	private String account;

	/**
	 * 密码
	 */
	private String password;
	
	private String money;
	
	private String createBy;

	private String updateBy;

	/**
	 * 账户状态：0-正常 1-冻结
	 */
	private Integer state;

	/**
	 * 会员等级id
	 */
	private Integer levelId;
	
	/**
	 * 头像编号   默认：'0'
	 */
	private String portrait;

	/**
	 * 1:正式玩家,2:临时玩家
	 */
	private Integer identity;
	
	/******************************************** 用户详细信息 ********************************************************/
	/**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 性别
     */
    private String gender;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 微信
     */
    private String weixin;

    /**
     * qq
     */
    private String qq;
    
    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 银行省份
     */
    private String bankProvince;

    /**
     * 银行县市
     */
    private String bankCity;

    /**
     * 银行账户
     */
    private String bankAccount;

    /**
     * 备注
     */
    private String remark;

	/**
	 * 昵称
	 */
	  private String nickName;

	/**
	 * 用户登录token
	 */
	private String token;

	/**
	 * 保险箱密码
	 */
	private String safePassword;

	/**
	 * 保险箱状态 0：关 1：开
	 */
	private Integer safeStatus;

	/**
	 * 保险箱钱
	 */
	private BigDecimal safeMoney;

	/**
	 * 厅主id
	 */
	private Long siteId;

	/**
	 * 0,无游戏状态
	 * 1,"奔驰宝马"
	 * 2,"飞禽走兽"
	 * 3,"捕鱼"
	 * 4,"斗地主"
	 * 5,"牛牛"',
	 */
	private Integer gameStatus;
}
