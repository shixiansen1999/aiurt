package com.aiurt.boot.task.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * @author zwl
 */
@Data
public class OverhaulStatisticsDTO {

    /**id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "id")
    private String id;

    /**任务id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "任务id")
    private String taskId;

    /**任务编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    /**班组编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "班组编码")
    private String orgCode;

    /**班组编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "班组编码id")
    private String orgCodeId;

    /**班组名称*/
    @TableField(exist = false)
    @Excel(name = "班组名称", width = 15)
    @ApiModelProperty(value = "班组名称")
    private String orgName;

    /**用户id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "用户id")
    private String  userId;

    /**姓名*/
    @TableField(exist = false)
    @Excel(name = "姓名", width = 15)
    @ApiModelProperty(value = "姓名")
    private String userName;

    /**总检修时长*/
    @TableField(exist = false)
    @Excel(name = "总检修时长", width = 15)
    @ApiModelProperty(value = "总检修时长")
    private Long maintenanceDuration;

    /**计划检修总数*/
    @TableField(exist = false)
    @Excel(name = "检修任务总数", width = 15)
    @ApiModelProperty(value = "检修任务总数")
    private Long taskTotal;


    /**已完成数*/
    @TableField(exist = false)
    @Excel(name = "已完成数", width = 15)
    @ApiModelProperty(value = "已完成数")
    private Long completedNumber;

    /**未完成数*/
    @TableField(exist = false)
    @Excel(name = "未完成数", width = 15)
    @ApiModelProperty(value = "未完成数")
    private Long notCompletedNumber;

    /**漏检修数*/
    @TableField(exist = false)
    @Excel(name = "漏检修数", width = 15)
    @ApiModelProperty(value = "漏检修数")
    private Long leakOverhaulNumber;


    /**平均每周漏检修数*/
    @TableField(exist = false)
    @Excel(name = "平均每周漏检修数", width = 15)
    @ApiModelProperty(value = "平均每周漏检修数")
    private Long avgWeekNumber;

    /**平均每周漏检修数*/
    @TableField(exist = false)
    @Excel(name = "平均每月漏检修数", width = 15)
    @ApiModelProperty(value = "平均每月漏检修数")
    private Long avgMonthNumber;

    /**完成率*/
    @TableField(exist = false)
    @Excel(name = "完成率", width = 15)
    @ApiModelProperty(value = "完成率")
    private String completionRate;

    /**异常数量*/
    @TableField(exist = false)
    @Excel(name = "异常数量", width = 15)
    @ApiModelProperty(value = "异常数量")
    private Long abnormalNumber;

    /**线路*/
    @TableField(exist = false)
    @ApiModelProperty(value = "线路编码")
    private String lineCode;


    /**站点*/
    @TableField(exist = false)
    @ApiModelProperty(value = "站点编码")
    private String stationCode;


    /**子系统编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "子系统编码")
    private String subsystemCode;

    /**状态*/
    @TableField(exist = false)
    @ApiModelProperty(value = "状态")
    private Long  status;

    /**开始时间*/
    @TableField(exist = false)
    @ApiModelProperty(value = "开始时间")
    @Excel(name = "开始时间，精确到分钟", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private java.util.Date startDate;


    /**结束时间*/
    @TableField(exist = false)
    @ApiModelProperty(value = "结束时间")
    @Excel(name = "结束时间，精确到分钟", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private java.util.Date endDate;


    private List<String> orgCodeList;

    /**名称集合*/
    @TableField(exist = false)
    @ApiModelProperty(value = "名称集合")
    private List<OverhaulStatisticsDTO> nameList;

    @ApiModelProperty("分页参数")
    private Integer pageNo;


    @ApiModelProperty("分页参数")
    private Integer pageSize;

    /**
     * 重写hashCode方法
     * @return
     */
    @Override
    public int hashCode(){
        return userId.hashCode();
    }

    /**
     * 重写equals方法
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o){
        if (o instanceof OverhaulStatisticsDTO ){
            OverhaulStatisticsDTO overhaulStatisticsDTO = (OverhaulStatisticsDTO) o ;
            return this.getUserId().equals(overhaulStatisticsDTO.getUserId());
        }
        return false;
    }

}
