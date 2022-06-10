package com.aiurt.boot.modules.manage.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: cs_collection
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Data
@TableName("cs_collection")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "回收站对象", description = "回收站对象")
public class Collection {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Integer id;
    /**
     * 内容
     */
    @Excel(name = "内容", width = 15)
    @ApiModelProperty(value = "内容")
    private String content;
    /**
     * 模块
     */
    @Excel(name = "模块", width = 15)
    @ApiModelProperty(value = "模块")
    private String module;
    /**
     * 恢复方法
     * this.getClass().getSimpleName()+".方法名"
     * 方法参数为一个string类型的数据
     */
    @Excel(name = "恢复方法", width = 15)
    @ApiModelProperty(value = "恢复方法")
    private String method;
    /**
     * 参数
     */
    @Excel(name = "参数", width = 15)
    @ApiModelProperty(value = "参数")
    private String params;
    /**
     * 删除标志
     */
    @Excel(name = "删除标志", width = 15)
    @ApiModelProperty(value = "删除标志")
    private Integer delFlag;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**
     * 更新时间
     */
    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
