package com.aiurt.modules.faultcauseusagerecords.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: fault_cause_usage_records
 * @Author: aiurt
 * @Version: V1.0
 */
@Data
@TableName("fault_cause_usage_records")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "故障原因使用记录表", description = "故障原因使用记录表")
public class FaultCauseUsageRecords implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
    /**
     * 故障编号
     */
    @ApiModelProperty(value = "故障编号")
    private String faultCode;
    /**
     * 故障维修单表id
     */
    @Excel(name = "故障维修单表id", width = 15)
    @ApiModelProperty(value = "故障维修单表id")
    private String faultRepairRecordId;
    /**
     * 故障原因及解决方案表id
     */
    @Excel(name = "故障原因及解决方案表id", width = 15)
    @ApiModelProperty(value = "故障原因及解决方案表id")
    private String faultCauseSolutionId;
    /**
     * 所属部门
     */
    @Excel(name = "所属部门", width = 15)
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
    /**
     * 删除状态：0.未删除 1已删除
     */
    @Excel(name = "删除状态：0.未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态：0.未删除 1已删除")
    private Integer delFlag;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private String updateBy;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**
     * 修改时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
}
