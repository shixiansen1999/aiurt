package com.aiurt.boot.modules.system.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 用户证件表
 * @Author: swsc
 * @Date:   2022-04-07
 * @Version: V1.0
 */
@Data
@TableName("sys_user_card")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_user_card对象", description="用户证件表")
public class SysUserCard {

	/**id*/
	@TableId(type= IdType.AUTO)
	@ApiModelProperty(value = "主键id")
	public Integer id;

	/**用户id*/
	@Excel(name = "用户id", width = 15)
	@ApiModelProperty(value = "用户id")
	public String userId;

	/**删除状态(0-正常,1-已删除)*/
	@Excel(name = "删除状态(0-正常,1-已删除)", width = 15)
	@ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
	public Integer delFlag;

	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	public Date createTime;

	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	public Date updateTime;

	/**工作证图片*/
	@Excel(name = "工作证图片", width = 15)
    @ApiModelProperty(value = "工作证图片")
	public String url;

	/**工作证名称*/
	@Excel(name = "工作证名称", width = 15)
	@ApiModelProperty(value = "工作证名称")
	public String name;

	/**工作证类型*/
	@Excel(name = "工作证类型", width = 15)
	@ApiModelProperty(value = "工作证类型")
	public String type;

}
