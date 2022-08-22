package com.aiurt.modules.workarea.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/8/11
 * @desc
 */
@Data
public class WorkAreaDTO extends DictEntity{
    /**工区信息表主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "工区信息表主键")
    private String id;
    /**工区编号*/
    @Excel(name = "工区编号", width = 15)
    @ApiModelProperty(value = "工区编号")
    @TableField(value = "`code`")
    private String code;
    /**工区名称*/
    @Excel(name = "工区名称", width = 15)
    @ApiModelProperty(value = "工区名称")
    @TableField(value = "`name`")
    private String name;
    /**工区类型：1运行工区、2检修工区*/
    @Excel(name = "工区类型：1运行工区、2检修工区", width = 15)
    @ApiModelProperty(value = "工区类型：1运行工区、2检修工区")
    @Dict(dicCode = "work_area_type")
    private Integer type;
    /**工区地点*/
    @Excel(name = "工区地点", width = 15)
    @ApiModelProperty(value = "工区地点")
    @TableField(value = "`position`")
    private String position;
    /**专业编号*/
    @Excel(name = "专业编号", width = 15)
    @ApiModelProperty(value = "专业编号")
    private String majorCode;
    /**工区管理负责人ID*/
    @Excel(name = "工区管理负责人ID", width = 15)
    @ApiModelProperty(value = "工区管理负责人ID")
    private String managerId;
    /**工区技术负责人ID*/
    @Excel(name = "工区技术负责人ID", width = 15)
    @ApiModelProperty(value = "工区技术负责人ID")
    private String technicalId;
    /**删除状态： 0未删除 1已删除*/
    @Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private Integer delFlag;
    /**专业名称*/
    @Excel(name = "专业名称", width = 15)
    @ApiModelProperty(value = "专业名称")
    @TableField(value = "`majorName`")
    private String majorName;
    /**工区管理负责人名称*/
    @Excel(name = "工区管理负责人名称", width = 15)
    @ApiModelProperty(value = "工区管理负责人名称")
    @TableField(value = "`managerName`")
    private String managerName  ;
    /**工区技术负责人名称*/
    @Excel(name = "工区技术负责人名称", width = 15)
    @ApiModelProperty(value = "工区技术负责人名称")
    @TableField(value = "`technicalName`")
    private String technicalName;
    /**组织机构编号*/
    @Excel(name = "组织机构编号", width = 15)
    @ApiModelProperty(value = "组织机构编号")
    private List <String> orgCodeList;
    /**线路编号*/
    @Excel(name = "线路编号", width = 15)
    @ApiModelProperty(value = "线路编号")
    private List<String> lineCodeList;
    /**站点编号*/
    @Excel(name = "站点编号", width = 15)
    @ApiModelProperty(value = "站点编号")
    private List<String> stationCodeList;
    /**线路站点名称*/
    @Excel(name = "线路站点名称", width = 15)
    @ApiModelProperty(value = "线路站点名称")
    private String  lineStationName;
    /**组织机构名称*/
    @Excel(name = "组织机构名称", width = 15)
    @ApiModelProperty(value = "组织机构名称")
    private String  orgName;
}
