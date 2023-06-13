package com.aiurt.modules.fault.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author fgw
 */
@Data
public class RecPersonListDTO implements Serializable {

    private static final long serialVersionUID = 3858351883927412481L;

    @ApiModelProperty(value = "用户id", position = 2)
    private String userId;
    @ApiModelProperty(value = "账号")
    @JsonIgnore
    private String userName;
    @ApiModelProperty(value = "用户名", position = 3)
    private String realName;
    @ApiModelProperty(value = "图片", position = 4)
    private String picturePath;
    @ApiModelProperty(value = "角色编码")
    @JsonIgnore
    private List<String> roleCode;
    @ApiModelProperty(value = "角色名称", position = 5)
    private List<String> roleName;
    @ApiModelProperty(value = "人员等级编号", position = 5)
    @Dict(dicCode = "job_grade")
    @JsonIgnore
    private Integer jobGrade;
    @ApiModelProperty(value = "人员等级", position = 5)
    private String jobGradeName;
    @ApiModelProperty(value = "班组编码", position = 6)
    @JsonIgnore
    private String orgCode;
    @ApiModelProperty(value = "班组名称", position = 6)
    private String orgName;
    @ApiModelProperty(value = "排班情况", position = 7)
    private String scheduleStatus;
    @ApiModelProperty(value = "是否处理相同的故障", position = 8)
    private String handledSameFault;
    @ApiModelProperty(value = "任务情况", position = 9)
    private String taskStatus;
    @ApiModelProperty(value = "站点", position = 10)
    private String stationName;
    @ApiModelProperty(value = "距离站点数量", position = 10)
    @JsonIgnore
    private Integer stationNum;
    @ApiModelProperty(value = "距离文本", position = 11)
    private String distanceText;
    @ApiModelProperty(value = "评估得分", position = 12)
    private Double evaluationScore;

    @ApiModelProperty(value = "雷达图：故障处理总次数", position = 13)
    private Double faultNumScore;
    @ApiModelProperty(value = "雷达图：解决效率", position = 14)
    private Double solutionEfficiencyScore;
    @ApiModelProperty(value = "雷达图：工龄", position = 15)
    private Double tenureScore;
    @ApiModelProperty(value = "雷达图：资质分数", position = 16)
    private Double qualificationScore;
    @ApiModelProperty(value = "雷达图：绩效", position = 17)
    private Double performanceScore;

    @ApiModelProperty(value = "列表：历史维修任务", position = 18)
    private List<FaultRecDTO> faultRecList;
    @ApiModelProperty(value = "列表：平均响应时长(分钟)", position = 19)
    private Double averageResponseTime;
    @ApiModelProperty(value = "列表：平均解决时长(分钟)", position = 20)
    private Double averageResolutionTime;
    @ApiModelProperty(value = "列表：故障处理次数（匹配的故障现象）", position = 21)
    private Integer faultHandCount;
    @ApiModelProperty(value = "列表：故障处理次数（同设备类型）", position = 22)
    private Integer faultHandDeviceTypeCount;
    @ApiModelProperty(value = "列表：工龄（单位为年）", position = 23)
    private Double tenure;
    @ApiModelProperty(value = "列表：绩效", position = 24)
    private Double performance;
    @ApiModelProperty(value = "列表：资质", position = 25)
    private List<String> qualification;

    @ApiModelProperty(value = "参加工作时间")
    @JsonIgnore
    private Date workingTime;
    @ApiModelProperty(value = "平均响应时间+平均解决时间")
    @JsonIgnore
    private Double sumResponseTimeResolveTime;
    @ApiModelProperty(value = "故障处理总次数")
    @JsonIgnore
    private Integer totalFaultHandlingCount;
}

