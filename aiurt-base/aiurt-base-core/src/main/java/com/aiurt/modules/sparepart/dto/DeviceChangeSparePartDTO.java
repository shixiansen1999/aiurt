package com.aiurt.modules.sparepart.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fgw
 */
@Data
public class DeviceChangeSparePartDTO implements Serializable {

    private static final long serialVersionUID = 2484496775288313442L;

    /**主键id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id，自动递增")
    private String id;

    /**类型:1-检修 2-故障*/
    private Integer type;

    /**故障/检修编号*/
    @Excel(name = "故障/检修编号", width = 15)
    @ApiModelProperty(value = "故障/检修编号")
    private String code;

    /**维修记录id*/
    @ApiModelProperty(value = "维修记录id")
    private String repairRecordId;

    /**设组id*/
    @ApiModelProperty(value = "设组编码")
    private String deviceCode;

    @ApiModelProperty(value = "设组名称")
    private String deviceName;

    /**原组件编号*/
    @ApiModelProperty(value = "原组件编号")
    private String oldSparePartCode;

    @ApiModelProperty(value = "原组件名称")
    private String oldSparePartName;

    /**原组件数量*/
    @ApiModelProperty(value = "原组件数量")
    private Integer oldSparePartNum;

    /**原组件所在班组*/
    @ApiModelProperty(value = "原组件所在班组编码")
    private String oldOrgCode;

    @ApiModelProperty("机构名称")
    private String oldOrgName;

    /**新组件编号*/
    @ApiModelProperty(value = "新组件编号")
    private String newSparePartCode;

    @ApiModelProperty(value = "新组件名称")
    private String newSparePartName;

    /**新组件数量*/
    @ApiModelProperty(value = "新组件数量")
    private Integer newSparePartNum;

    /**新组件所在班组*/
    private String newOrgCode;

    /**删除状态：0.未删除 1已删除*/
    @ApiModelProperty(value = "删除状态：0.未删除 1已删除")
    private Integer delFlag;

    /**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;

    /**创建时间*/
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**修改时间*/

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    /**是否易耗品(1是,0否)*/
    @ApiModelProperty(value = "是否易耗品(1是,0否)")
    private String consumables;

    @ApiModelProperty(value = "出库记录表ID")
    private String outOrderId;

    @ApiModelProperty("规格")
    private String specifications;
}
