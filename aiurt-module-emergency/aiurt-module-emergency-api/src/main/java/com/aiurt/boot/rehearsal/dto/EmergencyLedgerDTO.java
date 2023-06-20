package com.aiurt.boot.rehearsal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 信号20230630版本需求变更，应急管理需要增加导出闭环台账所需的DTO
 * @author 华宜威
 * @date 2023-06-20 15:46:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyLedgerDTO {

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "实际演练时间")
    private Date rehearsalTime;
    @ApiModelProperty(value = "演练科目")
    private String subject;
    @ApiModelProperty(value = "问题描述")
    private String description;
    @ApiModelProperty(value = "处理方式")
    private String processMode;
}
