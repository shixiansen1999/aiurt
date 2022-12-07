package com.aiurt.boot.rehearsal.entity;

import com.aiurt.boot.rehearsal.constant.EmergencyDictConstant;
import com.aiurt.common.aspect.annotation.DeptFilterColumn;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
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
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description: 应急演练年计划实体对象
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_rehearsal_year")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="应急演练年计划实体对象", description="应急演练年计划实体对象")
public class EmergencyRehearsalYear extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 新增保存时的校验分组
     */
    public interface Save {}

    /**
     * 修改时的校验分组
     */
    public interface Update {}

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    @NotNull(message = "主键ID不能为空！", groups = Update.class)
    private java.lang.String id;
	/**计划编号*/
	@Excel(name = "计划编号", width = 15)
    @ApiModelProperty(value = "计划编号")
    @TableField(value = "`code`")
    @NotNull(message = "计划编号不能为空！", groups = Update.class)
    private java.lang.String code;
	/**计划名称*/
	@Excel(name = "计划名称", width = 15)
    @ApiModelProperty(value = "计划名称")
    @NotNull(message = "计划名称不能为空！", groups = {Save.class, Update.class})
    private java.lang.String name;
	/**所属年份格式：yyyy*/
	@Excel(name = "所属年份", width = 15)
    @ApiModelProperty(value = "所属年份格式：yyyy")
    @TableField(value = "`year`")
    @NotNull(message = "所属年份不能为空！", groups = {Save.class, Update.class})
    private java.lang.String year;
	/**编制人ID*/
	@Excel(name = "编制人ID", width = 15)
    @ApiModelProperty(value = "编制人ID")
    @Dict(dictTable = "sys_user", dicCode = "id", dicText = "realname")
    @NotNull(message = "编制人ID不能为空！", groups = {Save.class, Update.class})
    private java.lang.String userId;
	/**编制部门编码*/
	@Excel(name = "编制部门编码", width = 15)
    @ApiModelProperty(value = "编制部门编码")
    @Dict(dictTable = "sys_depart", dicCode = "org_code", dicText = "depart_name")
    @NotNull(message = "编制部门编码不能为空！", groups = {Save.class, Update.class})
    @DeptFilterColumn
    private java.lang.String orgCode;
	/**编制日期*/
	@Excel(name = "编制日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "编制日期，格式yyyy-MM-dd")
    @NotNull(message = "编制日期编码不能为空！", groups = {Save.class, Update.class})
    private java.util.Date compileDate;
	/**审核状态（1待提交、2审核中、3已通过）*/
	@Excel(name = "审核状态（1待提交、2审核中、3已通过）", width = 15)
    @ApiModelProperty(value = "审核状态（1待提交、2审核中、3已通过）")
    @TableField(value = "`status`")
    @Dict(dicCode = EmergencyDictConstant.YEARPLAN_STATUS)
    private java.lang.Integer status;
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
    /**
     * 实例id
     */
    @ApiModelProperty(value = "实例id")
    @TableField(exist = false)
    private String processInstanceId;
    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id")
    @TableField(exist = false)
    private String taskId;
    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    @TableField(exist = false)
    private String taskName;
    /**
     * 模板key，流程标识
     */
    @ApiModelProperty(value = "模板key，流程标识")
    @TableField(exist = false)
    private String modelKey;
}
