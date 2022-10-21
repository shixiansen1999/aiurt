package com.aiurt.modules.train.feedback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedbackOptionsRecord;

import java.util.List;

/**
 * @Description: bd_train_question_feedback_options_record
 * @Author: jeecg-boot
 * @Date:   2022-05-23
 * @Version: V1.0
 */
public interface BdTrainQuestionFeedbackOptionsRecordMapper extends BaseMapper<BdTrainQuestionFeedbackOptionsRecord> {

     /**
      * 查询
      * @param id
      * @return
      */
     List<BdTrainQuestionFeedbackOptionsRecord> selectByMainId(String id);
}
