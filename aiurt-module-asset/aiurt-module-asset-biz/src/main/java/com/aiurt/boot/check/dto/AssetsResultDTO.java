package com.aiurt.boot.check.dto;

import com.aiurt.boot.record.entity.FixedAssetsCheckRecord;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 更新盘点结果数据的DTO对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetsResultDTO {
    /**
     * 盘点任务记录ID
     */
    @ApiModelProperty(value = "盘点任务记录ID")
    private String id;
    /**
     * 未盘数量
     */
    @ApiModelProperty(value = "未盘数量")
    private Integer undone;
    /**
     * 已盘数量
     */
    @ApiModelProperty(value = "已盘数量")
    private Integer done;
    /**
     * 统计数量
     */
    @ApiModelProperty(value = "统计数量")
    private Integer total;
    /**
     * 实际开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "实际开始时间")
    private Date actualStartTime;
    /**
     * 实际结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "实际结束时间")
    private Date actualEndTime;
    /**
     * 资产盘点数据
     */
    @ApiModelProperty(value = "资产盘点数据")
    List<FixedAssetsCheckRecord> records;
}
