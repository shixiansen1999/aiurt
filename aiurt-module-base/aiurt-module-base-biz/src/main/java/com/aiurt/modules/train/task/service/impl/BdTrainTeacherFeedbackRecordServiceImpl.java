package com.aiurt.modules.train.task.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.modules.train.feedback.entity.*;
import com.aiurt.modules.train.feedback.mapper.*;
import com.aiurt.modules.train.task.dto.QuestionDTO;
import com.aiurt.modules.train.task.entity.BdTrainTeacherFeedbackRecord;
import com.aiurt.modules.train.task.mapper.BdTrainTaskMapper;
import com.aiurt.modules.train.task.mapper.BdTrainTeacherFeedbackRecordMapper;
import com.aiurt.modules.train.task.service.IBdTrainTeacherFeedbackRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 讲师反馈记录表
 * @Author: jeecg-boot
 * @Date:   2022-04-20
 * @Version: V1.0
 */
@Service
public class BdTrainTeacherFeedbackRecordServiceImpl extends ServiceImpl<BdTrainTeacherFeedbackRecordMapper, BdTrainTeacherFeedbackRecord> implements IBdTrainTeacherFeedbackRecordService {
    @Autowired
    private BdTrainQuestionFeedbackOptionsMapper bdTrainQuestionFeedbackOptionsMapper;

    @Autowired
    private BdTrainQuestionFeedbackQuesMapper bdTrainQuestionFeedbackQuesMapper;
    @Autowired
    private BdTrainTeacherFeedbackRecordMapper bdTrainTeacherFeedbackRecordMapper;

    @Autowired
    private BdTrainQuestionFeedbackMapper bdTrainQuestionFeedbackMapper;

    @Autowired
    private BdTrainTaskMapper bdTrainTaskMapper;
    @Autowired
    private BdTrainQuestionFeedbackRecordMapper bdTrainQuestionFeedbackRecordMapper;
    @Autowired
    private BdTrainQuestionFeedbackQuesRecordMapper bdTrainQuestionFeedbackQuesRecordMapper;
    @Autowired
    private BdTrainQuestionFeedbackOptionsRecordMapper bdTrainQuestionFeedbackOptionsRecordMapper;

    @Override
    public BdTrainQuestionFeedback getTeacherFeedbackRecord(String userId, String taskId) {
        //判断是否有反馈记录
        List<List<BdTrainQuestionFeedbackQuesRecord>> stuDto = new ArrayList<>();
        BdTrainQuestionFeedbackRecord bdTrainQuestionFeedbackRecord = bdTrainQuestionFeedbackRecordMapper.getTeacherFeedBack(taskId);
        List<BdTrainQuestionFeedbackOptionsRecord> bdTrainQuestionFeedbackOptions = bdTrainQuestionFeedbackOptionsRecordMapper.selectByMainId(bdTrainQuestionFeedbackRecord.getId());
        List<BdTrainQuestionFeedbackQuesRecord> bdTrainQuestionFeedbackQuesRecords = bdTrainQuestionFeedbackQuesRecordMapper.selectByMainId(bdTrainQuestionFeedbackRecord.getId());
        bdTrainQuestionFeedbackQuesRecords.stream()
                    .collect(Collectors.groupingBy(BdTrainQuestionFeedbackQuesRecord::getClassifyName,Collectors.toList()))
                    .forEach((classifyName,teacherQuestionDTOListByClassifyName)->{
                        stuDto.add(teacherQuestionDTOListByClassifyName);
                    });
        for (BdTrainQuestionFeedbackQuesRecord bdTrainQuestionFeedbackQue : bdTrainQuestionFeedbackQuesRecords) {
            bdTrainQuestionFeedbackQue.setOptions(bdTrainQuestionFeedbackOptions);
        }
        bdTrainQuestionFeedbackRecord.setQueList(stuDto);
        List<BdTrainTeacherFeedbackRecord> teacherFeedbackRecord = bdTrainTeacherFeedbackRecordMapper.getTeacherFeedbackRecord(userId, taskId);
        if (ObjectUtil.isNotNull(teacherFeedbackRecord) && teacherFeedbackRecord.size() > 0) {
            bdTrainQuestionFeedbackRecord.setBdTrainTeacherFeedbackRecords(teacherFeedbackRecord);
        }
        else {
            bdTrainQuestionFeedbackRecord.setBdTrainTeacherFeedbackRecords(new ArrayList<>());
        }
        bdTrainQuestionFeedbackRecord.setBdTrainStudentFeedbackRecords(new ArrayList<>());
        BdTrainQuestionFeedback bdTrainQuestionFeedback = new BdTrainQuestionFeedback();
        BeanUtils.copyProperties(bdTrainQuestionFeedbackRecord,bdTrainQuestionFeedback);
        return bdTrainQuestionFeedback;
    }

    @Override
    public BdTrainTeacherFeedbackRecord queryTeacherTaskFeedbackEvaluate(String userId, String taskId) {

        List<QuestionDTO> teacherQuestionDTOList = bdTrainTaskMapper.selectquestionList(taskId,userId);
        List<List<QuestionDTO>> stuDto = new ArrayList<>();
        teacherQuestionDTOList.stream()
                .collect(Collectors.groupingBy(QuestionDTO::getClassifyName,Collectors.toList()))
                .forEach((classifyName,teacherQuestionDTOListByClassifyName)->{
                    stuDto.add(teacherQuestionDTOListByClassifyName);
                });
        BdTrainTeacherFeedbackRecord trainTeacherFeedbackRecordList = new BdTrainTeacherFeedbackRecord();
        trainTeacherFeedbackRecordList.setTeaQuestionDTOs(stuDto);
        return trainTeacherFeedbackRecordList;
    }
}
