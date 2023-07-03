package com.aiurt.modules.train.trainrecord.service;

import com.aiurt.modules.train.trainrecord.entity.TrainRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: train_record
 * @Author: aiurt
 * @Date:   2023-06-25
 * @Version: V1.0
 */
public interface ITrainRecordService extends IService<TrainRecord> {
    /**
     * 培训记录列表-通过id查询
     * @param id 培训档案id
     * @return 培训记录列表
     */
    List<TrainRecord> getList(String id);
}
