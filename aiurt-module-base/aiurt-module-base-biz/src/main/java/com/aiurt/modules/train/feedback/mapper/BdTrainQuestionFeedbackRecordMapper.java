package com.aiurt.modules.train.feedback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedbackRecord;

/**
 * @Description: bd_train_question_feedback_record
 * @Author: jeecg-boot
 * @Date:   2022-05-23
 * @Version: V1.0
 */
public interface BdTrainQuestionFeedbackRecordMapper extends BaseMapper<BdTrainQuestionFeedbackRecord> {

    /**
     * 获取学生反馈副表
     * @param taskId
     * @return
     */
    BdTrainQuestionFeedbackRecord getTeacherFeedBack(String taskId);

    BdTrainQuestionFeedbackRecord getStudentFeedBack(String taskId);
}
