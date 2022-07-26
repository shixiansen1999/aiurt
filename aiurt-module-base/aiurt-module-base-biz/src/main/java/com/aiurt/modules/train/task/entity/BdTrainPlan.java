package com.aiurt.modules.train.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.aiurt.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description: 年计划
 * @Author: jeecg-boot
 * @Date:   2022-04-20
 * @Version: V1.0
 */
@Data
@TableName("bd_train_plan")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bd_train_plan对象", description="年计划")
public class BdTrainPlan implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**年份*/
	@Excel(name = "年份", width = 15)
    @ApiModelProperty(value = "年份")
    private Integer planYear;
	/**部门*/
	@Excel(name = "部门", width = 15)
    @ApiModelProperty(value = "部门")
    private String deptName;
	/**培训计划名称*/
	@Excel(name = "培训计划名称", width = 15)
    @ApiModelProperty(value = "培训计划名称")
    private String planName;
	/**安全类累计课时*/
	@Excel(name = "安全类累计课时", width = 15)
    @ApiModelProperty(value = "安全类累计课时")
    private Integer safeHours;
	/**制度类累计课时*/
	@Excel(name = "制度类累计课时", width = 15)
    @ApiModelProperty(value = "制度类累计课时")
    private Integer institutionHours;
	/**技能类累计课时*/
	@Excel(name = "技能类累计课时", width = 15)
    @ApiModelProperty(value = "技能类累计课时")
    private Integer skillHours;
	/** 备注*/
	@Excel(name = " 备注", width = 15)
    @ApiModelProperty(value = " 备注")
    private String remarks;
	/**编制人*/
	@Excel(name = "编制人", width = 15)
    @ApiModelProperty(value = "编制人")
    private String prepareUserName;
	/**编制部门*/
	@Excel(name = "编制部门", width = 15)
    @ApiModelProperty(value = "编制部门")
    private String prepareDept;
	/**编制时间*/
	@Excel(name = "编制时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "编制时间")
    private Date prepareTime;
	/**逻辑删除(1:删除，0：未删除）*/
	@Excel(name = "逻辑删除(1:删除，0：未删除）", width = 15)
    @ApiModelProperty(value = "逻辑删除(1:删除，0：未删除）")
    private Integer idel;
	/**状态（1：已发布，0未发布）*/
	@Excel(name = "状态（1：已发布，0未发布）", width = 15)
    @ApiModelProperty(value = "状态（1：已发布，0未发布）")
    @Dict( dicCode = "state")
    private Integer state;
    @Excel(name = "年计划发布时间", width = 15)
    @ApiModelProperty(value = "年计划发布时间（消息发布的时间）")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date pblishTime;

    /**部门id*/
    @Excel(name = "部门id", width = 15)
    @ApiModelProperty(value = "部门id")
    private Integer deptId;

    /**状态文本值（1：已发布，0未发布）*/
    @Excel(name = "状态文本值（1：已发布，0未发布）", width = 15)
    @ApiModelProperty(value = "状态文本值（1：已发布，0未发布）")
    @TableField(exist = false)
    private String stateText;
    /**子计划*/
    @Excel(name = "子计划", width = 15)
    @ApiModelProperty(value = "子计划")
    @TableField(exist = false)
    private List<BdTrainPlanSub> subList;
}
