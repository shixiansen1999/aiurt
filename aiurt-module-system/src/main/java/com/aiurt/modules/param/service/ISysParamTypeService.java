package com.aiurt.modules.param.service;

import com.aiurt.modules.param.entity.SysParamType;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.system.vo.SelectTreeModel;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;

/**
 * @Description: 系统参数分类树
 * @Author: aiurt
 * @Date:   2023-01-04
 * @Version: V1.0
 */
public interface ISysParamTypeService extends IService<SysParamType> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";

	/**树节点有子节点状态值*/
	public static final String HASCHILD = "1";

	/**树节点无子节点状态值*/
	public static final String NOCHILD = "0";

	/**
	 * 新增节点
	 *
	 * @param sysParamType
	 */
	void addSysParamType(SysParamType sysParamType);

	/**
   * 修改节点
   *
   * @param sysParamType
   * @throws AiurtBootException
   */
	void updateSysParamType(SysParamType sysParamType) throws AiurtBootException;

	/**
	 * 删除节点
	 *
	 * @param id
   * @throws AiurtBootException
	 */
	void deleteSysParamType(String id) throws AiurtBootException;





}
