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
	 * 获取树形结构的模块数据
	 *
	 * @param name
	 * @return
	 */
	List<SelectTreeModel> getModuleTree(@Param("name") String name);

}
