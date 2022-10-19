package com.aiurt.modules.weeklyPlan.entity;

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
import org.hibernate.validator.constraints.Length;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 工区表，存储工区包含工作场所及对应工班信息
 * @Author: wgp
 * @Date: 2021-03-31
 * @Version: V1.0
 */
@Data
@TableName("bd_site")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "bd_site对象", description = "工区表，存储工区包含工作场所及对应工班信息")
public class BdSite implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "ID")
    private Integer id;
    /**
     * 工区名称
     */
    @Excel(name = "工区名称", width = 15)
    @ApiModelProperty(value = "工区名称")
    @Length(max = 32, message = "线路编码长度不能超过32")
    private String name;
    /**
     * 位置
     */
    @Dict(dictTable = "bd_station", dicText = "name", dicCode = "code")
    @ApiModelProperty(value = "位置")
    private String position;

    /**
     * 所属班组,多个逗号隔开
     */
    @Dict(dictTable = "bd_team", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "所属班组")
    private String teamId;
    /**
     * 线路
     */
    @Dict(dictTable = "bd_line", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "线路")
    private String lineId;
    /**
     * 专业
     */
    @Dict(dictTable = "bd_dept", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "专业")
    private String deptId = "2";
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;


    @ApiModelProperty("备件仓库表id")
    @TableField(exist = false)
    private Integer bdSparesWarehouseId;

    @ApiModelProperty("备件仓库名称")
    @TableField(exist = false)
    private String bdSparesWarehouseName;

}
