package com.aiurt.common.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author WangHongTao
 * @Date 2021/11/19
 *
 * 日志统计
 */
@Data
public class LogCountResult {

    @ApiModelProperty(value = "提交人id")
    private String submitId;

    @ApiModelProperty(value = "提交人姓名")
    private String submitName;

    @ApiModelProperty(value = "部门")
    private String department;

    @ApiModelProperty(value = "提交次数")
    private Integer submitNum;

    @ApiModelProperty(value = "未提交次数")
    private Integer unSubmitNum;

}
