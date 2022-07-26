package com.aiurt.modules.train.task.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Administrator
 * 2022/4/22
 */
@Data
public class TitleReportVo implements Serializable {
    @ApiModelProperty(value = "报表标题")
    private String title;
    @ApiModelProperty(value = "报表数据")
    private List<ReportVO> reportData;
}
