package com.aiurt.boot.team.entity;

import com.aiurt.common.aspect.annotation.Dict;
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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description: emergency_crew
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_crew")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="emergency_crew对象", description="emergency_crew")
public class EmergencyCrew implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 新增保存时的校验分组
     */
    public interface Save {}

    /**
     * 修改时的校验分组
     */
    public interface Update {}
	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
	/**应急队伍id*/
	@Excel(name = "应急队伍id", width = 15)
    @ApiModelProperty(value = "应急队伍id")
    private String emergencyTeamId;
	/**所属班次*/
	@Excel(name = "所属班次", width = 15)
    @ApiModelProperty(value = "所属班次")
    private String scheduleItem;
	/**职务*/
	@Excel(name = "职务", width = 15)
    @ApiModelProperty(value = "职务")
    @NotNull(message = "职务不能为空",groups = {EmergencyTeam.Save.class, EmergencyTeam.Update.class})
    @Dict(dicCode = "emergency_post")
    private Integer post;
	/**人员id*/
	@Excel(name = "人员id", width = 15)
    @ApiModelProperty(value = "人员id")
    @NotBlank(message = "人员不能为空",groups = {EmergencyTeam.Save.class, EmergencyTeam.Update.class})
    private String userId;

	@ApiModelProperty(value = "人员姓名")
    @TableField(exist = false)
    private String realname;
	/**联系电话*/
	@Excel(name = "联系电话", width = 15)
    @ApiModelProperty(value = "联系电话")
    @NotBlank(message = "联系电话不能为空",groups = {EmergencyTeam.Save.class, EmergencyTeam.Update.class})
    private String userPhone;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
	/**删除状态(0-正常,1-已删除)*/
	@Excel(name = "删除状态(0-正常,1-已删除)", width = 15)
    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;

    /**岗位*/
    @ApiModelProperty(value = "岗位")
    @Dict(dicCode = "sys_post")
    @TableField(exist = false)
    private String jobName;
}
