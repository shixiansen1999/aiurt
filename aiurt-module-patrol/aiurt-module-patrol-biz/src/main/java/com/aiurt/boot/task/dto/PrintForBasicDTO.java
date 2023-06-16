package com.aiurt.boot.task.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.InputStream;
import java.util.List;

/**
 * @className: PrintForBasicDTO
 * @author: hqy
 * @date: 2023/6/16 16:59
 * @version: 1.0
 */
@Data
public class PrintForBasicDTO {
    @ApiModelProperty(value = "标题")
    private  String title;
    @ApiModelProperty(value = "车站")
    private  String patrolStation;
    @ApiModelProperty(value = "巡检人")
    private  String patrolPerson;
    @ApiModelProperty(value = "抽检人")
    private  String checkUserName;
    @ApiModelProperty(value = "巡检日期")
    private  String patrolDate;
    @ApiModelProperty(value = "巡检时间")
    private  String patrolTime;
    @ApiModelProperty(value = "巡检年")
    private  String year;
    @ApiModelProperty(value = "巡检月")
    private  String  month;
    @ApiModelProperty(value = "巡检日")
    private  String day;
    @ApiModelProperty(value = "抽检年")
    private  String yearSpot;
    @ApiModelProperty(value = "抽检月")
    private  String monthSpot;
    @ApiModelProperty(value = "抽检日")
    private  String daySpot;
    @ApiModelProperty(value = "图片字节流")
    private InputStream inputStream;
    @ApiModelProperty(value = "打印标准和检查结果")
    private List<PrintDTO> printDTOList;
}
