package com.aiurt.boot.standard.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.aiurt.boot.standard.dto.OrgVO;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.aspect.annotation.MajorFilterColumn;
import com.aiurt.common.aspect.annotation.SystemFilterColumn;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: patrol_standard
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Data
@TableName("patrol_standard")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="patrol_standard对象", description="patrol_standard")
public class PatrolStandard extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private java.lang.String id;
	/**标准编号*/
    @ApiModelProperty(value = "标准编号")
    private java.lang.String code;
	/**巡检表名*/
	@Excel(name = "巡视标准表名称", width = 15,needMerge = true)
    @ApiModelProperty(value = "巡视标准表名称")
    private java.lang.String name;
	/**专业code*/
    @Excel(name = "适用专业", width = 15,needMerge = true)
    @ApiModelProperty(value = "专业code")
    @MajorFilterColumn
    private java.lang.String professionCode;
	/**适用系统code*/
    @Excel(name = "适用子系统", width = 15,needMerge = true)
    @ApiModelProperty(value = "适用系统code")
    @SystemFilterColumn
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private java.lang.String subsystemCode;
    /**与设备类型相关：0否 1 是*/
    @ApiModelProperty(value = "与设备类型相关：0否 1 是")
    private java.lang.Integer deviceType;
    /**与设备类型相关：0否 1 是*/
    @Excel(name = "是否与设备类型相关", width = 15,needMerge = true,dict = "")
    @ApiModelProperty(value = "与设备类型相关：0否 1 是")
    @TableField(exist = false)
    private java.lang.String deviceTypeNames;
    /**指定具体设备：0否 1 是*/
    @ApiModelProperty(value = "指定具体设备：0否 1 是")
    private java.lang.Integer specifyDevice;
    /**生效状态：0停用 1启用*/
    @ApiModelProperty(value = "生效状态：0停用 1启用")
    private java.lang.Integer status;
    @Excel(name = "生效状态", width = 15,needMerge = true)
    @ApiModelProperty(value = "生效状态：0停用 1启用")
    @TableField(exist = false)
    private java.lang.String statusName;
	/**设备类型code*/
	@Excel(name = "设备类型", width = 15,needMerge = true)
    @ApiModelProperty(value = "设备类型code")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private java.lang.String deviceTypeCode;
	/**标准表说明*/
    @ApiModelProperty(value = "标准表说明")
    private java.lang.String remark;
	/**标准制定人ID*/
    @ApiModelProperty(value = "标准制定人ID")
    private java.lang.String userId;
	/**删除状态： 0未删除 1已删除*/
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    @Dict(dicCode = "realname",dictTable="sys_user",dicText="username")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;

    @ExcelCollection(name = "配置项")
    @ApiModelProperty(value = "配置项")
    @TableField(exist = false)
    private List<PatrolStandardItems> patrolStandardItemsList;
    /**前端传组织机构codes*/
    @ApiModelProperty(value = "组织机构")
    @TableField(exist = false)
    private java.lang.String[] orgCodes;
    /**
     * 组织机构
     */
    @ApiModelProperty(value = "组织机构")
    @TableField(exist = false)
    private List<OrgVO> orgCodeList;
    /**
     * 组织机构名称
     */
    @ApiModelProperty(value = "组织机构名称")
    @TableField(exist = false)
    private java.lang.String orgName;
    /**
     * 导出传参
     */
    @ApiModelProperty(value = "组织机构名称")
    @TableField(exist = false)
    private List<String> selections;
    /**
     * 标准编号集合
     */
    @TableField(exist = false)
    private List<String> codes;
}
