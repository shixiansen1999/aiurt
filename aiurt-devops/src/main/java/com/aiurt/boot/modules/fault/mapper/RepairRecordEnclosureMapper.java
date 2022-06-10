package com.aiurt.boot.modules.fault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swsc.copsms.modules.fault.entity.RepairRecordEnclosure;

import java.util.List;

/**
 * @Description: 维修记录-附件表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface RepairRecordEnclosureMapper extends BaseMapper<RepairRecordEnclosure> {


    /**
     * 根据code查询附件列表
     * @param repairRecordId
     * @return
     */
    List<String> queryDetail(Long repairRecordId);

}
