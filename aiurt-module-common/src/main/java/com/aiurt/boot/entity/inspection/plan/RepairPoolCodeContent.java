package com.aiurt.boot.entity.inspection.plan;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: repair_pool_code_content
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Data
@TableName("repair_pool_code_content")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="repair_pool_code_content对象", description="repair_pool_code_content")
public class RepairPoolCodeContent implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private java.lang.String id;
	/**检查项编号*/
	@Excel(name = "检查项编号", width = 15)
    @ApiModelProperty(value = "检查项编号")
    private java.lang.String code;
	/**检修项名称*/
	@Excel(name = "检修项名称", width = 15)
    @ApiModelProperty(value = "检修项名称")
    private java.lang.String name;
	/**检修标准id，关联repair_pool_code表的id*/
	@Excel(name = "检修标准id，关联repair_pool_code表的id", width = 15)
    @ApiModelProperty(value = "检修标准id，关联repair_pool_code表的id")
    private java.lang.String repairPoolCodeId;
	/**维修内容*/
	@Excel(name = "维修内容", width = 15)
    @ApiModelProperty(value = "维修内容")
    private java.lang.String maintenanceContent;
	/**质量标准*/
	@Excel(name = "质量标准", width = 15)
    @ApiModelProperty(value = "质量标准")
    private java.lang.String qualityStandard;
	/**排序编号*/
	@Excel(name = "排序编号", width = 15)
    @ApiModelProperty(value = "排序编号")
    private java.lang.Integer sortNo;
	/**父级id，顶级为0*/
	@Excel(name = "父级id，顶级为0", width = 15)
    @ApiModelProperty(value = "父级id，顶级为0")
    private java.lang.String pid;
	/**是否有孩子节点*/
	@Excel(name = "是否有孩子节点", width = 15)
    @ApiModelProperty(value = "是否有孩子节点")
    private java.lang.String hasChild;
	/**检查项类型，是否是检查项：0否 1是*/
	@Excel(name = "检查项类型，是否是检查项：0否 1是", width = 15)
    @ApiModelProperty(value = "检查项类型，是否是检查项：0否 1是")
    private java.lang.Integer type;
	/**数据字典：1 开关项、2 选择项、3 输入项*/
	@Excel(name = "数据字典：1 开关项、2 选择项、3 输入项", width = 15)
    @ApiModelProperty(value = "数据字典：1 开关项、2 选择项、3 输入项")
    private java.lang.Integer statusItem;
	/**选择项关联的数据字典*/
	@Excel(name = "选择项关联的数据字典", width = 15)
    @ApiModelProperty(value = "选择项关联的数据字典")
    private java.lang.String dictCode;
	/**数据校验字段*/
	@Excel(name = "数据校验字段", width = 15)
    @ApiModelProperty(value = "数据校验字段")
    private java.lang.String dataCheck;
	/**删除状态*/
	@Excel(name = "删除状态", width = 15)
    @ApiModelProperty(value = "删除状态")
    private java.lang.Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private java.lang.String updateBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;
}
