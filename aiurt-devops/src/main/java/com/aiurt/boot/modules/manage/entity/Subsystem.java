package com.aiurt.boot.modules.manage.entity;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.aiurt.boot.modules.device.entity.DeviceType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: cs_subsystem
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Data
@TableName("cs_subsystem")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "子系统信息对象", description = "子系统信息对象")
public class Subsystem {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Integer id;
    /**
     * 名称
     */
    @Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
    private String systemName;
    /**
     * 编号
     */
    @Excel(name = "编号", width = 15)
    @ApiModelProperty(value = "编号")
    private String systemCode;
    /**
     * 说明
     */
    @Excel(name = "说明", width = 15)
    @ApiModelProperty(value = "说明")
    private String description;
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

    @TableField(exist = false)
    private List<DeviceType> deviceTypeList;

    /**技术员集合*/
    @TableField(exist = false)
    private String selectedSubSysUsers;

    public static final String SYSTEM_NAME = "system_name";

    public static final String SYSTEM_CODE = "system_code";
}
