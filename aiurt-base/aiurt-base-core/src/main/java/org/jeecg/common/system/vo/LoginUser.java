package org.jeecg.common.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 在线用户信息
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LoginUser {

	/**
	 * 登录人id
	 */
	@ApiModelProperty("id")
	private String id;

	/**
	 * 登录人账号
	 */
	@ApiModelProperty("账号")
	private String username;

	/**
	 * 登录人名字
	 */
	@ApiModelProperty("名字")
	private String realname;

	/**
	 * 登录人密码
	 */
	private String password;

     /**
      * 当前登录部门code
      */
    private String orgCode;
	/**
	 * 头像
	 */
	private String avatar;

	/**
	 * 生日
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthday;

	/**
	 * 性别（1：男 2：女）
	 */
	private Integer sex;

	/**
	 * 电子邮件
	 */
	private String email;

	/**
	 * 电话
	 */
	private String phone;

	/**
	 * 状态(1：正常 2：冻结 ）
	 */
	private Integer status;

	private Integer delFlag;
	/**
     * 同步工作流引擎1同步0不同步
     */
    private Integer activitiSync;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 *  身份（1 普通员工 2 上级）
	 */
	private Integer userIdentity;

	/**
	 * 管理部门ids
	 */
	private String departIds;

	/**
	 * 职务，关联职务表
	 */
	private String post;

	/**
	 * 座机号
	 */
	private String telephone;

	/**多租户id配置，编辑用户的时候设置*/
	private String relTenantIds;

	/**设备id uniapp推送用*/
	private String clientId;

	/**
	 * 部门ID
	 */
	private String orgId;
	/**
	 * 当前登录部门名称
	 */
	private String orgName;

	/**
	 * 角色名称
	 */
	private String roleNames;
	/**
	 * 岗位名称
	 */
	private String postNames;

	/**
	 * 角色编号
	 */
	private String roleCodes;
	/**
	 * 角色id
	 */
	private String roleIds;

	/**
	 * 工号，唯一键
	 */
	private String workNo;

	/**岗位*/
	private String jobName;
	/**签名*/
	private String signatureUrl;
	/**
	 * 参加工作时间
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date workingTime;
	/**
	 * 入职日期
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date entryDate;
}
