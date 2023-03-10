package com.aiurt.boot.task.dto;

import com.aiurt.boot.task.entity.PatrolSamplePerson;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @Classname :  PatrolSamplePersonSaveDTO
 * @Description : TODO
 * @Date :2023/3/8 17:42
 * @Created by   : sbx
 */

@Data
public class PatrolSamplePersonSaveDTO {
    /**巡检单号*/
    @Excel(name = "巡检单号", width = 15)
    @ApiModelProperty(value = "巡检单号")
    private java.lang.String patrolNumber;
    /**巡检位置*/
    @Excel(name = "巡检位置", width = 15)
    @ApiModelProperty(value = "巡检位置")
    private java.lang.String position;
    @ApiModelProperty(value = "抽检人信息")
    private List<PatrolSamplePerson> samplePeoplelist;
}
