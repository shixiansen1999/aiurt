package com.aiurt.modules.robot.vo;


import com.aiurt.modules.robot.entity.TaskFinishInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author JB
 * @Discription: 机器人巡检任务数据对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TaskFinishInfoVO extends TaskFinishInfo {
    /**
     * 线路编号
     */
    @Excel(name = "线路编号", width = 15)
    @ApiModelProperty(value = "线路编号")
    private String lineCode;
    /**
     * 线路名称
     */
    @Excel(name = "线路名称", width = 15)
    @ApiModelProperty(value = "线路名称")
    private String lineName;
    /**
     * 站点编号
     */
    @Excel(name = "站点编号", width = 15)
    @ApiModelProperty(value = "站点编号")
    private String stationCode;
    /**
     * 站点名称
     */
    @Excel(name = "站点名称", width = 15)
    @ApiModelProperty(value = "站点名称")
    private String stationName;
    /**
     * 任务结果
     */
    @Excel(name = "任务结果", width = 15)
    @ApiModelProperty(value = "任务结果")
    private String taskResult;
    /**
     * 处置状态字典名
     */
    @Excel(name = "处置状态字典名", width = 15)
    @ApiModelProperty(value = "处置状态字典名")
    private String isHandleDictName;
    /**
     * 任务状态字典名
     */
    @Excel(name = "任务状态字典名", width = 15)
    @ApiModelProperty(value = "任务状态字典名")
    private String finishStateDictName;
    /**
     * 机器人名称
     */
    @Excel(name = "机器人名称", width = 15)
    @ApiModelProperty(value = "机器人名称")
    private String robotName;
}
