package com.aiurt.modules.robot.entity;

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
 * @Description: patrol_point_info
 * @Author: aiurt
 * @Date:   2022-09-26
 * @Version: V1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("patrol_point_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="patrol_point_info对象", description="patrol_point_info")
public class PatrolPointInfo implements Serializable {
    private static final long serialVersionUID = 1L;

	/**巡检点位id*/
	@Excel(name = "巡检点位id", width = 15)
    @ApiModelProperty(value = "巡检点位id")
    private java.lang.String pointId;
	/**巡检点位名称*/
	@Excel(name = "巡检点位名称", width = 15)
    @ApiModelProperty(value = "巡检点位名称")
    @NotBlank(message = "巡检点位名称不能为空")
    @Size(max = 255, message = "巡检点位名称长度不能超过255个字符")
    private java.lang.String pointName;
	/**巡检点位类型*/
	@Excel(name = "巡检点位类型", width = 15)
    @ApiModelProperty(value = "巡检点位类型")
    private java.lang.String pointType;
	/**机器人id*/
	@Excel(name = "机器人id", width = 15)
    @ApiModelProperty(value = "机器人id")
    private java.lang.String robotId;
	/**所属区域id*/
	@Excel(name = "所属区域id", width = 15)
    @ApiModelProperty(value = "所属区域id")
    private java.lang.String areaId;
	/**所属停靠点id*/
	@Excel(name = "所属停靠点id", width = 15)
    @ApiModelProperty(value = "所属停靠点id")
    private java.lang.String dockId;
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
