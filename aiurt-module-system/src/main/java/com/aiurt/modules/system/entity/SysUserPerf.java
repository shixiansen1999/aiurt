package com.aiurt.modules.system.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: sys_user_perf
 * @Author: aiurt
 * @Date:   2023-06-07
 * @Version: V1.0
 */
@Data
@TableName("sys_user_perf")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sys_user_perf对象", description="sys_user_perf")
public class SysUserPerf implements Serializable {
    private static final long serialVersionUID = 1L;

	/**用户资质证书表主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "用户资质证书表主键ID")
    private String id;
	/**用户ID*/
	@Excel(name = "用户ID", width = 15)
    @ApiModelProperty(value = "用户ID")
    private String userId;
	/**年份*/
	@Excel(name = "年份", width = 15)
    @ApiModelProperty(value = "年份")
    private Integer year;
	/**月份(字典common_month)*/
	@Excel(name = "月份(字典common_month)", width = 15)
    @Dict(dicCode = "common_month")
    @ApiModelProperty(value = "月份(字典common_month)")
    private Integer month;
	/**绩效分数*/
	@Excel(name = "绩效分数", width = 15)
    @ApiModelProperty(value = "绩效分数")
    private String score;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private Integer delFlag;
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
