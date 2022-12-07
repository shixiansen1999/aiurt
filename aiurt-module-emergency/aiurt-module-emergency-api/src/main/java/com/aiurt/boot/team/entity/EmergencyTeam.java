package com.aiurt.boot.team.entity;

import com.aiurt.boot.team.dto.EmergencyTeamTrainingDTO;
import com.aiurt.boot.team.vo.EmergencyCrewVO;
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

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @Description: emergency_team
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_team")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="emergency_team对象", description="emergency_team")
public class EmergencyTeam implements Serializable {
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
	/**所属专业*/
	@Excel(name = "所属专业", width = 15)
    @ApiModelProperty(value = "所属专业")
    @NotBlank(message = "所属专业不能为空",groups = {Save.class, Update.class})
    private String majorCode;
    @ApiModelProperty(value = "所属专业名称")
    @TableField(exist = false)
    private String majorName;
	/**所属部门*/
	@Excel(name = "所属部门", width = 15)
    @ApiModelProperty(value = "所属部门")
    @NotBlank(message = "所属部门不能为空",groups = {Save.class, Update.class})
    private String orgCode;
    @ApiModelProperty(value = "所属部门名称")
    @TableField(exist = false)
    private String orgName;
	/**应急队伍名称*/
	@Excel(name = "应急队伍名称", width = 15)
    @ApiModelProperty(value = "应急队伍名称")
    @NotBlank(message = "应急队伍名称不能为空",groups = {Save.class, Update.class})
    private String emergencyTeamname;
	/**应急队伍编号*/
	@Excel(name = "应急队伍编号", width = 15)
    @ApiModelProperty(value = "应急队伍编号")
    private String emergencyTeamcode;
	/**队伍人数*/
	@Excel(name = "队伍人数", width = 15)
    @ApiModelProperty(value = "队伍人数")
    private Integer peopleNum;
	/**当班人数*/
	@Excel(name = "当班人数", width = 15)
    @ApiModelProperty(value = "当班人数")
    private Integer ondutyNum;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
	/**线路编码*/
	@Excel(name = "线路编码", width = 15)
    @ApiModelProperty(value = "线路编码")
    @NotBlank(message = "线路不能为空",groups = {Save.class, Update.class})
    private String lineCode;
    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;
	/**站点编码*/
	@Excel(name = "站点编码", width = 15)
    @ApiModelProperty(value = "站点编码")
    @NotBlank(message = "站点不能为空",groups = {Save.class, Update.class})
    private String stationCode;
    @ApiModelProperty(value = "站点名称")
    @TableField(exist = false)
    private String stationName;
	/**驻扎地编码*/
	@Excel(name = "驻扎地编码", width = 15)
    @ApiModelProperty(value = "驻扎地编码")
    @NotBlank(message = "驻扎地不能为空",groups = {Save.class, Update.class})
    private String positionCode;
    @ApiModelProperty(value = "驻扎地名称")
    @TableField(exist = false)
    private String positionName;
	/**工区编码*/
	@Excel(name = "工区编码", width = 15)
    @ApiModelProperty(value = "工区编码")
    private String workareaCode;
    @ApiModelProperty(value = "工区名称")
    @TableField(exist = false)
    private String workareaName;
	/**负责人id*/
	@Excel(name = "负责人id", width = 15)
    @ApiModelProperty(value = "负责人id")
    @NotBlank(message = "负责人不能为空",groups = {Save.class, Update.class})
    private String managerId;
    @ApiModelProperty(value = "负责人姓名")
    @TableField(exist = false)
    private String managerName;
	/**联系电话*/
	@Excel(name = "联系电话", width = 15)
    @ApiModelProperty(value = "联系电话")
    @NotBlank(message = "联系电话不能为空",groups = {Save.class, Update.class})
    private String managerPhone;
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


    @ApiModelProperty(value = "应急人员添加集合")
    @Valid
    @TableField(exist = false)
    private List<EmergencyCrew> emergencyCrewList;

    @ApiModelProperty(value = "应急人员查询集合")
    @TableField(exist = false)
    private List<EmergencyCrewVO> emergencyCrewVOList;

    @ApiModelProperty(value = "应急人员数量")
    @TableField(exist = false)
    private String crews;

    @ApiModelProperty(value = "训练计划")
    @TableField(exist = false)
    private List<EmergencyTeamTrainingDTO> emergencyTeamDTOList;
}
