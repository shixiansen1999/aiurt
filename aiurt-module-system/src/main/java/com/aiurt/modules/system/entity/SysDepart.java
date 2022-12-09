package com.aiurt.modules.system.entity;

import com.aiurt.common.aspect.annotation.DeptFilterColumn;
import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * 部门表
 * <p>
 *
 * @Author Steve
 * @Since  2019-01-22
 */
@Data
@TableName("sys_depart")
public class SysDepart implements Serializable {
    private static final long serialVersionUID = 1L;

	/**ID*/
	@ApiModelProperty(value = "ID")
	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	/**父机构ID*/
	@ApiModelProperty(value = "父机构ID")
	private String parentId;
	/**机构/部门名称*/
	@ApiModelProperty(value = "机构/部门名称")
	@Excel(name="机构/部门名称",width=15)
	private String departName;
	/**机构全称*/
	@ApiModelProperty(value = "机构全称")
	@Excel(name="机构全称",width=15)
	private String departFullName;
	/**机构电话*/
	@Excel(name = "机构电话", width = 15)
	@ApiModelProperty(value = "机构电话")
	private String departPhoneNum;
	/**英文名*/
	@ApiModelProperty(value = "英文名")
	@Excel(name="英文名",width=15)
	private String departNameEn;
	/**缩写*/
	@ApiModelProperty(value = "缩写")
	private String departNameAbbr;
	/**排序*/
	@ApiModelProperty(value = "排序")
	@Excel(name="排序",width=15)
	private Integer departOrder;
	/**描述*/
	@ApiModelProperty(value = "描述")
	@Excel(name="描述",width=15)
	private String description;
	/**机构类别 1=公司，2=组织机构，3=岗位*/
	@ApiModelProperty(value = "机构类别 1=公司，2=组织机构，3=岗位")
	@Excel(name="机构类别",width=15,dicCode="org_category")
	private String orgCategory;
	/**机构类型*/
	@ApiModelProperty(value = "机构类型")
	private String orgType;
	/**机构编码*/
	@ApiModelProperty(value = "机构编码")
	@Excel(name="机构编码",width=15)
	@DeptFilterColumn
	private String orgCode;
	/**手机号*/
	@ApiModelProperty(value = "手机号")
	@Excel(name="手机号",width=15)
	private String mobile;
	/**传真*/
	@ApiModelProperty(value = "传真")
	@Excel(name="传真",width=15)
	private String fax;
	/**地址*/
	@ApiModelProperty(value = "地址")
	@Excel(name="地址",width=15)
	private String address;
	/**备注*/
	@ApiModelProperty(value = "备注")
	@Excel(name="备注",width=15)
	private String memo;
	/**状态（1启用，0不启用）*/
	@ApiModelProperty(value = "状态（1启用，0不启用）")
	@Dict(dicCode = "depart_status")
	private String status;
	/**删除状态（0，正常，1已删除）*/
	@ApiModelProperty(value = "删除状态（0，正常，1已删除）")
	@Dict(dicCode = "del_flag")
	private String delFlag;
	/**对接企业微信的ID*/
	@ApiModelProperty(value = "对接企业微信的ID")
	private String qywxIdentifier;
	/**创建人*/
	@ApiModelProperty(value = "创建人")
	private String createBy;
	/**创建日期*/
	@ApiModelProperty(value = "创建日期")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	/**更新人*/
	@ApiModelProperty(value = "更新人")
	private String updateBy;
	/**更新日期*/
	@ApiModelProperty(value = "更新日期")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	/**联系人id*/
	@ApiModelProperty(value = "联系人id")
	@Excel(name="联系人名称",width=15)
	private String contactId;
	/**联系人方式*/
	@ApiModelProperty(value = "联系人方式")
	@Excel(name="联系人方式",width=15)
	private String concatWay;
	/**管理负责人id*/
	@ApiModelProperty(value = "管理负责人id")
	@Excel(name="管理负责人名称",width=15)
	private String managerId;
	/**技术负责人id*/
	@ApiModelProperty(value = "技术负责人id")
	@Excel(name="技术负责人名称",width=15)
	private String technicalId;
	/**班组类别*/
	@ApiModelProperty(value = "班组类别")
	@Excel(name="班组类别",width=15)
	@Dict(dicCode = "team_type")
	private Integer teamType;
    //update-begin---author:wangshuai ---date:20200308  for：[JTC-119]在部门管理菜单下设置部门负责人，新增字段负责人ids和旧的负责人ids
    /**部门负责人的ids*/
	@ApiModelProperty(value = "部门负责人的ids")
	@TableField(exist = false)
	private String directorUserIds;
    /**旧的部门负责人的ids(用于比较删除和新增)*/
	@ApiModelProperty(value = "旧的部门负责人的ids")
	@TableField(exist = false)
    private String oldDirectorUserIds;
    //update-end---author:wangshuai ---date:20200308  for：[JTC-119]新增字段负责人ids和旧的负责人ids

	/**
	 * 机构编码层级结构
	 */
	@ApiModelProperty(value = "机构编码层级结构")
	private String orgCodeCc;

	/**
	 * 重写equals方法
	 */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
			return true;
		}
        if (o == null || getClass() != o.getClass()) {
			return false;
		}
        if (!super.equals(o)) {
			return false;
		}
        SysDepart depart = (SysDepart) o;
        return Objects.equals(id, depart.id) &&
                Objects.equals(parentId, depart.parentId) &&
                Objects.equals(departName, depart.departName) &&
                Objects.equals(departNameEn, depart.departNameEn) &&
                Objects.equals(departNameAbbr, depart.departNameAbbr) &&
                Objects.equals(departOrder, depart.departOrder) &&
                Objects.equals(description, depart.description) &&
                Objects.equals(orgCategory, depart.orgCategory) &&
                Objects.equals(orgType, depart.orgType) &&
                Objects.equals(orgCode, depart.orgCode) &&
                Objects.equals(mobile, depart.mobile) &&
                Objects.equals(fax, depart.fax) &&
                Objects.equals(address, depart.address) &&
                Objects.equals(memo, depart.memo) &&
                Objects.equals(status, depart.status) &&
                Objects.equals(delFlag, depart.delFlag) &&
                Objects.equals(createBy, depart.createBy) &&
                Objects.equals(createTime, depart.createTime) &&
                Objects.equals(updateBy, depart.updateBy) &&
                Objects.equals(updateTime, depart.updateTime);
    }

    /**
     * 重写hashCode方法
     */
    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id, parentId, departName,
        		departNameEn, departNameAbbr, departOrder, description,orgCategory,
        		orgType, orgCode, mobile, fax, address, memo, status,
        		delFlag, createBy, createTime, updateBy, updateTime);
    }
}
