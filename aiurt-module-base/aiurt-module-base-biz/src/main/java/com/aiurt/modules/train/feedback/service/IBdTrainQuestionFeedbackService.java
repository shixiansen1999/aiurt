package com.aiurt.modules.train.feedback.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedbackQues;
import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedbackOptions;
import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedback;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.modules.train.feedback.vo.BdTrainQuestionFeedbackPage;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 问题反馈主表
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
public interface IBdTrainQuestionFeedbackService extends IService<BdTrainQuestionFeedback> {

	/**
	 * 添加一对多
	 * @param bdTrainQuestionFeedback
	 * @param bdTrainQuestionFeedbackQuesList
	 * @param bdTrainQuestionFeedbackOptionsList
	 * @return
	 */
	public void saveMain(BdTrainQuestionFeedback bdTrainQuestionFeedback,List<BdTrainQuestionFeedbackQues> bdTrainQuestionFeedbackQuesList,List<BdTrainQuestionFeedbackOptions> bdTrainQuestionFeedbackOptionsList) ;

	/**
	 * 修改一对多
	 * @param bdTrainQuestionFeedback
	 * @param bdTrainQuestionFeedbackQuesList
	 * @param bdTrainQuestionFeedbackOptionsList
	 * @return
	 */
	public void updateMain(BdTrainQuestionFeedback bdTrainQuestionFeedback,List<BdTrainQuestionFeedbackQues> bdTrainQuestionFeedbackQuesList,List<BdTrainQuestionFeedbackOptions> bdTrainQuestionFeedbackOptionsList);

	/**
	 * 删除一对多
	 * @param id
	 */
	public void delMain (String id);

	/**
	 * 批量删除一对多
	 * @param idList
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);
	/**
	 * 查询
	 * @return
	 */
	Page<BdTrainQuestionFeedback> queryPageList(Page<BdTrainQuestionFeedback> page, BdTrainQuestionFeedback bdTrainQuestionFeedback);

	/**
	 * 添加
	 * @param bdTrainQuestionFeedback
	 * @param bdTrainQuestionFeedbackPage
	 * @return
	 */
	void add( BdTrainQuestionFeedback  bdTrainQuestionFeedback,BdTrainQuestionFeedbackPage bdTrainQuestionFeedbackPage);
	/**
	 * 启用
	 * @return
	 * @param bdTrainQuestionFeedback
	 */
	void enable(BdTrainQuestionFeedback bdTrainQuestionFeedback);
	/**
	 * 问题反馈主表-查看
	 * @return
	 * @param id
	 */
	BdTrainQuestionFeedbackPage queryByFeedbackPage(String id);

}
