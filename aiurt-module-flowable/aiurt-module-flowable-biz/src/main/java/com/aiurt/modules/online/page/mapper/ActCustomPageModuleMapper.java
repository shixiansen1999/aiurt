package com.aiurt.modules.online.page.mapper;



import com.aiurt.modules.online.page.entity.ActCustomPageModule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.SelectTreeModel;

import java.util.List;
import java.util.Map;

/**
 * @Description: act_custom_page_module
 * @Author: jeecg-boot
 * @Date:   2023-08-18
 * @Version: V1.0
 */
public interface ActCustomPageModuleMapper extends BaseMapper<ActCustomPageModule> {

	/**
	 * 编辑节点状态
	 * @param id
	 * @param status
	 */
	void updateTreeNodeStatus(@Param("id") String id, @Param("status") String status);

	/**
	 * 【vue3专用】根据父级ID查询树节点数据
	 *
	 * @param pid
	 * @param query
	 * @return
	 */
	List<SelectTreeModel> queryListByPid(@Param("pid") String pid, @Param("query") Map<String, String> query);

	/**
	 * 获取树形结构的模块数据
	 *
	 * @param name
	 * @return
	 */
	List<SelectTreeModel> getModuleTree(@Param("name") String name);

}
