package com.aiurt.modules.train.feedback.service;

import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedbackOptions;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 问题反馈单选项
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
public interface IBdTrainQuestionFeedbackOptionsService extends IService<BdTrainQuestionFeedbackOptions> {
	/**
	 * cha
	 * @param mainId
	 * @return
	 */
	public List<BdTrainQuestionFeedbackOptions> selectByMainId(String mainId);
}
