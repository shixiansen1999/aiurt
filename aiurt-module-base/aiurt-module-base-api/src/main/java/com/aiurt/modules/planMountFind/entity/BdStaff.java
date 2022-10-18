package com.aiurt.modules.planMountFind.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 人员表
 * @Author: jeecg-boot
 * @Date:   2021-05-24
 * @Version: V1.0
 */
@Data
@TableName("bd_staff")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bd_staff对象", description="人员表")
public class BdStaff implements Serializable {
    private static final long serialVersionUID = 1L;

	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "ID")
    private Integer id;
	/**姓名*/
	@Excel(name = "姓名", width = 15)
    @ApiModelProperty(value = "姓名")
    private String name;
	/**工号*/
	@Excel(name = "工号", width = 15)
    @ApiModelProperty(value = "工号")
    private String number;
	/**角色ID(对应角色表里的ID）*/
	@Excel(name = "角色ID(对应角色表里的ID）", width = 15)
    @ApiModelProperty(value = "角色ID(对应角色表里的ID）")
    private Integer roleId;
	/**班组ID(对应班组表里的ID）*/
	@Excel(name = "班组ID(对应班组表里的ID）", width = 15)
    @ApiModelProperty(value = "班组ID(对应班组表里的ID）")
    private Integer teamId;
	/**职务*/
	@Excel(name = "职务", width = 15)
    @ApiModelProperty(value = "职务")
    private String job;
	/**电话*/
	@Excel(name = "电话", width = 15)
    @ApiModelProperty(value = "电话")
    private String phone;
	/**帐号*/
	@Excel(name = "帐号", width = 15)
    @ApiModelProperty(value = "帐号")
    private String login;
	/**密码*/
	@Excel(name = "密码", width = 15)
    @ApiModelProperty(value = "密码")
    private String password;
	/**登录设备ID*/
	@Excel(name = "登录设备ID", width = 15)
    @ApiModelProperty(value = "登录设备ID")
    private String loginCid;
	/**登录状态(0未登录，1已登录)*/
	@Excel(name = "登录状态(0未登录，1已登录)", width = 15)
    @ApiModelProperty(value = "登录状态(0未登录，1已登录)")
    private String loginState;
	/**登录时间*/
	@Excel(name = "登录时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "登录时间")
    private Date loginTime;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
	/**部门*/
	@Excel(name = "部门", width = 15)
    @ApiModelProperty(value = "部门")
    private String depart;
	/**0为冻结，1为激活*/
	@Excel(name = "0为冻结，1为激活", width = 15)
    @ApiModelProperty(value = "0为冻结，1为激活")
    private String activate;
	/**推送用token码*/
	@Excel(name = "推送用token码", width = 15)
    @ApiModelProperty(value = "推送用token码")
    private String token;
	/**stationId*/
	@Excel(name = "stationId", width = 15)
    @ApiModelProperty(value = "stationId")
    private Integer stationId;
	/**siteId*/
	@Excel(name = "siteId", width = 15)
    @ApiModelProperty(value = "siteId")
    private Integer siteId;
	/**最近一次上传位置x坐标*/
	@Excel(name = "最近一次上传位置x坐标", width = 15)
    @ApiModelProperty(value = "最近一次上传位置x坐标")
    private Double positionX;
	/**最近一次上传位置y坐标*/
	@Excel(name = "最近一次上传位置y坐标", width = 15)
    @ApiModelProperty(value = "最近一次上传位置y坐标")
    private Double positionY;
	/**最近上传坐标时间*/
	@Excel(name = "最近上传坐标时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "最近上传坐标时间")
    private Date positionUpdateTime;
	/**是否监控人员位置的标识（1是0否），备用*/
	@Excel(name = "是否监控人员位置的标识（1是0否），备用", width = 15)
    @ApiModelProperty(value = "是否监控人员位置的标识（1是0否），备用")
    private String monitorFlag;
	/**位置管理报警状态，1报警0正常*/
	@Excel(name = "位置管理报警状态，1报警0正常", width = 15)
    @ApiModelProperty(value = "位置管理报警状态，1报警0正常")
    private String alarmStatus;
	/**身份证号码*/
	@Excel(name = "身份证号码", width = 15)
    @ApiModelProperty(value = "身份证号码")
    private String identityNumber;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
