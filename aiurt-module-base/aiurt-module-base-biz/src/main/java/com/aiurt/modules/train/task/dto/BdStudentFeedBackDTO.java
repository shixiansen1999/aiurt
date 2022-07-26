package com.aiurt.modules.train.task.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.aiurt.modules.train.task.entity.BdTrainStudentFeedbackRecord;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @author lkj
 */
@Data
@ApiModel(value = "学员反馈表DTO")
public class BdStudentFeedBackDTO {


    /**学员反馈记录*/
    @Excel(name = "学员反馈记录", width = 15)
    @ApiModelProperty(value = "学员反馈记录")
    List<BdTrainStudentFeedbackRecord> bdTrainStudentFeedbackRecords;
}
