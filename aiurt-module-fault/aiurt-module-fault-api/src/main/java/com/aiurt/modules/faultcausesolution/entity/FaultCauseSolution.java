package com.aiurt.modules.faultcausesolution.entity;

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
 * @Description: fault_cause_solution
 * @Author: aiurt
 * @Date: 2022-06-24
 * @Version: V1.0
 */
@Data
@TableName("fault_cause_solution")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "故障原因解决方案对象", description = "故障原因解决方案对象")
public class FaultCauseSolution implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
    /**
     * 故障知识库ID
     */
    @ApiModelProperty(value = "故障知识库ID")
    private String knowledgeBaseId;
    /**
     * 故障原因
     */
    @Excel(name = "故障原因", width = 15)
    @ApiModelProperty(value = "故障原因")
    private String faultCause;
    /**
     * 解决方案
     */
    @Excel(name = "解决方案", width = 15)
    @ApiModelProperty(value = "解决方案")
    private String solution;
    /**
     * 维修视频url
     */
    @Excel(name = "维修视频url", width = 15)
    @ApiModelProperty(value = "维修视频url")
    private String videoUrl;
//    /**
//     * 备件编码
//     */
//    @Excel(name = "备件编码", width = 15)
//    @ApiModelProperty(value = "备件编码")
//    private String sparePartCode;
//    /**
//     * 数量
//     */
//    @Excel(name = "数量", width = 15)
//    @ApiModelProperty(value = "数量")
//    private Integer number;
//    /**
//     * 规格型号
//     */
//    @Excel(name = "规格型号", width = 15)
//    @ApiModelProperty(value = "规格型号")
//    private String specification;
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
