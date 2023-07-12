package com.aiurt.modules.train.trainrecord.service.impl;

import com.aiurt.modules.train.trainrecord.entity.TrainRecord;
import com.aiurt.modules.train.trainrecord.mapper.TrainRecordMapper;
import com.aiurt.modules.train.trainrecord.service.ITrainRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: train_record
 * @Author: aiurt
 * @Date:   2023-06-25
 * @Version: V1.0
 */
@Service
public class TrainRecordServiceImpl extends ServiceImpl<TrainRecordMapper, TrainRecord> implements ITrainRecordService {
@Autowired
private TrainRecordMapper recordMapper;
    @Override
    public List<TrainRecord> getList(String id) {
        return recordMapper.getList(id);
    }
}
