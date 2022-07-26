package com.aiurt.modules.train.task.service;

import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedback;
import com.aiurt.modules.train.task.dto.StudentFeedbackRecordDTO;
import com.aiurt.modules.train.task.entity.BdTrainStudentFeedbackRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: 学员反馈记录
 * @Author: jeecg-boot
 * @Date:   2022-04-20
 * @Version: V1.0
 */
public interface IBdTrainStudentFeedbackRecordService extends IService<BdTrainStudentFeedbackRecord> {

    /**
     *获取学员反馈表app
     * @param userId
     * @param taskId
     */
    BdTrainQuestionFeedback getStudentFeedbackRecordById(String userId, String taskId);

    /**
     *获取学员反馈表web
     * @param userId
     * @param taskId
     */
    List<StudentFeedbackRecordDTO> getStudentFeedbackRecord(String userId,String taskId);

}
