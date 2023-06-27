package com.aiurt.modules.worklog.dto;

import com.aiurt.modules.worklog.entity.WorkLog;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 首页-工作日志 返回对象
 * @author 华宜威
 * @date 2023-06-25 17:21:09
 */
@Data
public class WorkLogIndexDTO implements Serializable {
    /**应提交日志数*/
    @ApiModelProperty(value = "应提交日志数")
    private Integer shouldSubmitNum;
    /**已提交日志数*/
    @ApiModelProperty(value = "已提交日志数")
    private Integer submitNum;
    /**未提交日志数*/
    @ApiModelProperty(value = "未提交日志数")
    private Integer unSubmitNum;
    /**首页日志列表，只展示最近7条*/
    @ApiModelProperty(value = "首页日志列表，只展示最近7条")
    private List<WorkLogIndexShowDTO> workLogIndexShowDTOList;

}
