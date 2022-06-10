package com.aiurt.boot.modules.fault.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swsc.copsms.modules.fault.entity.FaultChangeSparePart;
import com.swsc.copsms.modules.fault.mapper.FaultChangeSparePartMapper;
import com.swsc.copsms.modules.fault.service.IFaultChangeSparePartService;
import org.springframework.stereotype.Service;

/**
 * @Description: 故障更换备件表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Service
public class FaultChangeSparePartServiceImpl extends ServiceImpl<FaultChangeSparePartMapper, FaultChangeSparePart> implements IFaultChangeSparePartService {

}
