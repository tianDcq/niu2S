package com.micro.game.server.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import lombok.Data;

/**
 * @ClassName: UserInputVo  
 * @Description: 用户InputVo
 * @author Allen  
 * @date 2018年7月14日
 */
@Data
public class UserInputVo implements Serializable {
	/**
	 * @Fields serialVersionUID
	 * @Description: 自动生成一个随机码
	 * @date 2018年1月27日 上午5:45:27
	 * @author dev allen
	 */
	private static final long serialVersionUID = -1L;

	/**
	 * @Fields offset
	 * @Description: 当前页
	 * @date 2018年1月8日 上午3:48:35
	 * @author allen
	 */
	private Integer offset;

	/**
	 * @Fields limit
	 * @Description: 每页显示条数
	 * @date 2018年1月8日 上午3:48:35
	 * @author allen
	 */
	private Integer limit;
	
	/**
	 * 排序列名 
	 */
	private String sort;
	
	/**
	 * 排位命令（desc，asc） 
	 */
	private String sortOrder;
	
	/**
	 * 组合查询条件
	 */
	private Map<String, Object> conditionsMap;
	
	
	/******************************************** 用户基本信息 ********************************************************/
	/**
	 * 厅主id
	 */
	private Long siteId;

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
	 * 玩家手机mac地址
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
	 * 保险箱状态
	 */
	private Integer safeStatus;

	/**
	 * 保险箱钱
	 */
	private BigDecimal safeMoney;

	/**
	 * 保险箱密码
	 */
	private String safePassword;

	/**
	 * 保险箱旧密码
	 */
	private String previousPassword;

	/**
	 * 用户头像
	 */
	private String portrait;

	/**
	 * 用户余额
	 */
	private BigDecimal money;

	/**
	 * 站点标识
	 */
	private String stationMark;

	/**
	 * 用户token
	 */
	private String token;
	
	/**
	 * 是否启用会员下注 1：启用、0：禁用
	 */
	private Integer accountBet;
	
	private String playId;
}
