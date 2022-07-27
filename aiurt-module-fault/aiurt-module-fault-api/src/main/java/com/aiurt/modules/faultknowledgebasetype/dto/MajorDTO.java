package com.aiurt.modules.faultknowledgebasetype.dto;

import com.aiurt.modules.faultknowledgebasetype.entity.FaultKnowledgeBaseType;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

@Data
@ApiModel("专业")
public class MajorDTO {
    /**主键id*/
    @ApiModelProperty(value = "主键id")
    private String id;
    /**专业编码*/
    @Excel(name = "专业编码", width = 15)
    @ApiModelProperty(value = "专业编码")
    private String majorCode;
    /**专业名称*/
    @Excel(name = "专业名称", width = 15)
    @ApiModelProperty(value = "专业名称")
    private String majorName;

    /**与专业关联的子系统*/
    private List<SubSystemDTO> subSystemDTOS;

    /**与专业关联的知识库类别*/
    private List<FaultKnowledgeBaseType> faultKnowledgeBaseTypes;

    @TableField(exist = false)
    private String key;
    @TableField(exist = false)
    private String value;
    @TableField(exist = false)
    private String label;
}
