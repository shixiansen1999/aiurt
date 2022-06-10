package com.aiurt.boot.modules.fault.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swsc.copsms.modules.fault.entity.FaultEnclosure;
import com.swsc.copsms.modules.fault.mapper.FaultEnclosureMapper;
import com.swsc.copsms.modules.fault.service.IFaultEnclosureService;
import org.springframework.stereotype.Service;

/**
 * @Description: 故障-附件表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Service
public class FaultEnclosureServiceImpl extends ServiceImpl<FaultEnclosureMapper, FaultEnclosure> implements IFaultEnclosureService {

}
