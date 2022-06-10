package com.aiurt.boot.modules.fault.service.impl;

import com.aiurt.boot.modules.fault.mapper.FaultMapper;
import com.aiurt.boot.modules.fault.service.IFaultService;
import com.aiurt.boot.modules.fault.service.IFaultStatisticsService;
import com.aiurt.boot.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: niuzeyu
 * @date: 2022年01月21日 18:56
 */
@Service
public class FaultStatisticsServiceImpl implements IFaultStatisticsService {
    @Resource
    private FaultMapper faultMapper;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private IFaultService faultService;

}
