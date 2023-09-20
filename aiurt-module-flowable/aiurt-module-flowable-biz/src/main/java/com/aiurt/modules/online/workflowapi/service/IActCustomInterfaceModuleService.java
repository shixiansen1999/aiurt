package com.aiurt.modules.online.workflowapi.service;

import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.online.workflowapi.entity.ActCustomInterfaceModule;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.system.vo.SelectTreeModel;


import java.util.List;

/**
 * @Description: act_custom_interface_module
 * @Author: jeecg-boot
 * @Date:   2023-08-14
 * @Version: V1.0
 */
public interface IActCustomInterfaceModuleService extends IService<ActCustomInterfaceModule> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";

	/**树节点有子节点状态值*/
	public static final String HASCHILD = "1";

	/**树节点无子节点状态值*/
	public static final String NOCHILD = "0";

	/**
	 * 新增节点
	 *
	 * @param actCustomInterfaceModule
	 */
	void addActCustomInterfaceModule(ActCustomInterfaceModule actCustomInterfaceModule);

	/**
   * 修改节点
   *
   * @param actCustomInterfaceModule
   * @throws AiurtBootException
   */
	void updateActCustomInterfaceModule(ActCustomInterfaceModule actCustomInterfaceModule) throws AiurtBootException;

	/**
	 * 删除节点
	 *
	 * @param id
	 */
	void deleteActCustomInterfaceModule(String id) throws AiurtBootException;

	  /**
	   * 查询所有数据，无分页
	   *
	   * @param queryWrapper
	   * @return List<ActCustomInterfaceModule>
	   */
    List<ActCustomInterfaceModule> queryTreeListNoPage(QueryWrapper<ActCustomInterfaceModule> queryWrapper);

	/**
	 * 获取树形结构的模块数据
	 *
	 * @param name 模块名称
	 * @return 树形结构的模块数据列表
	 */
	List<SelectTreeModel> getModuleTree(String name);


}
