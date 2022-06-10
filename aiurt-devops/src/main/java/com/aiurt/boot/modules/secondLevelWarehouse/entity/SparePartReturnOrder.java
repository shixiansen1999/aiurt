package com.aiurt.boot.modules.secondLevelWarehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author WangHongTao
 * @Date 2021/11/15
 */
@Data
@TableName("spare_part_return_order")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="spare_part_return_order对象", description="备件退库表")
public class SparePartReturnOrder {

    /**主键id*/
    @TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id")
    private  Long  id;

    /**物资编号*/
    @Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
    @NotBlank(message = "物资编号不能为空")
    private  String  materialCode;

    /**出库数量*/
    @Excel(name = "退库数量", width = 15)
    @ApiModelProperty(value = "退库数量")
    @NotNull(message = "退库数量不能为空")
    private  Integer  num;

    @ApiModelProperty("备注")
    private String remarks;

    /**所在班组*/
    @Excel(name = "所在班组", width = 15)
    @ApiModelProperty(value = "所在班组")
    private  String  orgId;

    /**删除状态(0.未删除 1.已删除)*/
    @Excel(name = "删除状态(0.未删除 1.已删除)", width = 15)
    @ApiModelProperty(value = "删除状态(0.未删除 1.已删除)")
    @TableLogic
    private  Integer  delFlag;

    /**创建人*/
    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private  String  createBy;

    /**修改人*/
    @Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
    private  String  updateBy;

    /**退库时间*/
    @Excel(name = "退库时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "退库时间")
    @NotNull(message = "退库时间不能为空")
    private  java.util.Date  returnTime;

    /**创建时间*/
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private  java.util.Date  createTime;

    /**修改时间*/
    @Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private  java.util.Date  updateTime;


    private static final String ID = "id";
    private static final String MATERIAL_CODE = "material_code";
    private static final String NUM = "num";
    private static final String WAREHOUSE_CODE = "warehouse_code";
    private static final String REMARKS = "remarks";
    private static final String DEL_FLAG = "del_flag";
    private static final String CREATE_BY = "create_by";
    private static final String UPDATE_BY = "update_by";
    private static final String CREATE_TIME = "create_time";
    private static final String UPDATE_TIME = "update_time";
}
