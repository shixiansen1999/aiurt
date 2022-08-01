package com.aiurt.boot.task.entity;

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
 * @Description: patrol_accompany
 * @Author: aiurt
 * @Date:   2022-06-28
 * @Version: V1.0
 */
@Data
@TableName("patrol_accompany")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="patrol_accompany对象", description="patrol_accompany")
public class PatrolAccompany implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**巡检单号*/
	@Excel(name = "巡检单号", width = 15)
    @ApiModelProperty(value = "巡检单号")
    private java.lang.String taskDeviceCode;
	/**同行人ID*/
	@Excel(name = "同行人ID", width = 15)
    @ApiModelProperty(value = "同行人ID")
    private java.lang.String userId;
	/**同行人名称*/
	@Excel(name = "同行人名称", width = 15)
    @ApiModelProperty(value = "同行人名称")
    private java.lang.String username;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
}
