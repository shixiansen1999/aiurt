package com.aiurt.modules.train.quzrtz.job;

import com.aiurt.modules.train.exam.entity.BdExamRecord;
import com.aiurt.modules.train.eaxm.mapper.BdExamRecordMapper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class MakeUpExamStartJob implements Job {

    @Autowired
    private BdExamRecordMapper bdExamRecordMapper;

    private String parameter;

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        BdExamRecord bdExamRecord = new BdExamRecord();
        bdExamRecord.setId(parameter);
        BdExamRecord record = bdExamRecordMapper.selectById(bdExamRecord.getId());
        record.setIsRelease("1");
        bdExamRecordMapper.updateById(record);
    }
}
