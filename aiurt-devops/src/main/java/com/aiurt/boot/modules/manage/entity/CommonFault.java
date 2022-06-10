package com.aiurt.boot.modules.manage.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: cs_common_fault
 * @Author: swsc
 * @Date: 2021-09-16
 * @Version: V1.0
 */
@Data
@TableName("cs_common_fault")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "cs_common_fault对象", description = "cs_common_fault")
public class CommonFault {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Integer id;

    /**
     * 子系统id
     */
    @Excel(name = "子系统id", width = 15)
    @ApiModelProperty(value = "子系统id")
    private Integer subId;

    /**
     * 设备分类id
     */
    @Excel(name = "设备分类id", width = 15)
    @ApiModelProperty(value = "设备分类id")
    private Integer equipId;

    /**
     * 故障现象
     */
    @Excel(name = "故障现象", width = 15)
    @ApiModelProperty(value = "故障现象")
    private String fault;

    /**
     * 删除标志
     */
    @Excel(name = "删除标志", width = 15)
    @ApiModelProperty(value = "删除标志")
    private Integer delFlag;

    /**
     * createTime
     */
    @Excel(name = "createTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "createTime")
    private Date createTime;

    /**
     * updateTime
     */
    @Excel(name = "updateTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "updateTime")
    private Date updateTime;


    @TableField(exist = false)
    private String systemName;
    @TableField(exist = false)
    private String equipName;
}
