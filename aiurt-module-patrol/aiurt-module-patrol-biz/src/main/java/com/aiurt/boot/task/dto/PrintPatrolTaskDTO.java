package com.aiurt.boot.task.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author LKJ
 */
@Data
public class PrintPatrolTaskDTO {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "车站")
    private String stationNames;

    @ApiModelProperty(value = "巡视人")
    private String userName;

    @ApiModelProperty(value = "抽检人")
    private String spotCheckUserName;

    @ApiModelProperty(value = "提交时间")
    private String submitTime;

    @ApiModelProperty(value = "巡检标准表Code")
    private java.lang.String standardCode;

    private List<PrintStationDTO> printStationDTOList;

    @ApiModelProperty(value = "任务提交的用户签名图片")
    private java.lang.String signUrl;

}
