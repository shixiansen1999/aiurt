package com.aiurt.modules.train.feedback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedbackQues;

import java.util.List;

/**
 * @Description: 问题反馈问题列表
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
public interface BdTrainQuestionFeedbackQuesMapper extends BaseMapper<BdTrainQuestionFeedbackQues> {

	/**
	 * @param mainId
	 * @return
	 */
	public boolean deleteByMainId(@Param("mainId") String mainId);
	/**
	 * @param mainId
	 * @return
	 */
	public List<BdTrainQuestionFeedbackQues> selectByMainId(@Param("mainId") String mainId);
	/**
	 * @param trainQuestionFeedbackQuesId
	 * @return
	 */
	List<BdTrainQuestionFeedbackQues> openQuestion(String trainQuestionFeedbackQuesId);

}
