package com.aiurt.modules.online.workflowapi.mapper;

import com.aiurt.modules.online.workflowapi.entity.ActCustomInterfaceModule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.SelectTreeModel;


import java.util.List;
import java.util.Map;

/**
 * @Description: act_custom_interface_module
 * @Author: jeecg-boot
 * @Date:   2023-08-14
 * @Version: V1.0
 */
public interface ActCustomInterfaceModuleMapper extends BaseMapper<ActCustomInterfaceModule> {

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

}
