package com.aiurt.common.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author zwl
 * @Date 2022/10/25
 */
@Data
public class OperationProcessResult {

    /**处理环节*/
    @Excel(name = "处理环节", width = 15)
    @ApiModelProperty(value = "处理环节")
    private String processLink;

    /**处理人*/
    @Excel(name = "处理人", width = 15)
    @ApiModelProperty(value = "处理人")
    private String processPerson;

    /**处理人姓名*/
    @Excel(name = "处理人姓名", width = 15)
    @ApiModelProperty(value = "处理人姓名")
    private String processPersonName;

    /**处理时间*/
    @Excel(name = "处理时间，CURRENT_TIMESTAMP", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "处理时间，CURRENT_TIMESTAMP")
    private Date processTime;

    /**用时*/
    @Excel(name = "用时", width = 15)
    @ApiModelProperty(value = "用时")
    private String duration;
}
