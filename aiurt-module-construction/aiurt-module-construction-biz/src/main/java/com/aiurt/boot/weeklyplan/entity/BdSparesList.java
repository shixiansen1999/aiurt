package com.aiurt.boot.weeklyplan.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: bd_spares_list
 * @Author: jeecg-boot
 * @Date:   2021-06-17
 * @Version: V1.0
 */
@Data
@TableName("bd_spares_list")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bd_spares_list对象", description="备件出入库")
public class BdSparesList implements Serializable {
    private static final long serialVersionUID = 1L;

	/**资产备件与设备备件明细表主键id，自增*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "资产备件与设备备件明细表主键id，自增")
    private Integer id;
	/**备件类型id，对应ht_spares表id*/
	@Excel(name = "备件类型", width = 15,dictTable = "bd_spares", dicText = "name", dicCode = "id",orderNum="2")
    @ApiModelProperty(value = "备件类型id，对应ht_spares表id")
    @Dict(dictTable = "bd_spares", dicText = "name", dicCode = "id")
    private Integer sparesId;
	/**备件种类id（1或2），对应ht_spares_type表id*/
    @ApiModelProperty(value = "备件种类id（1或2），对应ht_spares_type表id")
    @Dict(dictTable = "bd_spares_type", dicText = "type", dicCode = "id")
    private Integer sparesTypeId;
	/**备件编码*/
	@Excel(name = "备件编码", width = 15,orderNum="3")
    @ApiModelProperty(value = "备件编码")
    private String sparesCode;
	/**备件状态，1=闲置，2=线上，3=报废，4=报废退出系统*/
	@Excel(name = "备件状态", width = 15,dicCode = "spares_status",orderNum="8")
    @ApiModelProperty(value = "备件状态，1=闲置，2=线上，3=报废，4=报废退出系统")
    @Dict(dicCode = "spares_status")
    private Integer sparesStatus;
	/**备件当前所处备件库id，对应ht_spares_warehouse表id，备件状态为闲置时本栏非空*/
	@Excel(name = "备件当前所处备件库", width = 15,dictTable = "bd_spares_warehouse", dicText = "name", dicCode = "id",orderNum="9")
    @ApiModelProperty(value = "备件当前所处备件库id，对应ht_spares_warehouse表id，备件状态为闲置时本栏非空")
    @Dict(dictTable = "bd_spares_warehouse", dicText = "name", dicCode = "id")
    private Integer sparesWarehouseId;
	/**资产备件与设备备件在线上状态时，所属的设备id，其他状态下此栏为null*/
	@Excel(name = "关联设备名称", width = 15,dictTable = "bd_device_archives", dicText = "name", dicCode = "id",orderNum="10")
    @ApiModelProperty(value = "资产备件与设备备件在线上状态时，所属的设备id，其他状态下此栏为null")
    @Dict(dictTable = "bd_device_archives", dicText = "name", dicCode = "id")
    private Integer deviceId;
	/**厂商*/
	@Excel(name = "备件厂商", width = 15,dictTable = "bd_spares_manufacturers", dicText = "manufacturers", dicCode = "id",orderNum="4")
    @ApiModelProperty(value = "厂商")
    @Dict(dictTable = "bd_spares_manufacturers", dicText = "manufacturers", dicCode = "id")
    private String manufacturers;
	/**型号*/
	@Excel(name = "备件型号", width = 15,orderNum="5")
    @ApiModelProperty(value = "型号")
    private String type;
	/**备件专业id*/
	@Excel(name = "备件专业", width = 15,dictTable = "bd_spares_dept", dicText = "dept_name", dicCode = "id",orderNum="6")
    @ApiModelProperty(value = "备件专业id")
    @Dict(dictTable = "bd_spares_dept", dicText = "dept_name", dicCode = "id")
    private Integer sparesDeptId;
	/**备件名称*/
	@Excel(name = "备件名称", width = 15,orderNum="1")
    @ApiModelProperty(value = "备件名称")
    private String sparesName;
	/**备件数量*/
	@Excel(name = "备件数量", width = 15,orderNum="7")
    @ApiModelProperty(value = "备件数量")
    private Integer sparesNum;


    /**备件最低限定数量*/
    @ApiModelProperty(value = "备件最低限定数量")
    private Integer minNum;

	/**是否为易耗品类备件（0不是 1是）*/
	@Excel(name = "是否为易耗品类备件", width = 15,replace={"是_1","不是_0"},orderNum="11")
    @ApiModelProperty(value = "是否为易耗品类备件（0不是 1是）")
    private String consumable;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;

    @ApiModelProperty(value = "0表示显示退库，1表示显示出库")
    @TableField(exist = false)
    private Integer isOutOfStock;

    /**
     * 阈值
     */
    @ApiModelProperty(value = "0表示低于限定值备件，1表示高于限定值备件")
    @TableField(exist = false)
    private Integer threshold;

    @ApiModelProperty("备件仓库表id")
    @TableField(exist = false)
    private Integer bdSparesWarehouseId;

    @ApiModelProperty("备件仓库名称")
    @TableField(exist = false)
    private String bdSparesWarehouseName;

    @TableField(exist = false)
    private Integer siteId;

    @TableField(exist = false)
    private String siteName;
}
