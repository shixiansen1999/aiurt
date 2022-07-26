package com.aiurt.modules.train.quzrtz.job;

import com.aiurt.modules.train.eaxm.constans.ExamConstans;
import com.aiurt.modules.train.exam.entity.BdExamRecord;
import com.aiurt.modules.train.eaxm.mapper.BdExamPaperMapper;
import com.aiurt.modules.train.eaxm.mapper.BdExamRecordMapper;
import com.aiurt.modules.train.task.entity.BdTrainMakeupExamRecord;
import com.aiurt.modules.train.task.entity.BdTrainTask;
import com.aiurt.modules.train.task.mapper.BdTrainMakeupExamRecordMapper;
import com.aiurt.modules.train.task.mapper.BdTrainTaskMapper;
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
    }
}
