package com.aiurt.boot.rehearsal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author
 * @date 2022/11/30 14:39
 * @description: 应急演练实施记录条件分页查询DTO对象
 */
@Data
public class EmergencyRecordDTO {
    /**
     * 演练科目
     */
    @ApiModelProperty(value = "演练科目")
    private String subject;
    /**
     * 演练开始时间yyyy-MM-dd
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "演练开始时间yyyy-MM-dd")
    private Date startDate;
    /**
     * 演练结束时间yyyy-MM-dd
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "演练结束时间yyyy-MM-dd")
    private Date endDate;
    /**
     * 参与部门
     */
    @ApiModelProperty(value = "参与部门")
    private String joinOrgCode;
    /**
     * 预案类型(1专项应急预案、2综合应急预案、3现场处置方案)
     */
    @ApiModelProperty(value = "预案类型(1专项应急预案、2综合应急预案、3现场处置方案)")
    private Integer type;
    /**
     * 组织部门
     */
    @ApiModelProperty(value = "组织部门")
    private String orgCode;
}
