package com.aiurt.modules.worklog.entity;

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
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/20
 * @desc
 */

@Data
@TableName("cs_station")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="cs_station对象", description="cs_station")
public class Station  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private String id;
    /**站点*/
    @Excel(name = "站点", width = 15)
    @ApiModelProperty(value = "站点")
    private String stationName;
    /**站点编号*/
    @Excel(name = "站点编号", width = 15)
    @ApiModelProperty(value = "站点编号")
    private String stationCode;
    /**序号*/
    @Excel(name = "序号", width = 15)
    @ApiModelProperty(value = "序号")
    private Integer sort;
    /**线路编码（线路表内）*/
    @Excel(name = "线路编码（线路表内）", width = 15)
    @ApiModelProperty(value = "线路编码（线路表内）")
    private String lineCode;
    /**线路名称*/
    @Excel(name = "线路名称", width = 15)
    @ApiModelProperty(value = "线路名称")
    private String lineName;
    /**经度*/
    @Excel(name = "经度", width = 15)
    @ApiModelProperty(value = "经度")
    private String longitude;
    /**纬度*/
    @Excel(name = "纬度", width = 15)
    @ApiModelProperty(value = "纬度")
    private String latitude;
    /**position*/
    @Excel(name = "position", width = 15)
    @ApiModelProperty(value = "position")
    private String position;
    /**描述*/
    @Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
    private String description;
    /**预警信息状态*/
    @Excel(name = "预警信息状态", width = 15)
    @ApiModelProperty(value = "预警信息状态")
    private Integer warningStatus;
    /**url地址*/
    @Excel(name = "url地址", width = 15)
    @ApiModelProperty(value = "url地址")
    private String url;
    /**开关站状态*/
    @Excel(name = "开关站状态", width = 15)
    @ApiModelProperty(value = "开关站状态")
    private Integer openStatus;
    /**站点电话号码*/
    @Excel(name = "站点电话号码", width = 15)
    @ApiModelProperty(value = "站点电话号码")
    private String phoneNum;
    /**站所类型（3：车站、4：变电所、5：办公大楼、6：车辆段、7：停车场、8：区间）*/
    @Excel(name = "站所类型（3：车站、4：变电所、5：办公大楼、6：车辆段、7：停车场、8：区间）", width = 15)
    @ApiModelProperty(value = "站所类型（3：车站、4：变电所、5：办公大楼、6：车辆段、7：停车场、8：区间）")
    @Dict(dicCode = "station_level_two")
    private Integer stationType;
    /**轮乘线路（逗号隔开）*/
    @Excel(name = "轮乘线路（逗号隔开）", width = 15)
    @ApiModelProperty(value = "轮乘线路（逗号隔开）")
    private String changeLineCode;
    /**是否轮乘站(1是，0否)*/
    @Excel(name = "是否轮乘站(1是，0否)", width = 15)
    @ApiModelProperty(value = "是否轮乘站(1是，0否)")
    private Integer isChange;
    /**是否首尾站(1是，0否)*/
    @Excel(name = "是否首尾站(1是，0否)", width = 15)
    @ApiModelProperty(value = "是否首尾站(1是，0否)")
    private Integer isFirstOrEnd;
    /**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**更新时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    /**删除标志*/
    @Excel(name = "删除标志", width = 15)
    @ApiModelProperty(value = "删除标志")
    private Integer delFlag;
    /**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
    /**更新人*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新人")
    private Date updateBy;
    /**级别*/
    @ApiModelProperty(value = "级别")
    @TableField(exist = false)
    private Integer level;
}

