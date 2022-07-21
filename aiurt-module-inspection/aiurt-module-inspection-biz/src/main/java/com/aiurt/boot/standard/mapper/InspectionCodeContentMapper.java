package com.aiurt.boot.standard.mapper;

import com.aiurt.boot.standard.entity.InspectionCodeContent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.SelectTreeModel;

import java.util.List;
import java.util.Map;

/**
 * @Description: inspection_code_content
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface InspectionCodeContentMapper extends BaseMapper<InspectionCodeContent> {

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
	 * 树型分页查询
	 * @param inspectionCodeContent
	 * @return
	 */
    List<InspectionCodeContent> selectLists(@Param("inspectionCodeContent") InspectionCodeContent inspectionCodeContent);

	/**
	 * 逻辑删除父节点下面的子节点
	 * @param id
	 */
    void updatePid(String id);
}
