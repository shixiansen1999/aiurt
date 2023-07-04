package com.aiurt.modules.train.traindegreerecord.service.impl;

import com.aiurt.modules.train.traindegreerecord.entity.TrainDegreeRecord;
import com.aiurt.modules.train.traindegreerecord.mapper.TrainDegreeRecordMapper;
import com.aiurt.modules.train.traindegreerecord.service.ITrainDegreeRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: train_degree_record
 * @Author: aiurt
 * @Date:   2023-06-25
 * @Version: V1.0
 */
@Service
public class TrainDegreeRecordServiceImpl extends ServiceImpl<TrainDegreeRecordMapper, TrainDegreeRecord> implements ITrainDegreeRecordService {
    @Autowired
    private TrainDegreeRecordMapper degreeRecordMapper;
    @Override
    public List<TrainDegreeRecord> getList(String id) {
        return degreeRecordMapper.getList(id);
    }
}
