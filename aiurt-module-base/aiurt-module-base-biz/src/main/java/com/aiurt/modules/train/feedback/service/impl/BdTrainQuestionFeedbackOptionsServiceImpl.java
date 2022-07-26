package com.aiurt.modules.train.feedback.service.impl;

import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedbackOptions;
import com.aiurt.modules.train.feedback.mapper.BdTrainQuestionFeedbackOptionsMapper;
import com.aiurt.modules.train.feedback.service.IBdTrainQuestionFeedbackOptionsService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 问题反馈单选项
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
@Service
public class BdTrainQuestionFeedbackOptionsServiceImpl extends ServiceImpl<BdTrainQuestionFeedbackOptionsMapper, BdTrainQuestionFeedbackOptions> implements IBdTrainQuestionFeedbackOptionsService {
	
	@Autowired
	private BdTrainQuestionFeedbackOptionsMapper bdTrainQuestionFeedbackOptionsMapper;
	
	@Override
	public List<BdTrainQuestionFeedbackOptions> selectByMainId(String mainId) {
		return bdTrainQuestionFeedbackOptionsMapper.selectByMainId(mainId);
	}
}
