package com.aiurt.boot.modules.system.service;

import com.aiurt.boot.modules.system.entity.SysCategory;
import com.aiurt.boot.modules.system.model.TreeSelectModel;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: 分类字典
 * @Author: swsc
 * @Date:   2019-05-29
 * @Version: V1.0
 */
public interface ISysCategoryService extends IService<SysCategory> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";

	void addSysCategory(SysCategory sysCategory);

	void updateSysCategory(SysCategory sysCategory);

	/**
	  * 根据父级编码加载分类字典的数据
	 * @param pcode
	 * @return
	 */
	public List<TreeSelectModel> queryListByCode(String pcode) throws AiurtBootException;

	/**
	  * 根据pid查询子节点集合
	 * @param pid
	 * @return
	 */
	public List<TreeSelectModel> queryListByPid(String pid);

	/**
	 * 根据pid查询子节点集合,支持查询条件
	 * @param pid
	 * @param condition
	 * @return
	 */
	public List<TreeSelectModel> queryListByPid(String pid, Map<String,String> condition);

	/**
	 * 根据code查询id
	 * @param code
	 * @return
	 */
	public String queryIdByCode(String code);

}
