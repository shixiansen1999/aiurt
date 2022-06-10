package com.aiurt.boot.common.result;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: 故障分析报告
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Data
public class FaultAnalysisReportResult {

    /**主键id，自动递增*/
    @TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id，自动递增")
    private Long id;

    /**故障编号，示例：G101.2109.001*/
    @Excel(name = "故障编号，示例：G101.2109.001", width = 15)
    @ApiModelProperty(value = "故障编号，示例：G101.2109.001")
    private String faultCode;

    /**故障分析*/
    @Excel(name = "故障分析", width = 15)
    @ApiModelProperty(value = "故障分析")
    private String faultAnalysis;

    /**解决方案*/
    @Excel(name = "解决方案", width = 15)
    @ApiModelProperty(value = "解决方案")
    private String solution;

    /**删除状态：0.未删除 1已删除*/
    @Excel(name = "删除状态：0.未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态：0.未删除 1已删除")
    private Integer delFlag;

    /**故障现象*/
    @Excel(name = "故障现象", width = 15)
    @ApiModelProperty(value = "故障现象")
    private String faultPhenomenon;

    /**故障类型*/
    @Excel(name = "故障类型", width = 15)
    @ApiModelProperty(value = "故障类型")
    private Integer faultType;
    /**故障类型描述*/
    @Excel(name = "故障类型描述", width = 15)
    @ApiModelProperty(value = "故障类型描述")
    private String faultTypeDesc;

    /**创建人*/
    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /**修改人*/
    @Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
    private String updateBy;

    /**创建时间，CURRENT_TIMESTAMP*/
    @Excel(name = "创建时间，CURRENT_TIMESTAMP", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间，CURRENT_TIMESTAMP")
    private Date createTime;

    /**修改时间，根据当前时间戳更新*/
    @Excel(name = "修改时间，根据当前时间戳更新", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间，根据当前时间戳更新")
    private Date updateTime;

    /**附件列表*/
    List<String> urlList;
}
