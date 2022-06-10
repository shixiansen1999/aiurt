package com.aiurt.boot.modules.patrol.vo.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Mr. zhao
 * @date 2021/12/14 15:02
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class ImportTaskAddVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "巡检表名称")
    @Excel(name = "巡检表名称", width = 25)
    private String patrolName;

    @ApiModelProperty(value = "系统名称")
    @Excel(name = "系统名称", width = 25)
    private String systemName;

    @ApiModelProperty(value = "站点名称")
    @Excel(name ="站点编号",width = 20)
    private String stationCode;

    @ApiModelProperty(value = "执行时间")
    @Excel(name = "执行时间",width = 20,format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date time;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注",width = 15)
    private String note;
}
