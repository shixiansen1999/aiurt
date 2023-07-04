package com.aiurt.modules.train.quzrtz.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.train.eaxm.constans.ExamConstans;
import com.aiurt.modules.train.eaxm.mapper.BdExamRecordDetailMapper;
import com.aiurt.modules.train.eaxm.mapper.BdExamRecordMapper;
import com.aiurt.modules.train.exam.entity.BdExamRecord;
import com.aiurt.modules.train.question.entity.BdQuestion;
import com.aiurt.modules.train.question.mapper.BdQuestionMapper;
import com.aiurt.modules.train.task.entity.BdTrainMakeupExamRecord;
import com.aiurt.modules.train.task.entity.BdTrainTask;
import com.aiurt.modules.train.task.entity.BdTrainTaskUser;
import com.aiurt.modules.train.task.mapper.BdTrainMakeupExamRecordMapper;
import com.aiurt.modules.train.task.mapper.BdTrainTaskMapper;
import com.aiurt.modules.train.task.mapper.BdTrainTaskUserMapper;
import com.aiurt.modules.train.task.service.IBdTrainTaskService;
import com.aiurt.modules.train.task.vo.BdTrainTaskPage;
import com.aiurt.modules.train.trainarchive.entity.TrainArchive;
import com.aiurt.modules.train.trainarchive.service.ITrainArchiveService;
import com.aiurt.modules.train.trainrecord.entity.TrainRecord;
import com.aiurt.modules.train.trainrecord.service.ITrainRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Service
public class SampleJob implements Job {

    @Autowired
    private BdQuestionMapper bdQuestionMapper;

    @Autowired
    private IBdTrainTaskService bdTrainTaskService;
    @Autowired
    private BdExamRecordMapper bdExamRecordMapper;
    @Autowired
    private BdTrainTaskUserMapper bdTrainTaskUserMapper;
    @Autowired
    private BdTrainMakeupExamRecordMapper bdTrainMakeupExamRecordMapper;
    @Autowired
    private BdExamRecordDetailMapper bdExamRecordDetailMapper;
    @Autowired
    private BdTrainTaskMapper bdTrainTaskMapper;
    @Autowired
    private ITrainArchiveService archiveService;
    @Autowired
    private ITrainRecordService recordService;

    private String parameter;

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        BdTrainTaskPage bdTrainTaskPage = new BdTrainTaskPage();
        BdTrainTask bdTrainTask = bdTrainTaskService.getById(parameter);
        List<BdQuestion> bdQuestions = bdQuestionMapper.contentList(bdTrainTask.getExamPaperId());
        boolean flag = false;
        for (int i = 0; i < bdQuestions.size(); i++) {
            Integer queType = bdQuestions.get(i).getQueType();
            if (queType == 3) {
                flag = true;
                break;
            }
        }
        List<BdExamRecord> bdExamRecords = bdExamRecordMapper.getNum(bdTrainTask.getId());
        List<BdExamRecord> examRecords = new ArrayList<>();
        List<TrainArchive> archiveList = archiveService.list(new LambdaQueryWrapper<TrainArchive>().eq(TrainArchive::getDelFlag, CommonConstant.DEL_FLAG_0));
        Map<String, TrainArchive> archiveMap = archiveList.stream().collect(Collectors.toMap(TrainArchive::getUserId, Function.identity()));
        List<TrainRecord> trainRecords = new ArrayList<>();
        //对已考试的进行状态更改
        for (BdExamRecord bdExamRecord : bdExamRecords) {
            TrainArchive archive = archiveMap.get(bdExamRecord.getUserId());
            LambdaQueryWrapper<TrainRecord> queryWrapper = new LambdaQueryWrapper<TrainRecord>().eq(TrainRecord::getDelFlag, CommonConstant.DEL_FLAG_0);
            if ("1".equals(bdExamRecord.getIsRelease())) {
                //如果是考试中改为未考试，且直接已结束
                if (ExamConstans.IN_THE_EXAM.equals(bdExamRecord.getExamState())) {
                    bdExamRecord.setExamState("0");
                    bdExamRecord.setIsRelease("3");
                    examRecords.add(bdExamRecord);
                    bdExamRecordMapper.deleteById(bdExamRecord.getId());
                    if(ObjectUtil.isNotEmpty(archive)){
                        queryWrapper.eq(TrainRecord::getTrainArchiveId, archive.getId());
                        queryWrapper.eq(TrainRecord::getTrainTaskId, bdTrainTask.getId());
                        TrainRecord trainRecord = recordService.getOne(queryWrapper);
                        trainRecord.setCheckGrade("0");
                        trainRecords.add(trainRecord);
                    }
                }else {
                    //已考试，无简答题直接结束，有则待复核
                    if (!flag) {
                        bdExamRecord.setIsRelease("3");
                    } else {
                        bdExamRecord.setIsRelease("2");
                    }
                    if(ObjectUtil.isNotEmpty(archive)){
                        queryWrapper.eq(TrainRecord::getTrainArchiveId, archive.getId());
                        queryWrapper.eq(TrainRecord::getTrainTaskId, bdTrainTask.getId());
                        TrainRecord trainRecord = recordService.getOne(queryWrapper);
                        trainRecord.setCheckGrade(ObjectUtil.isNotEmpty(bdExamRecord.getScore())?String.valueOf(bdExamRecord.getScore()):"0");
                        trainRecords.add(trainRecord);
                    }
                }
            }
        }
        List<BdTrainTaskUser> userList = bdTrainTaskUserMapper.getUserListById(bdTrainTask.getId());
        //已考试人员ids
        List<BdExamRecord> list = bdExamRecordMapper.getNum(bdTrainTask.getId());
        List<String> collect1 = list.stream().map(BdExamRecord::getUserId).collect(Collectors.toList());
        //应考试人员ids
        List<String> collect2 = userList.stream().map(BdTrainTaskUser::getUserId).collect(Collectors.toList());
        //未考试人员ids
        collect2.removeAll(collect1);

        Map<String, BdExamRecord> map = examRecords.stream().collect(Collectors.toMap(BdExamRecord::getUserId, b -> b));
        //如果是没有考试且没有简答题的
            if (CollectionUtil.isNotEmpty(collect2) ) {
                for (String c:collect2) {
                    BdExamRecord bdExamRecord = map.get(c);
                    BdExamRecord record = new BdExamRecord();
                    if (ObjectUtil.isNotNull(bdExamRecord)) {
                        BeanUtils.copyProperties(bdExamRecord,record,bdExamRecord.getId());
                    } else {
                         record = add(bdTrainTask);
                    }
                    record.setIsRelease("3");
                    //判断有无简答题
                    if (!flag) {
                        bdTrainTaskPage.setTaskState(6);
                        record.setCorrect(1);
                    } else {
                        bdTrainTaskPage.setTaskState(5);
                        record.setCorrect(0);
                    }
                    record.setUserId(c);
                    bdExamRecordMapper.insert(record);
                    if (bdTrainTask.getMakeUpState() == 1) {
                        BdTrainMakeupExamRecord bdTrainMakeupExamRecord = new BdTrainMakeupExamRecord();
                        bdTrainMakeupExamRecord.setSysOrgCode(bdTrainTask.getTaskTeamName());
                        bdTrainMakeupExamRecord.setTrainTaskId(bdTrainTask.getId());
                        bdTrainMakeupExamRecord.setExamPaperId(bdTrainTask.getExamPaperId());
                        bdTrainMakeupExamRecord.setUserId(c);
                        bdTrainMakeupExamRecord.setExamClassify(1);
                        bdTrainMakeupExamRecordMapper.insert(bdTrainMakeupExamRecord);
                    }
                    //如果全部人缺考则更改任务状态
                    List<BdExamRecord> records = bdExamRecordMapper.getNum(parameter);
                    List<BdExamRecord> collect = records.stream().filter(b -> ExamConstans.RECORD_OVER.equals(b.getIsRelease())).collect(Collectors.toList());
                    int num1 = records.size();
                    int num2 = collect.size();
                    if (num1 == num2) {
                        bdTrainTaskPage.setTaskState(6);
                    }
                    TrainArchive archive = archiveMap.get(bdExamRecord.getUserId());
                    LambdaQueryWrapper<TrainRecord> queryWrapper = new LambdaQueryWrapper<TrainRecord>().eq(TrainRecord::getDelFlag, CommonConstant.DEL_FLAG_0);
                    if(ObjectUtil.isNotEmpty(archive)){
                        queryWrapper.eq(TrainRecord::getTrainArchiveId, archive.getId());
                        queryWrapper.eq(TrainRecord::getTrainTaskId, bdTrainTask.getId());
                        TrainRecord trainRecord = recordService.getOne(queryWrapper);
                        trainRecord.setCheckGrade("0");
                        trainRecords.add(trainRecord);
                    }
                }
            }
            if(CollUtil.isNotEmpty(trainRecords)){
                recordService.updateBatchById(trainRecords);
            }
        bdTrainTaskPage.setId(parameter);
        bdTrainTaskService.edit(bdTrainTaskPage);
    }

    private BdExamRecord add(BdTrainTask bdTrainTask) {
        BdExamRecord record = new BdExamRecord();
        record.setExamState("0");
        record.setTrainTaskId(bdTrainTask.getId());
        record.setExamTime(bdTrainTask.getExamPlanTime());
        record.setIsSubmit(0);
        record.setExamPaperId(bdTrainTask.getExamPaperId());
        record.setExamClassify(1);
        return record;
    }
}
