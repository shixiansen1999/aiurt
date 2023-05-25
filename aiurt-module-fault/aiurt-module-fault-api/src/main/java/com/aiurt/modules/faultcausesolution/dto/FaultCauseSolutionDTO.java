package com.aiurt.modules.faultcausesolution.dto;

import com.aiurt.modules.faultsparepart.entity.FaultSparePart;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.List;

/**
 * 故障原因解决方案对象
 */
@ApiModel(value = "故障原因解决方案对象", description = "故障原因解决方案对象")
@Data
public class FaultCauseSolutionDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
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
    /**
     * 备件信息
     */
    @ApiModelProperty(value = "备件信息")
    private List<FaultSparePart> spareParts;
}
