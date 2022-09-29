package com.aiurt.modules.robot.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @Description: robot_info
 * @Author: aiurt
 * @Date: 2022-09-23
 * @Version: V1.0
 */
@Data
@TableName("robot_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "robot_info对象", description = "robot_info")
public class RobotInfo extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 机器人id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "机器人id")
    private java.lang.String robotId;
    /**
     * 机器人ip
     */
    @ApiModelProperty(value = "机器人ip", required = true, example = "192.168.1.189")
    @NotEmpty(message = "机器人ip不能为空")
    @Size(max = 32, message = "机器人ip最大长度为32位")
    private java.lang.String robotIp;
    /**
     * 机器人名称
     */
    @Excel(name = "机器人名称", width = 15)
    @ApiModelProperty(value = "机器人名称", required = true, example = "GZRobot01")
    @NotBlank(message = "机器人名称不能为空")
    @Size(max = 255, message = "机器人名称长度不能超过255个字符")
    private java.lang.String robotName;
    /**
     * 机器人类型
     */
    @Excel(name = "机器人类型", width = 15)
    @ApiModelProperty(value = "机器人类型")
    @Size(max = 255, message = "机器人类型长度不能超过255个字符")
    private java.lang.String robotType;
    /**
     * 线路编码
     */
    @Excel(name = "线路编码", width = 15)
    @ApiModelProperty(value = "线路编码", required = true, example = "06")
    @Dict(dictTable = "cs_line", dicText = "line_name", dicCode = "line_code")
    @NotBlank(message = "线路不能为空")
    @Size(max = 64, message = "线路编码长度不能超过64个字符")
    private java.lang.String lineCode;
    /**
     * 站点编码
     */
    @Excel(name = "站点编码", width = 15)
    @ApiModelProperty(value = "站点编码", required = true, example = "03102")
    @Dict(dictTable = "cs_station", dicText = "station_name", dicCode = "station_code")
    @NotBlank(message = "站点不能为空")
    @Size(max = 64, message = "站点编码长度不能超过64个字符")
    private java.lang.String stationCode;
    /**
     * 高清视频ip
     */
    @Excel(name = "高清视频ip", width = 15)
    @ApiModelProperty(value = "高清视频ip")
    private java.lang.String cameraIp;
    /**
     * 高清视频端口
     */
    @Excel(name = "高清视频端口", width = 15)
    @ApiModelProperty(value = "高清视频端口")
    private java.lang.Integer cameraPort;
    /**
     * 高清视频用户名
     */
    @Excel(name = "高清视频端口", width = 15)
    @ApiModelProperty(value = "高清视频用户名")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private java.lang.String cameraUser;
    /**
     * 高清视频密码
     */
    @Excel(name = "高清视频密码", width = 15)
    @ApiModelProperty(value = "高清视频密码")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private java.lang.String cameraPassword;
    /**
     * 红外视频IP
     */
    @Excel(name = "红外视频IP", width = 15)
    @ApiModelProperty(value = "红外视频IP")
    private java.lang.String flirIp;
    /**
     * 红外视频端口
     */
    @Excel(name = "红外视频端口", width = 15)
    @ApiModelProperty(value = "红外视频端口")
    private java.lang.Integer flirPort;
    /**
     * 红外视频用户名
     */
    @Excel(name = "红外视频用户名", width = 15)
    @ApiModelProperty(value = "红外视频用户名")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private java.lang.String flirUser;
    /**
     * 红外视频密码
     */
    @Excel(name = "红外视频密码", width = 15)
    @ApiModelProperty(value = "红外视频密码")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private java.lang.String flirPassword;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private java.lang.String updateBy;
    /**
     * 修改时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;
}
