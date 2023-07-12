package com.aiurt.modules.train.traindegreerecord.mapper;

import com.aiurt.modules.train.traindegreerecord.entity.TrainDegreeRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: train_degree_record
 * @Author: aiurt
 * @Date:   2023-06-25
 * @Version: V1.0
 */
public interface TrainDegreeRecordMapper extends BaseMapper<TrainDegreeRecord> {
    /**
     * 培训学历记录列表查询-通过id查询
     * @param id 培训档案id
     * @return 返回培训记录信息
     */
    List<TrainDegreeRecord> getList(@Param("id") String id);
}
