package com.aiurt.boot.modules.repairManage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author qian
 * @version 1.0
 * @date 2021/9/22 14:52
 */
@Data
@ApiModel(value = "ReTaskDetailVO对象", description = "检修单详情")
public class ReTaskDetailVO {

    @ApiModelProperty(value = "检修任务ID")
    private Long id;

    @ApiModelProperty(value = "周数")
    private Integer weeks;
    @ApiModelProperty(value = "开始时间")
    private Date startTime;
    @ApiModelProperty(value = "结束时间")
    private Date endTime;
    @ApiModelProperty(value = "检修人，检修人names")
    private String staffNames;
    @ApiModelProperty(value = "提交时间")
    private Date submitTime;
    @ApiModelProperty(value = "检修状态：0.未检修 1.已检修 2.确认 3.不予确认 4.验收 5.不予验收")
    private Integer status;
    @ApiModelProperty(value = "检修班组")
    private String teamName;
    @ApiModelProperty(value = "检修地点")
    private String position;
    @ApiModelProperty(value = "检修池内容")
    private List<RepairPoolListVO> repairPoolList;
    @ApiModelProperty(value = "检修记录")
    private String content;
    @ApiModelProperty(value = "处理结果")
    private String processContent;
    @ApiModelProperty(value = "附件信息")
    private List<String> url;
    @ApiModelProperty(value = "提交人")
    private String sumitUserName;
    @ApiModelProperty(value = "确认人")
    private String confirmUserName;
    @ApiModelProperty(value = "确认时间")
    private Date confirmTime;
    @ApiModelProperty(value = "验收人")
    private String receiptUserName;
    @ApiModelProperty(value = "验收时间")
    private Date receiptTime;
}
