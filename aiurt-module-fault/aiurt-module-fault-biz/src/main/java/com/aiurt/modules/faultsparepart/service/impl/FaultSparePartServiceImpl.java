package com.aiurt.modules.faultsparepart.service.impl;

import com.aiurt.modules.faultcausesolution.entity.FaultCauseSolution;
import com.aiurt.modules.faultcausesolution.mapper.FaultCauseSolutionMapper;
import com.aiurt.modules.faultcausesolution.service.IFaultCauseSolutionService;
import com.aiurt.modules.faultsparepart.entity.FaultSparePart;
import com.aiurt.modules.faultsparepart.mapper.FaultSparePartMapper;
import com.aiurt.modules.faultsparepart.service.IFaultSparePartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Description: 故障知识分类
 * @Author: aiurt
 * @Date: 2022-06-24
 * @Version: V1.0
 */
@Service
public class FaultSparePartServiceImpl extends ServiceImpl<FaultSparePartMapper, FaultSparePart> implements IFaultSparePartService {

}
