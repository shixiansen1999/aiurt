package com.aiurt.modules.system.dto;

import com.aiurt.modules.system.entity.SysPermission;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: 菜单树，封装树结构
 * @author: jeecg-boot
 */
public class SysPermissionTreeDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private String id;

	private String key;
	private String title;

	private String value;

	private String label;

	private String color;


	/**
	 * 父id
	 */
	@ApiModelProperty("父id")
	private String parentId;

	/**
	 * 菜单名称
	 */
	@ApiModelProperty("菜单名称")
	private String name;

	/**
	 *
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
	 * 跳转网页链接
	 */
	@ApiModelProperty("跳转网页链接")
	private String url;

	/**
	 * 一级菜单跳转地址
	 */
	@ApiModelProperty("一级菜单跳转地址")
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
	private Integer menuType;

	/**
	 * 是否叶子节点: 1:是 0:不是
	 */
	@ApiModelProperty("是否叶子节点: true:是 false:不是")
	private boolean isLeaf;

	/**
	 * 是否路由菜单: 0:不是  1:是（默认值1）
	 */
	@ApiModelProperty("是否路由菜单: false:不是  true:是（默认值true）")
	private boolean route;


	/**
	 * 是否路缓存页面: 0:不是  1:是（默认值1）
	 */
	@ApiModelProperty("是否路缓存页面: false:不是  true:是（默认值true）")
	private boolean keepAlive;

	/**
	 * 是否导航栏: 0:不是  1:是
	 */
	@ApiModelProperty("是否导航栏: false:不是  true:是")
	private boolean isNavBar ;
	/**
	 * 是否筛选器: 0:不是  1:是
	 */
	@ApiModelProperty("是否筛选器: false:不是  true:是")
	private boolean isFilter ;
	/**
	 * 是否快捷搜索: 0:不是  1:是
	 */
	@ApiModelProperty("是否快捷搜索: false:不是  true:是")
	private boolean isSearch ;

	/**
	 * 描述
	 */
	@ApiModelProperty("描述")
	private String description;

	/**
	 * 删除状态 0正常 1已删除
	 */
	@ApiModelProperty("删除状态 0正常 1已删除")
	private Integer delFlag;

	/**
	 * 创建人
	 */
	private String createBy;

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

	/**alwaysShow*/
    private boolean alwaysShow;
    /**是否隐藏路由菜单: 0否,1是（默认值0）*/
	@ApiModelProperty("是否启用菜单: false否,true是")
    private boolean hidden;

    /**按钮权限状态(0无效1有效)*/
	@ApiModelProperty("按钮权限状态(0无效1有效)")
	private String status;

	/*update_begin author:wuxianquan date:20190908 for:model增加字段 */
	/** 外链菜单打开方式 0/内部打开 1/外部打开 */
	@ApiModelProperty("外链菜单打开方式 0/内部打开 1/外部打开")
	private boolean internalOrExternal;
	/*update_end author:wuxianquan date:20190908 for:model增加字段 */


	public SysPermissionTreeDTO() {
	}

	public SysPermissionTreeDTO(SysPermission permission) {
		this.key = permission.getId();
		this.value = permission.getId();
		this.label = permission.getName();
		this.id = permission.getId();
		this.perms = permission.getPerms();
		this.permsType = permission.getPermsType();
		this.component = permission.getComponent();
		this.createBy = permission.getCreateBy();
		this.createTime = permission.getCreateTime();
		this.delFlag = permission.getDelFlag();
		this.description = permission.getDescription();
		this.icon = permission.getIcon();
		this.isLeaf = permission.isLeaf();
		this.menuType = permission.getMenuType();
		this.name = permission.getName();
		this.parentId = permission.getParentId();
		this.sortNo = permission.getSortNo();
		this.updateBy = permission.getUpdateBy();
		this.updateTime = permission.getUpdateTime();
		this.redirect = permission.getRedirect();
		this.url = permission.getUrl();
		this.hidden = permission.isHidden();
		this.isSearch = permission.isSearch();
		this.isFilter = permission.isFilter();
		this.isNavBar = permission.isNavBar();
		this.route = permission.isRoute();
		this.keepAlive = permission.isKeepAlive();
		this.alwaysShow= permission.isAlwaysShow();
		/*update_begin author:wuxianquan date:20190908 for:赋值 */
		this.internalOrExternal = permission.isInternalOrExternal();
		/*update_end author:wuxianquan date:20190908 for:赋值 */
		this.title=permission.getName();
		if (!permission.isLeaf()) {
			this.children = new ArrayList<SysPermissionTreeDTO>();
		}
		this.status = permission.getStatus();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	private List<SysPermissionTreeDTO> children;

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean leaf) {
		isLeaf = leaf;
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	public boolean isAlwaysShow() {
		return alwaysShow;
	}

	public void setAlwaysShow(boolean alwaysShow) {
		this.alwaysShow = alwaysShow;
	}
	public List<SysPermissionTreeDTO> getChildren() {
		return children;
	}

	public void setChildren(List<SysPermissionTreeDTO> children) {
		this.children = children;
	}

	public String getRedirect() {
		return redirect;
	}

	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isNavBar() {
		return isNavBar;
	}

	public void setNavBar(boolean navBar) {
		isNavBar = navBar;
	}

	public boolean isFilter() {
		return isFilter;
	}

	public void setFilter(boolean filter) {
		isFilter = filter;
	}

	public boolean isSearch() {
		return isSearch;
	}

	public void setSearch(boolean search) {
		isSearch = search;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Double getSortNo() {
		return sortNo;
	}

	public void setSortNo(Double sortNo) {
		this.sortNo = sortNo;
	}

	public Integer getMenuType() {
		return menuType;
	}

	public void setMenuType(Integer menuType) {
		this.menuType = menuType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isRoute() {
		return route;
	}

	public void setRoute(boolean route) {
		this.route = route;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPerms() {
		return perms;
	}

	public void setPerms(String perms) {
		this.perms = perms;
	}

	public boolean getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public String getPermsType() {
		return permsType;
	}

	public void setPermsType(String permsType) {
		this.permsType = permsType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isInternalOrExternal() {
		return internalOrExternal;
	}

	public void setInternalOrExternal(boolean internalOrExternal) {
		this.internalOrExternal = internalOrExternal;
	}
	/*update_end author:wuxianquan date:20190908 for:get set 方法 */
}
