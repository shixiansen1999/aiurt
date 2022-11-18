package com.aiurt.modules.system.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 菜单权限表
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SysPermission", description="菜单实体")
public class SysPermission implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	/**
	 * 父id
	 */
	@ApiModelProperty("父id")
	private String parentId;

	/**
	 * 菜单名称
	 */
	@ApiModelProperty("名称")
	private String name;

	/**
	 * 菜单权限编码，例如：“sys:schedule:list,sys:schedule:info”,多个逗号隔开
	 */
	@ApiModelProperty("菜单权限编码")
	private String perms;
	/**
	 * 权限策略1显示2禁用
	 */
	@ApiModelProperty("权限策略1显示2禁用")
	private String permsType;

	/**
	 * 菜单图标
	 */
	@ApiModelProperty("菜单图标")
	private String icon;

	/**
	 * 组件
	 */
	@ApiModelProperty("组件")
	private String component;

	/**
	 * 组件名字
	 */
	@ApiModelProperty("组件名字")
	private String componentName;

	/**
	 * 路径
	 */
	@ApiModelProperty("路径")
	private String url;
	/**
	 * 一级菜单跳转地址
	 */
	@ApiModelProperty("跳转地址")
	private String redirect;

	/**
	 * 菜单排序
	 */
	@ApiModelProperty("菜单排序")
	private Double sortNo;

	/**
	 * 类型（0：一级菜单；1：子菜单 ；2：按钮权限）
	 */
	@ApiModelProperty("类型（0：模块；1：子菜单 ；2：按钮权限）")
	@Dict(dicCode = "menu_type")
	private Integer menuType;

	/**
	 * 是否叶子节点: 1:是  0:不是
	 */
	@ApiModelProperty("是否叶子节点: true:是  false:不是")
	@TableField(value="is_leaf")
	private boolean leaf;

	/**
	 * 是否路由菜单: 0:不是  1:是（默认值1）
	 */
	@ApiModelProperty("是否路由菜单: false:不是  true:是（默认值true）")
	@TableField(value="is_route")
	private boolean route;
	/**
	 * 是否导航栏: 0:不是  1:是
	 */
	@ApiModelProperty("是否导航栏: false:不是  true:是")
	@TableField(value="is_navBar")
	private boolean isNavBar ;
	/**
	 * 是否筛选器: 0:不是  1:是
	 */
	@ApiModelProperty("是否筛选器: false:不是  true:是")
	@TableField(value="is_filter")
	private boolean isFilter ;
	/**
	 * 是否快捷搜索: 0:不是  1:是
	 */
	@ApiModelProperty("是否快捷搜索: false:不是  true:是")
	@TableField(value="is_search")
	private boolean isSearch ;


	/**
	 * 是否缓存页面: 0:不是  1:是（默认值1）
	 */
	@ApiModelProperty("是否缓存页面: false:不是  true:是（默认值true）")
	@TableField(value="keep_alive")
	private boolean keepAlive;

	/**
	 * 描述
	 */
	@ApiModelProperty("描述")
	private String description;

	/**
	 * 创建人
	 */
	private String createBy;

	/**
	 * 删除状态 0正常 1已删除
	 */
	@ApiModelProperty("删除状态 0正常 1已删除")
	private Integer delFlag;

	/**
	 * 是否配置菜单的数据权限 1是0否 默认0
	 */
	@ApiModelProperty("是否配置菜单的数据权限 1是0否 默认0")
	private Integer ruleFlag;

	/**
	 * 是否隐藏路由菜单: 0否,1是（默认值0）
	 */
	@ApiModelProperty("是否停用 false否,true是")
	private boolean hidden;

	/**
	 * 是否隐藏Tab: 0否,1是（默认值0）
	 */
	@ApiModelProperty("是否隐藏Tab: false否,true是（默认值false）")
	private boolean hideTab;

	/**
	 * 是否为手机app(1是,0否)
	 */
	@ApiModelProperty("是否为移动端(1是,0否)")
	private Integer isApp;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新人
	 */
	private String updateBy;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**按钮权限状态(0无效1有效)*/
	@ApiModelProperty("按钮权限状态(0无效1有效)")
	private java.lang.String status;

	/**alwaysShow*/
    private boolean alwaysShow;

	/*update_begin author:wuxianquan date:20190908 for:实体增加字段 */
    /** 外链菜单打开方式 0/内部打开 1/外部打开 */
    private boolean internalOrExternal;
	/*update_end author:wuxianquan date:20190908 for:实体增加字段 */

    public SysPermission() {

    }
    public SysPermission(boolean index) {
    	if(index) {
    		this.id = "9502685863ab87f0ad1134142788a385";
        	this.name="首页";
        	this.component="dashboard/Analysis";
        	this.componentName="dashboard-analysis";
        	this.url="/dashboard/analysis";
        	this.icon="home";
        	this.menuType=0;
        	this.sortNo=0.0;
        	this.ruleFlag=0;
        	this.delFlag=0;
        	this.alwaysShow=false;
        	this.route=true;
        	this.keepAlive=true;
        	this.leaf=true;
        	this.hidden=false;
        	this.isSearch=false;
        	this.isFilter=false;
        	this.isNavBar=false;
    	}

    }
}
