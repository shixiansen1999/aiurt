package com.aiurt.modules.train.trainjobchangerecord.mapper;

import com.aiurt.modules.train.trainjobchangerecord.entity.TrainJobChangeRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: train_job_change_record
 * @Author: aiurt
 * @Date:   2023-06-25
 * @Version: V1.0
 */
public interface TrainJobChangeRecordMapper extends BaseMapper<TrainJobChangeRecord> {
    /**
     * 培训学历记录列表查询-通过id查询
     * @param id 培训档案id
     * @return 返回培训记录信息
     */
    List<TrainJobChangeRecord> getList(@Param("id") String id);
}
