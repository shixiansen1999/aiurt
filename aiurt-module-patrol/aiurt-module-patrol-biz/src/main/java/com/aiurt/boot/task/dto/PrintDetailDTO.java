package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.system.vo.DictModel;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @author LKJ
 */
@Data
public class PrintDetailDTO {


    @ApiModelProperty(value = "巡视内容及标准")
    private String content;

    @ApiModelProperty(value = "巡视结果")
    private String result;

    /**
     * 巡检结果字典下拉列表
     */
    @ApiModelProperty(value = "巡检结果字典下拉列表")
    private List<DictModel> resultList;

    @ApiModelProperty(value = "备注")
    private String remark;
}
