package com.aiurt.modules.robot.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @Description: patrol_area_info
 * @Author: aiurt
 * @Date:   2022-09-26
 * @Version: V1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("patrol_area_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="patrol_area_info对象", description="patrol_area_info")
public class PatrolAreaInfo extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**区域id*/
	@Excel(name = "区域id", width = 15)
    @ApiModelProperty(value = "区域id")
    private java.lang.String areaId;
	/**区域名称*/
	@Excel(name = "区域名称", width = 15)
    @ApiModelProperty(value = "区域名称")
    @NotBlank(message = "区域名称不能为空")
    @Size(max = 255, message = "区域名称长度不能超过255个字符")
    private java.lang.String areaName;
	/**所属父区域id*/
	@Excel(name = "所属父区域id", width = 15)
    @ApiModelProperty(value = "所属父区域id")
    private java.lang.String pid;
	/**机器人id*/
	@Excel(name = "机器人id", width = 15)
    @ApiModelProperty(value = "机器人id")
    @Dict(dictTable = "robot_info", dicText = "robot_name", dicCode = "robot_id")
    private java.lang.String robotId;
	/**设备编码*/
	@Excel(name = "设备编码", width = 15)
    @ApiModelProperty(value = "设备编码")
    @Dict(dictTable = "device", dicText = "name", dicCode = "code")
    private java.lang.String deviceCode;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private java.lang.String updateBy;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;
}
