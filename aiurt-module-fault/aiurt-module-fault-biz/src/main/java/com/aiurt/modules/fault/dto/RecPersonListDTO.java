package com.aiurt.modules.fault.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author fgw
 */
@Data
public class RecPersonListDTO implements Serializable {

    private static final long serialVersionUID = 3858351883927412481L;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "账号")
    private String userName;

    @ApiModelProperty(value = "用户名")
    private String realName;

    @ApiModelProperty(value = "图片")
    private String picturePath;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "角色编码")
    private String roleCode;

    @ApiModelProperty(value = "排版情况")
    private String scheduleStatus;

    @ApiModelProperty(value = "是否处理相同的故障")
    private String handledSameFault;

    @ApiModelProperty(value = "任务情况")
    private String taskStatus;

    @ApiModelProperty(value = "站点")
    private String stationName;

    @ApiModelProperty(value = "得分")
    private Long evaluationScore;

    @ApiModelProperty(value = "工龄")
    private Float tenure;

    @ApiModelProperty(value = "绩效")
    private Integer performance;

    @ApiModelProperty(value = "资质")
    private String qualification;

    @ApiModelProperty(value = "平均响应时长")
    private Long averageResponseTime;

    @ApiModelProperty(value = "平均解决时长")
    private Long averageResolutionTime;

    @ApiModelProperty(value = "故障次数")
    private Long faultHandCount;

    @ApiModelProperty(value = "故障处理次数 设备类型")
    private Long faultHandDeviceTypeCount;

    @ApiModelProperty(value = "历史维修任务")
    private List<FaultRecDTO> faultRecList;
}

