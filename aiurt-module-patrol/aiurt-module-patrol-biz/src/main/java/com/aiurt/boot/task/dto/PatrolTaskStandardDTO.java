package com.aiurt.boot.task.dto;

import com.aiurt.boot.task.entity.PatrolTaskStandard;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/6/27
 * @desc
 */
@Data
public class PatrolTaskStandardDTO extends PatrolTaskStandard {
    @Excel(name = "专业名称", width = 15)
    @ApiModelProperty(value = "专业名称")
    private java.lang.String majorName;
    @Excel(name = "子系统名称", width = 15)
    @ApiModelProperty(value = "子系统名称")
    private java.lang.String sysName;
}

