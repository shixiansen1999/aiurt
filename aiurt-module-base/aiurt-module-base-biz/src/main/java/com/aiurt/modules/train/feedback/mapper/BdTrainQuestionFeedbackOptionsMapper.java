package com.aiurt.modules.train.feedback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedbackOptions;

import java.util.List;

/**
 * @Description: 问题反馈单选项
 * @Author: jeecg-boot
 * @Date:   2022-04-18
 * @Version: V1.0
 */
public interface BdTrainQuestionFeedbackOptionsMapper extends BaseMapper<BdTrainQuestionFeedbackOptions> {
	/**
	 * 根据id批量删除
	 * @param mainId
	 * @return
	 */
	public boolean deleteByMainId(@Param("mainId") String mainId);
	/**
	 * 查
	 * @param mainId
	 * @return
	 */
	public List<BdTrainQuestionFeedbackOptions> selectByMainId(@Param("mainId") String mainId);
	/**
	 *查询
	 * @param bdTrainQuestionFeedbackOptionsId
	 * @return
	 */
	List<BdTrainQuestionFeedbackOptions> openOption(@Param("bdTrainQuestionFeedbackOptionsId")String bdTrainQuestionFeedbackOptionsId);
}
