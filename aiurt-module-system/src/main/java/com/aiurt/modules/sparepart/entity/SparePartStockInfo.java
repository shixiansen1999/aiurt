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
 * @Description: spare_part_stock_info
 * @Author: aiurt
 * @Date:   2022-07-20
 * @Version: V1.0
 */
@Data
@TableName("spare_part_stock_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="spare_part_stock_info对象", description="spare_part_stock_info")
public class SparePartStockInfo extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private String id;
	/**备件仓库名称*/
	@Excel(name = "备件仓库名称", width = 15)
    @ApiModelProperty(value = "备件仓库名称")
    private String warehouseName;
	/**备件仓库编号*/
	@Excel(name = "备件仓库编号", width = 15)
    @ApiModelProperty(value = "备件仓库编号")
    private String warehouseCode;
	/**备件仓库状态：1启用、2停用*/
	@Excel(name = "备件仓库状态：1启用、2停用", width = 15)
    @ApiModelProperty(value = "数据字典：warehouse_status ，备件仓库状态：1启用、2停用")
    @Dict(dicCode = "warehouse_status")
    private Integer warehouseStatus;
	/**备件仓库位置*/
	@Excel(name = "备件仓库位置", width = 15)
    @ApiModelProperty(value = "备件仓库位置")
    private String warehousePosition;
	/**组织id*/
	@Excel(name = "组织id", width = 15)
    @ApiModelProperty(value = "组织id")
    @Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "id")
    private String organizationId;
	/**删除状态 0-未删除 1-已删除*/
	@Excel(name = "删除状态 0-未删除 1-已删除", width = 15)
    @ApiModelProperty(value = "删除状态 0-未删除 1-已删除")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    private String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
    /**备注*/
    @ApiModelProperty(value = "备注")
    private String remarks;
    /**当前模块*/
    @ApiModelProperty(value = "当前模块")
    @TableField(exist = false)
    private String module;
}
