package com.aiurt.modules.train.task.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedback;
import com.aiurt.modules.train.task.entity.BdTrainTeacherFeedbackRecord;

/**
 * @Description: 讲师反馈记录表
 * @Author: jeecg-boot
 * @Date:   2022-04-20
 * @Version: V1.0
 */
public interface IBdTrainTeacherFeedbackRecordService extends IService<BdTrainTeacherFeedbackRecord> {
    /**
     *获取讲师评估
     * @param userId
     * @param taskId
     */
    BdTrainQuestionFeedback getTeacherFeedbackRecord(String userId, String taskId);


    /**
     * 讲师（已关闭）查看评估
     * @param userId
     * @param taskId
     * @return
     */
    BdTrainTeacherFeedbackRecord queryTeacherTaskFeedbackEvaluate(String userId, String taskId);
}
