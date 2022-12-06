package com.aiurt.boot.rehearsal.entity;

import java.io.Serializable;

import com.aiurt.boot.rehearsal.constant.EmergencyDictConstant;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Description: emergency_implementation_record
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_implementation_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="应急实时记录登记信息实体对象", description="应急实时记录登记信息实体对象")
public class EmergencyImplementationRecord extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 添加时校验分组
     */
    public interface Save{}
    /**
     * 更新时校验分组
     */
    public interface Update{}

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    @NotNull(message = "记录主键不能为空！", groups = Update.class)
    private java.lang.String id;
	/**关联月演练计划ID*/
	@Excel(name = "关联月演练计划ID", width = 15)
    @ApiModelProperty(value = "关联月演练计划ID")
    @NotNull(message = "月演练计划ID不能为空！", groups = {Save.class, Update.class})
    private java.lang.String planId;
	/**演练人数*/
	@Excel(name = "演练人数", width = 15)
    @ApiModelProperty(value = "演练人数")
    @NotNull(message = "演练人数不能为空！", groups = {Save.class, Update.class})
    @Min(value = 1,message = "演练人数需大于0！", groups = {Save.class, Update.class})
    private java.lang.Integer number;
	/**实际演练时间*/
	@Excel(name = "实际演练时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "实际演练时间")
    @NotNull(message = "实际演练时间不能为空！", groups = {Save.class, Update.class})
    private java.util.Date rehearsalTime;
	/**演练地点，精确到点位*/
	@Excel(name = "演练地点", width = 15)
    @ApiModelProperty(value = "演练地点，精确到点位")
    @Dict(dictTable = "cs_station_position", dicCode = "position_code", dicText = "position_name")
    @NotNull(message = "演练地点不能为空！", groups = {Save.class, Update.class})
    private java.lang.String stationCode;
	/**观察岗位/点位，精确到点位*/
	@Excel(name = "观察岗位/点位", width = 15)
    @ApiModelProperty(value = "观察岗位/点位，精确到点位")
    @Dict(dictTable = "cs_station_position", dicCode = "position_code", dicText = "position_name")
    @NotNull(message = "观察岗位/点位不能为空！", groups = {Save.class, Update.class})
    private java.lang.String positionCode;
	/**记录人ID*/
	@Excel(name = "记录人ID", width = 15)
    @ApiModelProperty(value = "记录人ID")
    @Dict(dictTable = "sys_user", dicCode = "id", dicText = "realname")
    private java.lang.String recorderId;
	/**记录人部门编号*/
	@Excel(name = "记录人部门编号", width = 15)
    @ApiModelProperty(value = "记录人部门编号")
    @Dict(dictTable = "sys_depart", dicCode = "org_code", dicText = "depart_name")
    private java.lang.String orgCode;
	/**记录时间*/
	@Excel(name = "记录时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "记录时间")
    @NotNull(message = "记录时间不能为空！", groups = {Save.class, Update.class})
    private java.util.Date recordTime;
	/**重点关注环节*/
	@Excel(name = "重点关注环节", width = 15)
    @ApiModelProperty(value = "重点关注环节")
    @NotNull(message = "重点关注环节不能为空！", groups = {Save.class, Update.class})
    private java.lang.String concernStep;
	/**观察岗位/点位合格标准*/
	@Excel(name = "观察岗位/点位合格标准", width = 15)
    @ApiModelProperty(value = "观察岗位/点位合格标准")
    @NotNull(message = "观察岗位/点位合格标准不能为空！", groups = {Save.class, Update.class})
    private java.lang.String eligibilityCriteria;
	/**演练总结*/
	@Excel(name = "演练总结", width = 15)
    @ApiModelProperty(value = "演练总结")
    @NotNull(message = "演练总结不能为空！", groups = {Save.class, Update.class})
    private java.lang.String summarize;
	/**演练预案地址*/
	@Excel(name = "演练预案地址", width = 15)
    @ApiModelProperty(value = "演练预案地址")
    @NotNull(message = "演练预案地址不能为空！", groups = {Save.class, Update.class})
    private java.lang.String schemeUrl;
	/**记录状态(1待提交、2已提交)*/
	@Excel(name = "记录状态(1待提交、2已提交)", width = 15)
    @ApiModelProperty(value = "记录状态(1待提交、2已提交)")
    @Dict(dicCode = EmergencyDictConstant.RECORD_STATUS)
    private java.lang.Integer status;
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
