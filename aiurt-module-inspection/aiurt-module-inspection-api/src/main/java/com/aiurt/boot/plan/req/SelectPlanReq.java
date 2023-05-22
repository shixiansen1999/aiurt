package com.aiurt.boot.plan.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/7/1211:32
 */
@Data
public class SelectPlanReq {

    @ApiModelProperty(value = "开始时间", required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "开始时间不能为空")
    private java.util.Date startTime;
    @ApiModelProperty(value = "结束时间", required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "结束时间不能为空")
    private Date endTime;
    @ApiModelProperty(value = "状态")
    private Integer status;
    @ApiModelProperty(value = "多个状态,用,隔开")
    private String statuList;
    @ApiModelProperty(value = "作业类型")
    private Integer workType;
    @ApiModelProperty(value = "线路code")
    private String lineCode;
    @ApiModelProperty(value = "站点code")
    private String stationCode;
    @ApiModelProperty(value = "位置code")
    private String positionCode;
    @ApiModelProperty(value = "pageNo")
    private Integer pageNo = 1;
    @ApiModelProperty(value = "pageSize")
    private Integer pageSize = 30;

    @ApiModelProperty(value = "组织结构code集合")
    private List<String> orgCodeList;
    @ApiModelProperty(value = "状态集合")
    private List<String> statusList;
    @ApiModelProperty(value = "站点code集合")
    private List<String> stationCodeList;
    @ApiModelProperty(value = "是否是手工下发")
    private Integer isManual;
    @ApiModelProperty(value = "手工下发列表查询标志")
    private Boolean isManualSign;
    @ApiModelProperty(value = "检修计划单号")
    private java.lang.String code;
    @ApiModelProperty(value = "检修周期类型")
    private java.lang.Integer type;

}
