package com.aiurt.modules.weeklyplan.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 班组记录表
 * @Author: wgp
 * @Date: 2021-03-31
 * @Version: V1.0
 */
@Data
@TableName("bd_team")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "bd_team对象", description = "班组记录表")
@ToString
public class BdTeam implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "ID")
    private String id;
    /**
     * 上级ID
     */
    @Excel(name = "上级ID", width = 15)
    @ApiModelProperty(value = "上级ID")
    private String parentId;
    /**
     * 父ID 数据字典使用
     */
    @Excel(name = "父ID", width = 15)
    @ApiModelProperty(value = "父ID")
    private Integer pid;
    /**
     * 名称
     */
    @Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
    @Length(max = 255, message = "机构名称长度不能超过255")
    private String name;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    @Length(max = 255, message = "备注长度不能超过255")
    private String remark;
    /**
     * 类型(1组2段3线4其他)
     */
    @Excel(name = "类型(1组2段3线4其他)", width = 15, dicCode = "team_type_name")
    @Dict(dicCode = "team_type_name")
    @ApiModelProperty(value = "类型(1组2段3线4其他)")
    private Integer type;
    /**
     * 所属专业id，对应ht_dept表id
     */
    @Excel(name = "所属专业id，对应ht_dept表id", width = 15, dictTable = "bd_dept", dicText = "name", dicCode = "id")
    @Dict(dictTable = "bd_dept", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "所属专业id，对应ht_dept表id")
    private Integer deptId;
    /**
     * 所属线路id，对应ht_line表id，为null表示管辖所有线路，一个班组可管理多条线路（对应工区）
     */
    @Excel(name = "所属线路id，对应ht_line表id，为null表示管辖所有线路，一个班组可管理多条线路（对应工区）", width = 15, dictTable = "bd_line", dicText = "name", dicCode = "id")
    @Dict(dictTable = "bd_line", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "所属线路id，对应ht_line表id，为null表示管辖所有线路，一个班组可管理多条线路（对应工区）")
    private String lineId;
    /**
     * typeid
     */
    @Excel(name = "typeid", width = 15)
    @ApiModelProperty(value = "typeid")
    private Integer typeId;
    /**
     * 子系统
     */
    @Excel(name = "子系统", width = 15)
    @ApiModelProperty(value = "子系统")
    @Length(max = 255, message = "子系统长度不能超过255")
    private String subsystem;
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
    private Date createTime;
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
    private Date updateTime;

    /**
     * 是否有子节点
     */
    @Excel(name = "是否有子节点", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否有子节点")
    private String hasChild;


    @ApiModelProperty(value = "考勤地点")
    @Length(max = 255, message = "考勤地点长度不能超过255")
    private String attendancePlace;


    @ApiModelProperty(value = "打卡方式")
    @Length(max = 255, message = "打卡方式长度不能超过255")
    private String clockWay;

    @ApiModelProperty(value = "班组排序")
    private Integer scheduleNumber;


}
