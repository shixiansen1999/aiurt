package com.aiurt.modules.modeler.service;

import com.aiurt.modules.modeler.entity.ActCustomClassify;
import org.jeecg.common.system.vo.SelectTreeModel;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;

/**
 * @Description: 流程分类
 * @Author: aiurt
 * @Date:   2022-07-21
 * @Version: V1.0
 */
public interface IActCustomClassifyService extends IService<ActCustomClassify> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";

	/**树节点有子节点状态值*/
	public static final String HASCHILD = "1";

	/**树节点无子节点状态值*/
	public static final String NOCHILD = "0";

	/**
	 * 新增节点
	 *
	 * @param actCustomClassify
	 */
	void addActCustomClassify(ActCustomClassify actCustomClassify);

	/**
   * 修改节点
   *
   * @param actCustomClassify
   * @throws AiurtBootException
   */
	void updateActCustomClassify(ActCustomClassify actCustomClassify) throws AiurtBootException;

	/**
	 * 删除节点
	 *
	 * @param id
   * @throws AiurtBootException
	 */
	void deleteActCustomClassify(String id) throws AiurtBootException;

	  /**
	   * 查询所有数据，无分页
	   *
	   * @param queryWrapper
	   * @return List<ActCustomClassify>
	   */
    List<ActCustomClassify> queryTreeListNoPage(QueryWrapper<ActCustomClassify> queryWrapper);

	/**
	 * 【vue3专用】根据父级编码加载分类字典的数据
	 *
	 * @param parentCode
	 * @return
	 */
	List<SelectTreeModel> queryListByCode(String parentCode);

	/**
	 * 【vue3专用】根据pid查询子节点集合
	 *
	 * @param pid
	 * @return
	 */
	List<SelectTreeModel> queryListByPid(String pid);

}
