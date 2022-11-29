package com.aiurt.boot.rehearsal.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: emergency_rehearsal_month
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_rehearsal_month")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="emergency_rehearsal_month对象", description="emergency_rehearsal_month")
public class EmergencyRehearsalMonth implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**年演练计划ID*/
	@Excel(name = "年演练计划ID", width = 15)
    @ApiModelProperty(value = "年演练计划ID")
    private java.lang.String planId;
	/**月演练计划编号*/
	@Excel(name = "月演练计划编号", width = 15)
    @ApiModelProperty(value = "月演练计划编号")
    private java.lang.String code;
	/**演练类型(1单项应急预案、2综合应急预案、3现场处置方案)*/
	@Excel(name = "演练类型(1单项应急预案、2综合应急预案、3现场处置方案)", width = 15)
    @ApiModelProperty(value = "演练类型(1单项应急预案、2综合应急预案、3现场处置方案)")
    private java.lang.Integer type;
	/**演练科目*/
	@Excel(name = "演练科目", width = 15)
    @ApiModelProperty(value = "演练科目")
    private java.lang.String subject;
	/**依托预案ID*/
	@Excel(name = "依托预案ID", width = 15)
    @ApiModelProperty(value = "依托预案ID")
    private java.lang.String schemeId;
	/**演练形式(1实战演练、2桌面推演)*/
	@Excel(name = "演练形式(1实战演练、2桌面推演)", width = 15)
    @ApiModelProperty(value = "演练形式(1实战演练、2桌面推演)")
    private java.lang.Integer modality;
	/**组织部门编码*/
	@Excel(name = "组织部门编码", width = 15)
    @ApiModelProperty(value = "组织部门编码")
    private java.lang.String orgCode;
	/**演练时间*/
	@Excel(name = "演练时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "演练时间")
    private java.util.Date rehearsalTime;
	/**必须体现环节*/
	@Excel(name = "必须体现环节", width = 15)
    @ApiModelProperty(value = "必须体现环节")
    private java.lang.String step;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
}
