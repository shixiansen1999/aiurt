package com.aiurt.modules.sparepart.entity.dto;

import com.aiurt.common.aspect.annotation.DeptFilterColumn;
import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * 站台门专用导出
 *
 * @author LKJ
 */
@Data
public class SparePartScrapSpecialDTO {
    /**主键id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
    /**序号*/
    @Excel(name = "序号", width = 15)
    @TableField(exist = false)
    private String number;
    /**物资类型名称*/
    @Excel(name = "物资类型", width = 15,orderNum = "1")
    @ApiModelProperty(value = "物资类型名称")
    @TableField(exist = false)
    private  String  typeName;
    /**物资名称*/
    @Excel(name = "物资名称", width = 15,orderNum = "2")
    @ApiModelProperty(value = "物资名称")
    @TableField(exist = false)
    private String name;
    /**处置数量*/
    @Excel(name = "数量", width = 15,orderNum = "5")
    @ApiModelProperty(value = "处置数量")
    private Integer num;

    /**处置原因*/
    @Excel(name = "报废原因", width = 15,orderNum = "10")
    @ApiModelProperty(value = "处置原因")
    private String reason;

    /**规格型号*/
    @Excel(name = "规格型号",width = 15,orderNum = "3")
    @TableField(exist = false)
    @ApiModelProperty(value = "规格型号")
    private String specifications;

    /**单位*/
    @Excel(name = "单位",width = 15,orderNum = "4")
    @ApiModelProperty(value = " 单位")
    @TableField(exist = false)
    private String unit;

    /**所属部门*/
    @Excel(name = "归属部门",width = 15,orderNum = "6",dictTable ="sys_depart",dicText = "depart_name",dicCode = "org_code")
    @ApiModelProperty(value = "所属部门")
    @DeptFilterColumn
    @Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
    private String sysOrgCode;
    @Excel(name = "存放位置", width = 15,orderNum = "7")
    @ApiModelProperty(value = "存放位置")
    private String location;

    @Excel(name = "备注", width = 15,orderNum = "11")
    @ApiModelProperty(value = "备注")
    private String remarks;

    @Excel(name = "生命周期", width = 15,orderNum = "8")
    @ApiModelProperty(value = "生命周期")
    private String lifeCycle;

    @Excel(name = "使用时间", width = 15,orderNum = "9")
    @ApiModelProperty(value = "使用时间")
    private String usageTime;
}
