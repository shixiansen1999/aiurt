package com.aiurt.modules.faultalarm.mapper;

import com.aiurt.modules.faultalarm.dto.resp.AlmRecordRespDTO;
import com.aiurt.modules.faultalarm.entity.AlmRecordHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 告警历史处理记录mapper
 * @Author: aiurt
 * @Date: 2023-06-05
 * @Version: V1.0
 */
public interface AlmRecordHistoryMapper extends BaseMapper<AlmRecordHistory> {

    /**
     * 根据id查询历史记录
     * @param id
     * @return
     */
    AlmRecordRespDTO queryById(String id);
}
