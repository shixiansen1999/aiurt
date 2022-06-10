package com.aiurt.boot.modules.fault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swsc.copsms.common.result.FaultRepairRecordResult;
import com.swsc.copsms.modules.fault.entity.FaultRepairRecord;

import java.util.List;

/**
 * @Description: 故障维修记录表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface FaultRepairRecordMapper extends BaseMapper<FaultRepairRecord> {

    /**
     * 根据code查询检修情况
     * @param code
     * @return
     */
    List<FaultRepairRecordResult> queryDetail(String code);

}
