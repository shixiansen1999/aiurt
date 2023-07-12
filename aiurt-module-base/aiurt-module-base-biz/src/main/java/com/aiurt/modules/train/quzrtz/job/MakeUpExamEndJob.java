package com.aiurt.modules.train.quzrtz.job;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.train.eaxm.constans.ExamConstans;
import com.aiurt.modules.train.eaxm.mapper.BdExamPaperMapper;
import com.aiurt.modules.train.eaxm.mapper.BdExamRecordMapper;
import com.aiurt.modules.train.exam.entity.BdExamRecord;
import com.aiurt.modules.train.task.entity.BdTrainMakeupExamRecord;
import com.aiurt.modules.train.task.entity.BdTrainTask;
import com.aiurt.modules.train.task.mapper.BdTrainMakeupExamRecordMapper;
import com.aiurt.modules.train.task.mapper.BdTrainTaskMapper;
import com.aiurt.modules.train.trainarchive.entity.TrainArchive;
import com.aiurt.modules.train.trainarchive.service.ITrainArchiveService;
import com.aiurt.modules.train.trainrecord.entity.TrainRecord;
import com.aiurt.modules.train.trainrecord.service.ITrainRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 考试时间结束了，定时任务结束考试
 * @author lkj
 */
@Service
public class MakeUpExamEndJob implements Job {
    @Autowired
    private BdExamRecordMapper bdExamRecordMapper;
    @Autowired
    private BdExamPaperMapper bdExamPaperMapper;
    @Autowired
    private BdTrainTaskMapper bdTrainTaskMapper;
    @Autowired
    private BdTrainMakeupExamRecordMapper bdTrainMakeupExamRecordMapper;
    @Autowired
    private ITrainArchiveService archiveService;
    @Autowired
    private ITrainRecordService recordService;

    private String parameter;

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        BdExamRecord bdExamRecord = new BdExamRecord();
        bdExamRecord.setId(parameter);
        BdExamRecord record = bdExamRecordMapper.selectById(bdExamRecord.getId());
        //如果是考试中改为未考试
        if (ExamConstans.IN_THE_EXAM.equals(record.getExamState())) {
            record.setExamState("0");
        }
        //如果是未考试
        if (ExamConstans.NOT_TESTED.equals(record.getExamState())) {
            record.setIsPass(0);
            record.setIsRelease("3");
            BdTrainMakeupExamRecord bdTrainMakeupExamRecord = new BdTrainMakeupExamRecord();
            BdTrainTask bdTrainTask = bdTrainTaskMapper.selectById(record.getTrainTaskId());
            bdTrainMakeupExamRecord.setSysOrgCode(bdTrainTask.getTaskTeamName());
            bdTrainMakeupExamRecord.setTrainTaskId(record.getTrainTaskId());
            bdTrainMakeupExamRecord.setExamPaperId(record.getExamPaperId());
            bdTrainMakeupExamRecord.setUserId(record.getUserId());
            bdTrainMakeupExamRecord.setExamId(record.getId());
            bdTrainMakeupExamRecord.setExamClassify(0);
            bdTrainMakeupExamRecordMapper.insert(bdTrainMakeupExamRecord);
        }
        bdExamRecordMapper.updateById(record);
        //定时任务结束，更新培训档案
        TrainArchive archive = archiveService.getOne(new LambdaQueryWrapper<TrainArchive>()
                .eq(TrainArchive::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(TrainArchive::getUserId, record.getUserId()));
        if(ObjectUtil.isNotEmpty(archive)){
            TrainRecord trainRecord = recordService.getOne(new LambdaQueryWrapper<TrainRecord>()
                    .eq(TrainRecord::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .eq(TrainRecord::getTrainTaskId, record.getTrainTaskId()).eq(TrainRecord::getTrainArchiveId, archive.getId()));
            if(ObjectUtil.isNotEmpty(trainRecord)){
                trainRecord.setCheckGrade(ObjectUtil.isNotEmpty(record.getScore())?String.valueOf(record.getScore()):"0");
                recordService.updateById(trainRecord);
            }
        }
    }
}
