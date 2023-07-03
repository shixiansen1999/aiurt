package com.aiurt.modules.train.traindegreerecord.service;

import com.aiurt.modules.train.traindegreerecord.entity.TrainDegreeRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: train_degree_record
 * @Author: aiurt
 * @Date:   2023-06-25
 * @Version: V1.0
 */
public interface ITrainDegreeRecordService extends IService<TrainDegreeRecord> {
    /**
     * 培训学历记录列表查询-通过id查询
     * @param id 培训档案id
     * @return 返回培训记录信息
     */
    List<TrainDegreeRecord> getList(String id);
}
