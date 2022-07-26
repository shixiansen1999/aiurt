package com.aiurt.modules.train.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedback;
import com.aiurt.modules.train.task.entity.BdTrainTeacherFeedbackRecord;

import java.util.List;

/**
 * @Description: 讲师反馈记录表
 * @Author: jeecg-boot
 * @Date:   2022-04-20
 * @Version: V1.0
 */
public interface BdTrainTeacherFeedbackRecordMapper extends BaseMapper<BdTrainTeacherFeedbackRecord> {
    /**
     * 获取讲师反馈表
     * @param userId
     * @param taskId
     */
    List<BdTrainTeacherFeedbackRecord> getTeacherFeedbackRecord(@Param("userId") String userId, @Param("taskId") String taskId);

    /**
     * 获取问题反馈主表id
     * */
    BdTrainQuestionFeedback getBdTrainQuestionFeedbackId();

    /**
     * 根据任务id和用户id,筛选讲师反馈表,单选题
     * @param userId
     * @param taskId
     * @return
     */
    List<BdTrainTeacherFeedbackRecord> getChoiceTeacherFeedbackRecordEvaluate(@Param("userId") String userId, @Param("taskId") String taskId);

    /**
     * 根据任务id和用户id,,筛选讲师反馈表,简答题
     * @param userId
     * @param taskId
     * @return
     */
    List<BdTrainTeacherFeedbackRecord> getAnswerTeacherFeedbackRecordEvaluate(@Param("userId") String userId, @Param("taskId") String taskId);

    BdTrainQuestionFeedback getFeedbackId(String taskId);
}
