package com.aiurt.modules.sm.mapper;

import com.aiurt.common.api.vo.TreeNode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.SelectTreeModel;
import com.aiurt.modules.sm.entity.CsSafetyAttentionType;

import java.util.List;
import java.util.Map;

/**
 * @Description: 安全事项类型表
 * @Author: aiurt
 * @Date:   2022-11-17
 * @Version: V1.0
 */
public interface CsSafetyAttentionTypeMapper extends BaseMapper<CsSafetyAttentionType> {

	/**
	 * 编辑节点状态
	 * @param id
	 * @param status
	 */
	void updateTreeNodeStatus(@Param("id") String id,@Param("status") String status);

	/**
	 * 【vue3专用】根据父级ID查询树节点数据
	 *
	 * @param pid
	 * @param query
	 * @return
	 */
	List<SelectTreeModel> queryListByPid(@Param("pid") String pid, @Param("query") Map<String, String> query);

	/**
	 * 查询所有的安全事项类型
	 * @return
	 */
    List<TreeNode> queryTreeList();

	/**
	 * 查询所有的专业信息
	 * @return
	 */
	List<TreeNode> queryAllMajor(String majorCode);

	/**
	 * 根据专业查询节点
	 * @param majorCode
	 * @return
	 */
	List<TreeNode> queryTreeByMajorCode(@Param("majorCode") String majorCode);
}
