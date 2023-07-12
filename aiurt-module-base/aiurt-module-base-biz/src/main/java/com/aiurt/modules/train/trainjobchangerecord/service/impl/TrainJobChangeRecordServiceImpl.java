package com.aiurt.modules.train.trainjobchangerecord.service.impl;

import com.aiurt.modules.train.trainjobchangerecord.entity.TrainJobChangeRecord;
import com.aiurt.modules.train.trainjobchangerecord.mapper.TrainJobChangeRecordMapper;
import com.aiurt.modules.train.trainjobchangerecord.service.ITrainJobChangeRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: train_job_change_record
 * @Author: aiurt
 * @Date:   2023-06-25
 * @Version: V1.0
 */
@Service
public class TrainJobChangeRecordServiceImpl extends ServiceImpl<TrainJobChangeRecordMapper, TrainJobChangeRecord> implements ITrainJobChangeRecordService {
    @Autowired
    private TrainJobChangeRecordMapper changeRecordMapper;
    @Override
    public List<TrainJobChangeRecord> getList(String id) {
        return changeRecordMapper.getList(id);
    }
}
