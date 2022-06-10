package com.aiurt.boot.modules.manage.entity;

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

import java.util.Date;

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
@ApiModel(value = "常见故障管理对象", description = "常见故障管理对象")
public class CommonFault {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 子系统id
     */
    @Excel(name = "子系统id", width = 15)
    @ApiModelProperty(value = "子系统id")
    private Long subId;

    /**
     * 设备分类id
     */
    @Excel(name = "设备分类id", width = 15)
    @ApiModelProperty(value = "设备分类id")
    private Long equipId;

    /**
     * 故障现象
     */
    @Excel(name = "故障现象", width = 15)
    @ApiModelProperty(value = "故障现象")
    private String fault;

    /**
     * 发生次数
     */
    @Excel(name = "发生次数", width = 15)
    @ApiModelProperty(value = "发生次数")
    private Integer num;

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

    /**
     * 关联故障知识库id
     */
    @Excel(name = "关联故障知识库id", width = 15)
    @ApiModelProperty(value = "关联故障知识库id")
    private Long knowledgeId;



    @TableField(exist = false)
    private String systemName;
    @TableField(exist = false)
    private String equipName;

    public static final String ID = "id";
}
