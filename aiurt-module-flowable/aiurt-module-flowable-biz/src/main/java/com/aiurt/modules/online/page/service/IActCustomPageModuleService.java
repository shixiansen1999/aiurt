package com.aiurt.modules.online.page.service;

import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.online.page.entity.ActCustomPageModule;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.SelectTreeModel;

import java.util.List;

/**
 * @Description: act_custom_page_module
 * @Author: jeecg-boot
 * @Date:   2023-08-18
 * @Version: V1.0
 */
public interface IActCustomPageModuleService extends IService<ActCustomPageModule> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";

	/**树节点有子节点状态值*/
	public static final String HASCHILD = "1";

	/**树节点无子节点状态值*/
	public static final String NOCHILD = "0";

	/**
	 * 新增节点
	 *
	 * @param actCustomPageModule
	 */
	void addActCustomPageModule(ActCustomPageModule actCustomPageModule);

	/**
   * 修改节点
   *
   * @param actCustomPageModule
   * @throws AiurtBootException
   */
	void updateActCustomPageModule(ActCustomPageModule actCustomPageModule) throws AiurtBootException;

	/**
	 * 删除节点
	 *
	 * @param id
   * @throws AiurtBootException
	 */
	void deleteActCustomPageModule(String id) throws AiurtBootException;

	  /**
	   * 查询所有数据，无分页
	   *
	   * @param queryWrapper
	   * @return List<ActCustomPageModule>
	   */
    List<ActCustomPageModule> queryTreeListNoPage(QueryWrapper<ActCustomPageModule> queryWrapper);

	/**
	 * 【vue3专用】根据父级编码加载分类字典的数据
	 *
	 * @param parentId
	 * @return
	 */
	List<SelectTreeModel> queryListByCode(String parentId);

	/**
	 * 【vue3专用】根据pid查询子节点集合
	 *
	 * @param pid
	 * @return
	 */
	List<SelectTreeModel> queryListByPid(String pid);

	/**
	 * 获取树形结构的模块数据
	 *
	 * @param name 模块名称
	 * @return 树形结构的模块数据列表
	 */
	List<SelectTreeModel> getModuleTree(String name);

}
