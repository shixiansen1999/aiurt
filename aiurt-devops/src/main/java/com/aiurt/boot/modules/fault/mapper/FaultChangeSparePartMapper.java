package com.aiurt.boot.modules.fault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swsc.copsms.common.result.SpareResult;
import com.swsc.copsms.modules.fault.entity.FaultChangeSparePart;

import java.util.List;

/**
 * @Description: 故障更换备件表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface FaultChangeSparePartMapper extends BaseMapper<FaultChangeSparePart> {


    /**
     * 根据code查询更换备件
     * @param code
     * @return
     */
    List<SpareResult> querySpare(String code);
}
