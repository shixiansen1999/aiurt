package com.aiurt.modules.train.trainjobchangerecord.service;

import com.aiurt.modules.train.trainjobchangerecord.entity.TrainJobChangeRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: train_job_change_record
 * @Author: aiurt
 * @Date:   2023-06-25
 * @Version: V1.0
 */
public interface ITrainJobChangeRecordService extends IService<TrainJobChangeRecord> {
    /**
     * 岗位变动列表查询-根据id查询
     * @param id 培训档案id
     * @return 岗位变动列表查询
     */
    List<TrainJobChangeRecord> getList(String id);
}
