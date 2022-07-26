package com.aiurt.modules.train.task.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.modules.train.feedback.entity.*;
import com.aiurt.modules.train.feedback.mapper.*;
import com.aiurt.modules.train.task.dto.StudentFeedbackRecordDTO;
import com.aiurt.modules.train.task.entity.BdTrainStudentFeedbackRecord;
import com.aiurt.modules.train.task.mapper.BdTrainStudentFeedbackRecordMapper;
import com.aiurt.modules.train.task.service.IBdTrainStudentFeedbackRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 学员反馈记录
 * @Author: jeecg-boot
 * @Date:   2022-04-20
 * @Version: V1.0
 */
@Service
public class BdTrainStudentFeedbackRecordServiceImpl extends ServiceImpl<BdTrainStudentFeedbackRecordMapper, BdTrainStudentFeedbackRecord> implements IBdTrainStudentFeedbackRecordService {

    @Autowired
    private BdTrainQuestionFeedbackOptionsMapper bdTrainQuestionFeedbackOptionsMapper;
    @Autowired
    private BdTrainQuestionFeedbackOptionsRecordMapper bdTrainQuestionFeedbackOptionsRecordMapper;

    @Autowired
    private BdTrainQuestionFeedbackQuesMapper bdTrainQuestionFeedbackQuesMapper;
   @Autowired
    private BdTrainQuestionFeedbackQuesRecordMapper bdTrainQuestionFeedbackQuesRecordMapper;

    @Autowired
    private BdTrainStudentFeedbackRecordMapper bdTrainStudentFeedbackRecordMapper;

    @Autowired
    private BdTrainQuestionFeedbackMapper bdTrainQuestionFeedbackMapper;
    @Autowired
    private BdTrainQuestionFeedbackRecordMapper bdTrainQuestionFeedbackRecordMapper;
    @Override
    public BdTrainQuestionFeedback getStudentFeedbackRecordById(String userId, String taskId) {
        List<List<BdTrainQuestionFeedbackQuesRecord>> stuDto = new ArrayList<>();
        BdTrainQuestionFeedbackRecord bdTrainQuestionFeedbackRecord = bdTrainQuestionFeedbackRecordMapper.getStudentFeedBack(taskId);
        List<BdTrainQuestionFeedbackOptionsRecord> bdTrainQuestionFeedbackOptions = bdTrainQuestionFeedbackOptionsRecordMapper.selectByMainId(bdTrainQuestionFeedbackRecord.getId());
        List<BdTrainQuestionFeedbackQuesRecord> bdTrainQuestionFeedbackQuesRecords = bdTrainQuestionFeedbackQuesRecordMapper.selectByMainId(bdTrainQuestionFeedbackRecord.getId());
        bdTrainQuestionFeedbackQuesRecords.stream()
                .collect(Collectors.groupingBy(BdTrainQuestionFeedbackQuesRecord::getClassifyName,Collectors.toList()))
                .forEach((classifyName, teacherQuestionDTOListByClassifyName)->{
                    stuDto.add(teacherQuestionDTOListByClassifyName);
                });
        for (BdTrainQuestionFeedbackQuesRecord bdTrainQuestionFeedbackQuesRecord : bdTrainQuestionFeedbackQuesRecords) {
            bdTrainQuestionFeedbackQuesRecord.setOptions(bdTrainQuestionFeedbackOptions);
        }
        bdTrainQuestionFeedbackRecord.setQueList(stuDto);
        List<BdTrainStudentFeedbackRecord> studentFeedbackRecord = bdTrainStudentFeedbackRecordMapper.getStudentFeedbackRecordById(userId, taskId);
        if (ObjectUtil.isNotNull(studentFeedbackRecord) && studentFeedbackRecord.size() > 0) {

            bdTrainQuestionFeedbackRecord.setBdTrainStudentFeedbackRecords(studentFeedbackRecord);
        }else {
            bdTrainQuestionFeedbackRecord.setBdTrainStudentFeedbackRecords(new ArrayList<>());
        }
        bdTrainQuestionFeedbackRecord.setBdTrainTeacherFeedbackRecords(new ArrayList<>());
        BdTrainQuestionFeedback bdTrainQuestionFeedback = new BdTrainQuestionFeedback();
        BeanUtils.copyProperties(bdTrainQuestionFeedbackRecord,bdTrainQuestionFeedback);
        return bdTrainQuestionFeedback;
    }

    @Override
    public List<StudentFeedbackRecordDTO> getStudentFeedbackRecord(String userId,String taskId) {
        List<BdTrainStudentFeedbackRecord> studentFeedbackRecord = bdTrainStudentFeedbackRecordMapper.getStudentFeedbackRecord(userId, taskId);

        if (CollectionUtil.isNotEmpty(studentFeedbackRecord)) {
            //获取反馈单选项
            String feedbackId = studentFeedbackRecord.get(0).getBdTrainQuestionFeedbackId();
            BdTrainQuestionFeedback bdTrainQuestionFeedback = bdTrainQuestionFeedbackMapper.selectById(feedbackId);
            List<BdTrainQuestionFeedbackOptions> bdTrainQuestionFeedbackOptions = bdTrainQuestionFeedbackOptionsMapper.selectByMainId(feedbackId);

            List<StudentFeedbackRecordDTO> studentFeedbackRecordDTOs = new ArrayList<>();
            //根据反馈问题类别分组
            List<String> collect = studentFeedbackRecord.stream().map(BdTrainStudentFeedbackRecord::getClassifyName).collect(Collectors.toList());
            collect.forEach(c->{
                StudentFeedbackRecordDTO studentFeedbackRecordDTO = new StudentFeedbackRecordDTO();
                List<BdTrainStudentFeedbackRecord> collect1 = studentFeedbackRecord.stream().filter(s -> s.getClassifyName().equals(c)).collect(Collectors.toList());
                BdTrainStudentFeedbackRecord bdTrainStudentFeedbackRecord = collect1.get(0);
                studentFeedbackRecordDTO.setName(bdTrainQuestionFeedback.getName());
                studentFeedbackRecordDTO.setClassifyName(c);
                Integer questionClassify = bdTrainQuestionFeedbackQuesMapper.selectById(bdTrainStudentFeedbackRecord.getBdTrainQuestionFeedbackQuesId()).getQuestionClassify();
                studentFeedbackRecordDTO.setQuestionClassify(questionClassify);
                if (questionClassify.equals(0)) {
                    studentFeedbackRecordDTO.setOptions(bdTrainQuestionFeedbackOptions);
                }
                studentFeedbackRecordDTO.setBdTrainStudentFeedbackRecords(collect1);
                studentFeedbackRecordDTOs.add(studentFeedbackRecordDTO);
            });

            return studentFeedbackRecordDTOs;
        }
        return null;
    }
}
