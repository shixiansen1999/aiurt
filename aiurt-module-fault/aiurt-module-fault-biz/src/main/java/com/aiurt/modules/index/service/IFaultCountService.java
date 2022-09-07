package com.aiurt.modules.index.service;

import com.aiurt.modules.fault.dto.FaultIndexDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.HashMap;

public interface IFaultCountService {
    FaultIndexDTO queryFaultCount(Date startDate, Date endDate);
}
