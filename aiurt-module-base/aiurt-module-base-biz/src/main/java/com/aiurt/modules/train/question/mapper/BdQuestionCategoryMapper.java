package com.aiurt.modules.train.question.mapper;

import com.aiurt.common.api.vo.TreeNode;
import com.aiurt.modules.train.question.entity.BdQuestionCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: bd_question_category
 * @Author: jeecg-boot
 * @Date:   2022-04-15
 * @Version: V1.0
 */
public interface BdQuestionCategoryMapper extends BaseMapper<BdQuestionCategory> {

	/**
	 * 编辑节点状态
	 * @param id
	 * @param status
	 */
	void updateTreeNodeStatus(@Param("id") String id,@Param("status") String status);

	/**
	 * 树code
	 * @return
	 */
    List<TreeNode> queryPageList();

}
