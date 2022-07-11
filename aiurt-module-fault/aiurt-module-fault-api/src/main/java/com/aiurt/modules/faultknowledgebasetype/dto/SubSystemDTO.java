package com.aiurt.modules.faultknowledgebasetype.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.faultknowledgebasetype.entity.FaultKnowledgeBaseType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

@Data
@ApiModel("子系统")
public class SubSystemDTO {
    /**id*/
    @ApiModelProperty(value = "id")
    private String id;
    /**名称*/
    @Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
    private String systemName;
    /**编号*/
    @Excel(name = "编号", width = 15)
    @ApiModelProperty(value = "编号")
    private String systemCode;
    /**所属专业-专业表*/
    @Excel(name = "所属专业-专业表", width = 15)
    @ApiModelProperty(value = "所属专业-专业表")
    @Dict(dictTable = "cs_major", dicText = "major_name", dicCode = "major_code")
    private String majorCode;

    /**与子系统关联的知识库类别*/
    private List<FaultKnowledgeBaseType> faultKnowledgeBaseTypes;
}
