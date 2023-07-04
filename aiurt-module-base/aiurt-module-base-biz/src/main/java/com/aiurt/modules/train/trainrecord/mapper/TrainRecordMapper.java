package com.aiurt.modules.train.trainrecord.mapper;

import com.aiurt.modules.train.trainrecord.entity.TrainRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: train_record
 * @Author: aiurt
 * @Date: 2023-06-25
 * @Version: V1.0
 */
public interface TrainRecordMapper extends BaseMapper<TrainRecord> {
    /**
     * 培训记录列表-通过id查询
     *
     * @param id 培训档案id
     * @return 培训记录列表
     */
    List<TrainRecord> getList(@Param("id") String id);
}
