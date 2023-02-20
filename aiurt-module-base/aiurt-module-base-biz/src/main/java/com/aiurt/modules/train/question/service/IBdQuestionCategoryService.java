package com.aiurt.modules.train.question.service;

import com.aiurt.common.api.vo.TreeNode;
import com.aiurt.modules.train.question.entity.BdQuestionCategory;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;

import java.util.List;

/**
 * @Description: bd_question_category
 * @Author: jeecg-boot
 * @Date:   2022-04-15
 * @Version: V1.0
 */
public interface IBdQuestionCategoryService extends IService<BdQuestionCategory> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";

	/**树节点有子节点状态值*/
	public static final String HASCHILD = "1";

	/**树节点无子节点状态值*/
	public static final String NOCHILD = "0";


	/**
	 * 新增节点
	 * @param bdQuestionCategory
	 */
	void addBdQuestionCategory(BdQuestionCategory bdQuestionCategory);


	/**
	 * 修改节点
	 * @param bdQuestionCategory
	 * @throws JeecgBootException
	 */
	void updateBdQuestionCategory(BdQuestionCategory bdQuestionCategory) throws JeecgBootException;


	/**
	 * 删除节点
	 * @param id
	 * @throws JeecgBootException
	 */
	void deleteBdQuestionCategory(String id) throws JeecgBootException;


	/**
	 * 查询所有数据，无分页
	 * @param queryWrapper
	 * @return
	 */
    List<BdQuestionCategory> queryTreeListNoPage(QueryWrapper<BdQuestionCategory> queryWrapper);

	/**
	 * 查询习题类别树
	 * @return
	 */
	List<TreeNode> queryPageList(String name);

}
