package com.aiurt.modules.faultknowledgebasetype.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 故障知识分类
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
@Data
@TableName("fault_knowledge_base_type")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fault_knowledge_base_type对象", description="故障知识分类")
public class FaultKnowledgeBaseType implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private Integer id;
	/**子系统编号:0的时候为其他，非0为系统编号*/
	@Excel(name = "子系统编号:0的时候为其他，非0为系统编号", width = 15)
    @ApiModelProperty(value = "子系统编号:0的时候为其他，非0为系统编号")
    private String systemCode;
	/**父id:0的时候为子系统下第一级*/
	@Excel(name = "父id:0的时候为子系统下第一级", width = 15)
    @ApiModelProperty(value = "父id:0的时候为子系统下第一级")
    private Integer pid;
	/**类型名称*/
	@Excel(name = "类型名称", width = 15)
    @ApiModelProperty(value = "类型名称")
    private String name;
	/**删除状态:0.未删除 1已删除*/
	@Excel(name = "删除状态:0.未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态:0.未删除 1已删除")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;
	/**创建时间,CURRENT_TIMESTAMP*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间,CURRENT_TIMESTAMP")
    private Date createTime;
	/**修改时间,根据当前时间戳更新*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间,根据当前时间戳更新")
    private Date updateTime;
	/**故障知识分类编码*/
	@Excel(name = "故障知识分类编码", width = 15)
    @ApiModelProperty(value = "故障知识分类编码")
    private String code;
	/**专业编码*/
	@Excel(name = "专业编码", width = 15)
    @ApiModelProperty(value = "专业编码")
    private String majorCode;
	/**故障知识编码层级结构*/
	@Excel(name = "故障知识编码层级结构", width = 15)
    @ApiModelProperty(value = "故障知识编码层级结构")
    private String codeCc;
}
