package com.aiurt.modules.train.feedback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedbackQuesRecord;

import java.util.List;

/**
 * @Description: bd_train_question_feedback_ques_record
 * @Author: jeecg-boot
 * @Date:   2022-05-23
 * @Version: V1.0
 */
public interface BdTrainQuestionFeedbackQuesRecordMapper extends BaseMapper<BdTrainQuestionFeedbackQuesRecord> {

     /**
      * 查询
      * @param id
      * @return
      */
     List<BdTrainQuestionFeedbackQuesRecord> selectByMainId(String id);
}
