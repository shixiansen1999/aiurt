package com.aiurt.modules.sm.service;

import com.aiurt.modules.sm.dto.SafetyAttentionTypeTreeDTO;
import org.jeecg.common.system.vo.SelectTreeModel;
import com.aiurt.modules.sm.entity.CsSafetyAttentionType;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;

/**
 * @Description: 安全事项类型表
 * @Author: aiurt
 * @Date:   2022-11-17
 * @Version: V1.0
 */
public interface ICsSafetyAttentionTypeService extends IService<CsSafetyAttentionType> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";

	/**树节点有子节点状态值*/
	public static final String HASCHILD = "1";

	/**树节点无子节点状态值*/
	public static final String NOCHILD = "0";

	/**
	 * 新增节点
	 *
	 * @param csSafetyAttentionType
	 */
	void addCsSafetyAttentionType(CsSafetyAttentionType csSafetyAttentionType);

	/**
   * 修改节点
   *
   * @param csSafetyAttentionType
   * @throws AiurtBootException
   */
	void updateCsSafetyAttentionType(CsSafetyAttentionType csSafetyAttentionType) throws AiurtBootException;

	/**
	 * 删除节点
	 *
	 * @param id
   * @throws AiurtBootException
	 */
	void deleteCsSafetyAttentionType(String id) throws AiurtBootException;

	  /**
	   * 查询所有数据，无分页
	   *
	   * @param queryWrapper
	   * @return List<CsSafetyAttentionType>
	   */
    List<CsSafetyAttentionType> queryTreeListNoPage(QueryWrapper<CsSafetyAttentionType> queryWrapper);

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

	/**
	 * 查询数据 查出所有安全事项类型,并以树结构数据格式响应给前端
	 * @return
	 */
    List<SelectTreeModel> queryTreeList();

}
