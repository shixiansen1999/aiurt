package com.aiurt.modules.train.task.mapper;

import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedback;
import com.aiurt.modules.train.task.entity.BdTrainStudentFeedbackRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 学员反馈记录
 * @Author: jeecg-boot
 * @Date:   2022-04-20
 * @Version: V1.0
 */
public interface BdTrainStudentFeedbackRecordMapper extends BaseMapper<BdTrainStudentFeedbackRecord> {

    /**
     * 获取学员反馈表
     * @param userId
     * @param taskId
     * @return
     */
    List<BdTrainStudentFeedbackRecord> getStudentFeedbackRecordById(@Param("userId") String userId, @Param("taskId") String taskId,@Param("isSubmit") Integer isSubmit);

    /**
     * 获取所有学员反馈表
     * @param userId
     * @param taskId
     * @return
     */
    List<BdTrainStudentFeedbackRecord> getStudentFeedbackRecord(@Param("userId") String userId,@Param("taskId") String taskId);
    /**
     * 获取问题反馈主表id
     * @param
     * @return
     * */
    BdTrainQuestionFeedback getBdTrainQuestionFeedbackId();


}
