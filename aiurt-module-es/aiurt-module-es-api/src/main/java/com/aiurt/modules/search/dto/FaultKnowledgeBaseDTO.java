package com.aiurt.modules.search.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.aspect.annotation.MajorFilterColumn;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 故障知识库
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
@Data
public class FaultKnowledgeBaseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    @Field(name="id")
    private String id;
	/**故障知识分类编码*/
	@Excel(name = "故障知识分类编码", width = 15)
    @ApiModelProperty(value = "故障知识分类编码")
    @Dict(dictTable = "fault_knowledge_base_type", dicText = "name", dicCode = "code")
    @Field(name="knowledge_base_type_code")
    private String knowledge_base_type_code;
	/**故障现象*/
	@Excel(name = "故障现象", width = 15)
    @ApiModelProperty(value = "故障现象")
    @Field(name="fault_phenomenon")
    private String fault_phenomenon;
	/**故障原因*/
	@Excel(name = "故障原因", width = 15)
    @ApiModelProperty(value = "故障原因")
    @Field(name="fault_reason")
    private String fault_reason;
	/**故障措施/解决方案*/
	@Excel(name = "解决方案", width = 15)
    @ApiModelProperty(value = "故障措施/解决方案")
    private String solution;
	/**关联故障*/
	@Excel(name = "关联故障", width = 15)
    @ApiModelProperty(value = "关联故障")
    @Field(name="fault_codes")
    private String fault_codes;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    @Field(name="create_time")
    private Date create_time;

	/**排查方法*/
	@Excel(name = "排查方法", width = 15)
    @ApiModelProperty(value = "排查方法")
    private String method;
	/**专业编码*/
	@Excel(name = "专业编码", width = 15)
    @ApiModelProperty(value = "专业编码")
    @Dict(dictTable = "cs_major", dicText = "major_name", dicCode = "major_code")
    @MajorFilterColumn
    @Field(name="major_code")
    private String major_code;

	/**设备类型*/
	@Excel(name = "设备类型", width = 15)
    @ApiModelProperty(value = "设备类型")
    @Dict(dictTable ="device_Type",dicText = "name",dicCode = "code")
    @Field(name="device_type_code")
    private String device_type_code;

    /**携带工具*/
    @Excel(name = "携带工具", width = 15)
    @ApiModelProperty(value = "携带工具")
    private String tools;
    /**附件*/
    @Excel(name = "附件", width = 15)
    @ApiModelProperty(value = "附件")
    @Field(name="file_path")
    private String file_path;
    /**状态*/
    @Excel(name = "状态(0:待审批,1:已审批,2:已驳回)", width = 15)
    @ApiModelProperty(value = "状态(0:待审批,1:已审批,2:已驳回)")
    private Integer status;

}
