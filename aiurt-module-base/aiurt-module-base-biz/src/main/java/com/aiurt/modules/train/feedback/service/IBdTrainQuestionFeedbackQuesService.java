package com.aiurt.modules.train.feedback.service;

import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedbackQues;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 问题反馈问题列表
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
public interface IBdTrainQuestionFeedbackQuesService extends IService<BdTrainQuestionFeedbackQues> {
	/**
	 * 查询
	 * @param mainId
	 * @return
	 */
	public List<BdTrainQuestionFeedbackQues> selectByMainId(String mainId);
}
