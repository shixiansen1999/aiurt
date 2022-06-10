package com.aiurt.boot.modules.fault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swsc.copsms.modules.fault.entity.FaultEnclosure;

import java.util.List;

/**
 * @Description: 故障-附件表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface FaultEnclosureMapper extends BaseMapper<FaultEnclosure> {

    /**
     * 查询附件列表
     * @param code
     * @return
     */
    List<String> query(String code);
}
