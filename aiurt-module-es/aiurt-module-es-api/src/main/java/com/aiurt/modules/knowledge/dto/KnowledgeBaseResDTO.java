package com.aiurt.modules.knowledge.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author
 * @description 故障知识库高级搜索响应DTO对象
 */
@ApiModel(value = "故障知识库高级搜索响应DTO对象", description = "故障知识库高级搜索响应DTO对象")
@Data
public class KnowledgeBaseResDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private String id;

    /**
     * 故障现象编码
     */
    @ApiModelProperty(value = "故障现象编码")
    private String faultPhenomenonCode;

    /**
     * 故障现象
     */
    @ApiModelProperty(value = "故障现象")
    private String title;

    /**
     * 专业编号
     */
    @ApiModelProperty(value = "专业编号")
    private String majorCode;

    /**
     * 专业名称
     */
    @ApiModelProperty(value = "专业名称")
    private String majorName;

    /**
     * 组件部位编号
     */
    @ApiModelProperty(value = "组件部位编号")
    private String materialCode;

    /**
     * 组件部位名称
     */
    @ApiModelProperty(value = "组件部位名称")
    private String materialName;

    /**
     * 设备类型编号
     */
    @ApiModelProperty(value = "设备类型编号")
    private String deviceTypeCode;

    /**
     * 设备类型名称
     */
    @ApiModelProperty(value = "设备类型名称")
    private String deviceTypeName;

    /**
     * 故障原因
     */
    @ApiModelProperty(value = "故障原因")
    private String faultReason;

    /**
     * 故障等级编号
     */
    @ApiModelProperty(value = "故障等级编号")
    private String faultLevelCode;

    /**
     * 故障等级名称
     */
    @ApiModelProperty(value = "故障等级名称")
    private String faultLevelName;

    /**
     * 浏览数
     */
    @ApiModelProperty(value = "浏览数")
    private String scanNum;

    /**
     * 采用数
     */
    @ApiModelProperty(value = "采用数")
    private String use;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
