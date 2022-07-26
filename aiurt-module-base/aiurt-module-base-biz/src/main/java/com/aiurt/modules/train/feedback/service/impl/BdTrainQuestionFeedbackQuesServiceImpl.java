package com.aiurt.modules.train.feedback.service.impl;

import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedbackQues;
import com.aiurt.modules.train.feedback.mapper.BdTrainQuestionFeedbackQuesMapper;
import com.aiurt.modules.train.feedback.service.IBdTrainQuestionFeedbackQuesService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 问题反馈问题列表
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Service
public class BdTrainQuestionFeedbackQuesServiceImpl extends ServiceImpl<BdTrainQuestionFeedbackQuesMapper, BdTrainQuestionFeedbackQues> implements IBdTrainQuestionFeedbackQuesService {
	
	@Autowired
	private BdTrainQuestionFeedbackQuesMapper bdTrainQuestionFeedbackQuesMapper;
	
	@Override
	public List<BdTrainQuestionFeedbackQues> selectByMainId(String mainId) {
		return bdTrainQuestionFeedbackQuesMapper.selectByMainId(mainId);
	}
}
