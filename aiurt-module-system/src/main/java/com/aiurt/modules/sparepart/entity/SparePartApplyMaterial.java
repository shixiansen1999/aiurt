package com.aiurt.modules.sparepart.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;

import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @Description: spare_part_apply_material
 * @Author: aiurt
 * @Date:   2022-07-20
 * @Version: V1.0
 */
@Data
@TableName("spare_part_apply_material")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="spare_part_apply_material对象", description="spare_part_apply_material")
public class SparePartApplyMaterial implements Serializable {
    private static final long serialVersionUID = 1L;

	/**自增主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "自增主键id")
    private String id;
    /**申领id*/
    @Excel(name = "申领id", width = 15)
    @ApiModelProperty(value = "申领id")
    private String applyId;
    /**二级库出库单号*/
    @Excel(name = "二级库出库单号", width = 15)
    @ApiModelProperty(value = "二级库出库单号")
    @TableField(exist = false)
    private String orderCode;
    /**出库仓库(申领仓库)*/
    @Excel(name = "出库仓库(申领仓库)", width = 15)
    @ApiModelProperty(value = "出库仓库(申领仓库)")
    @Dict(dictTable ="stock_level2_info",dicText = "warehouse_name",dicCode = "warehouse_code")
    private String warehouseCode;
	/**申领编号*/
	@Excel(name = "申领编号", width = 15)
    @ApiModelProperty(value = "申领编号")
    private String applyCode;
	/**物资编号*/
	@Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
    private String materialCode;
    /**仓库现有库存*/
    @Excel(name = "仓库现有库存")
    @ApiModelProperty(value = "仓库现有库存")
    private  Integer  inventory;
	/**申请出库数量*/
	@Excel(name = "申请出库数量", width = 15)
    @ApiModelProperty(value = "申请出库数量")
    private Integer applyNum;
	/**实际出库数量*/
	@Excel(name = "实际出库数量", width = 15)
    @ApiModelProperty(value = "实际出库数量")
    private Integer actualNum;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remarks;
	/**删除状态(0.未删除 1.已删除)*/
	@Excel(name = "删除状态(0.未删除 1.已删除)", width = 15)
    @ApiModelProperty(value = "删除状态(0.未删除 1.已删除)")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    @Dict(dictTable ="sys_user",dicText = "realname",dicCode = "username")
    private String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    @Dict(dictTable ="sys_user",dicText = "realname",dicCode = "username")
    private String updateBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
    /**所属专业-用于前端回显*/
    @ApiModelProperty(value = "所属专业")
    @TableField(exist = false)
    private  String  majorName;
    /**所属子系统-用于前端回显*/
    @ApiModelProperty(value = "所属子系统")
    @TableField(exist = false)
    private  String  systemName;
    /**物资名称-用于前端回显*/
    @Excel(name = "物资名称", width = 15)
    @ApiModelProperty(value = "物资名称")
    private String materialName;
    /**物资分类-用于前端回显*/
    @ApiModelProperty(value = "物资分类")
    @TableField(exist = false)
    private  String  baseTypeCodeName;
    /**物资类型-用于前端回显*/
    @ApiModelProperty(value = "物资类型")
    @TableField(exist = false)
    private  String  typeName;
    /**现有库存-用于前端回显*/
    @Excel(name = "现有库存")
    @ApiModelProperty(value = "现有库存")
    private  Integer  num;
    /**单位-用于前端回显*/
    @ApiModelProperty(value = " 单位")
    @TableField(exist = false)
    private String unit;

}
