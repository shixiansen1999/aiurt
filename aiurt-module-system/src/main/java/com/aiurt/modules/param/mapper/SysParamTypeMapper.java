package com.aiurt.modules.param.mapper;

import com.aiurt.modules.param.entity.SysParamType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.SelectTreeModel;

import java.util.List;
import java.util.Map;

/**
 * @Description: 系统参数分类树
 * @Author: aiurt
 * @Date:   2023-01-04
 * @Version: V1.0
 */
public interface SysParamTypeMapper extends BaseMapper<SysParamType> {

	/**
	 * 编辑节点状态
	 * @param id
	 * @param status
	 */
	void updateTreeNodeStatus(@Param("id") Integer id,@Param("status") String status);

	/**
	 * 【vue3专用】根据父级ID查询树节点数据
	 *
	 * @param pid
	 * @param query
	 * @return
	 */
	List<SelectTreeModel> queryListByPid(@Param("pid") String pid, @Param("query") Map<String, String> query);

}
