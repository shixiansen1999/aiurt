package com.aiurt.modules.sparepart.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

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
 * @Description: spare_part_malfunction
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Data
@TableName("spare_part_malfunction")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="spare_part_malfunction对象", description="spare_part_malfunction")
public class SparePartMalfunction implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private String id;
	/**出库记录表ID*/
    @ApiModelProperty(value = "出库记录表ID")
    private String outOrderId;
	/**维修记录单号*/
	@Excel(name = "维修记录单号", width = 15,mergeVertical = true)
    @ApiModelProperty(value = "维修记录单号")
    private String maintenanceRecord;
	/**故障设备编号*/
    @ApiModelProperty(value = "故障设备编号")
    @Dict(dictTable ="device",dicText = "name",dicCode = "code")
    private String malfunctionDeviceCode;
    /**故障设备名称*/
    @Excel(name = "故障设备", width = 15,mergeVertical = true)
    @ApiModelProperty(value = "故障设备名称")
    @TableField(exist = false)
    private String deviceName;
	/**故障类别：1设备故障、2外界妨害、3其他*/
    @ApiModelProperty(value = "故障类别：1设备故障、2外界妨害、3其他")
    @Dict(dicCode = "malfunction_type")
    private Integer malfunctionType;
    /**故障类别名称*/
    @Excel(name = "故障类别", width = 15,mergeVertical = true)
    @ApiModelProperty(value = "故障类别名称")
    @TableField(exist = false)
    private String malfunctionTypeName;
	/**详细描述*/
    @ApiModelProperty(value = "详细描述")
    private String description;
	/**替换数量*/
	@Excel(name = "替换数量", width = 15,mergeVertical = true)
    @ApiModelProperty(value = "替换数量")
    private Integer replaceNumber;
	/**维修机构ID*/
    @ApiModelProperty(value = "维修机构ID")
    @Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    private String orgId;
    /**维修机构名称*/
    @Excel(name = "维修机构", width = 15,mergeVertical = true)
    @ApiModelProperty(value = "维修机构名称")
    @TableField(exist = false)
    private String orgName;
	/**维修用戶ID*/
    @ApiModelProperty(value = "维修用戶ID")
    @Dict(dictTable ="sys_user",dicText = "realname",dicCode = "username")
    private String maintainUserId;
    /**维修时间*/
    @Excel(name = "维修时间", width = 15, format = "yyyy-MM-dd",mergeVertical = true)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "维修时间")
    private Date maintainTime;
    /**维修用戶名称*/
    @Excel(name = "维修人", width = 15,mergeVertical = true)
    @ApiModelProperty(value = "维修用戶名称")
    @TableField(exist = false)
    private String userName;

	/**删除状态： 0未删除 1已删除*/
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    @Dict(dictTable ="sys_user",dicText = "realname",dicCode = "username")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    @Dict(dictTable ="sys_user",dicText = "realname",dicCode = "username")
    private String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    /**維修時間-起始*/
    @ApiModelProperty(value = "維修時間-起始")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @TableField(exist = false)
    private Date maintainTimeBegin;
    /**維修時間-結束*/
    @ApiModelProperty(value = "維修時間-結束")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @TableField(exist = false)
    private Date maintainTimeEnd;
    /**ids*/
    @ApiModelProperty(value = "ids")
    @TableField(exist = false)
    private List<String> ids;
    /**物资编码*/
    @Excel(name = "物资编码", width = 15)
    @ApiModelProperty(value = "物资编码")
    @TableField(exist = false)
    private String materialsCode;
    /**物资名称*/
    @Excel(name = "物资名称", width = 15)
    @ApiModelProperty(value = "物资名称")
    @TableField(exist = false)
    private String materialsName;
    /**被替换的组件编码*/
    @Excel(name = "被替换的组件编码", width = 15)
    @ApiModelProperty(value = "被替换的组件编码")
    @TableField(exist = false)
    private String replaceSubassemblyCode;
    /**替换的组件编码*/
    @Excel(name = "替换的组件编码", width = 15)
    @ApiModelProperty(value = "替换的组件编码")
    @TableField(exist = false)
    private String subassemblyCode;

}
