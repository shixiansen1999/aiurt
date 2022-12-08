package com.aiurt.boot.rehearsal.entity;

import com.aiurt.boot.rehearsal.constant.EmergencyDictConstant;
import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalYearAddDTO;
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
 * @Description: 月演练计划实体对象
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_rehearsal_month")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="月演练计划实体对象", description="月演练计划实体对象")
public class EmergencyRehearsalMonth extends DictEntity implements Serializable {
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
    @TableField(value = "`code`")
    private java.lang.String code;
	/**演练类型(1专项应急预案、2综合应急预案、3现场处置方案)*/
	@Excel(name = "演练类型(1专项应急预案、2综合应急预案、3现场处置方案)", width = 15)
    @ApiModelProperty(value = "演练类型(1专项应急预案、2综合应急预案、3现场处置方案)")
    @Dict(dicCode = EmergencyDictConstant.TYPE)
    @NotNull(message = "演练类型不能为空！", groups = {EmergencyRehearsalYear.Save.class, EmergencyRehearsalYear.Update.class})
    private java.lang.Integer type;
	/**计划类型(0年计划外、1年计划内)*/
    @Excel(name = "计划类型(0年计划外、1年计划内)", width = 15)
    @ApiModelProperty(value = "计划类型(0年计划外、1年计划内)")
    @Dict(dicCode = EmergencyDictConstant.WITHIN)
    @NotNull(message = "计划类型不能为空！", groups = {EmergencyRehearsalYear.Save.class, EmergencyRehearsalYear.Update.class})
    private java.lang.Integer yearWithin;
	/**演练科目*/
	@Excel(name = "演练科目", width = 15)
    @ApiModelProperty(value = "演练科目")
    @NotNull(message = "演练科目不能为空！", groups = {EmergencyRehearsalYear.Save.class, EmergencyRehearsalYear.Update.class})
    private java.lang.String subject;
	/**依托预案ID*/
	@Excel(name = "依托预案ID", width = 15)
    @ApiModelProperty(value = "依托预案ID")
    @NotNull(message = "依托预案ID不能为空！", groups = {EmergencyRehearsalYear.Save.class, EmergencyRehearsalYear.Update.class})
    private java.lang.String schemeId;
	/**依托预案名称*/
    @Excel(name = "依托预案名称", width = 15)
    @ApiModelProperty(value = "依托预案名称")
    @TableField(exist = false)
    private java.lang.String schemeName;
	/**演练形式(1实战演练、2桌面推演)*/
	@Excel(name = "演练形式(1实战演练、2桌面推演)", width = 15)
    @Dict(dicCode = EmergencyDictConstant.MODALITY)
    @ApiModelProperty(value = "演练形式(1实战演练、2桌面推演)")
    @NotNull(message = "演练形式不能为空！", groups = {EmergencyRehearsalYear.Save.class, EmergencyRehearsalYear.Update.class})
    private java.lang.Integer modality;
	/**组织部门编码*/
	@Excel(name = "组织部门编码", width = 15)
    @ApiModelProperty(value = "组织部门编码")
    @Dict(dictTable = "sys_depart", dicCode = "org_code", dicText ="depart_name")
    @NotNull(message = "组织部门编码不能为空！", groups = {EmergencyRehearsalYear.Save.class, EmergencyRehearsalYear.Update.class})
    private java.lang.String orgCode;
	/**演练时间，格式yyyy-MM-dd*/
	@Excel(name = "演练时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM")
    @ApiModelProperty(value = "演练时间，格式yyyy-MM-dd")
    @NotNull(message = "演练时间不能为空！", groups = {EmergencyRehearsalYear.Save.class, EmergencyRehearsalYear.Update.class})
    private java.util.Date rehearsalTime;
	/**必须体现环节*/
	@Excel(name = "必须体现环节", width = 15)
    @ApiModelProperty(value = "必须体现环节")
    @NotNull(message = "必须体现环节不能为空！", groups = {EmergencyRehearsalYear.Save.class, EmergencyRehearsalYear.Update.class})
    private java.lang.String step;
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
