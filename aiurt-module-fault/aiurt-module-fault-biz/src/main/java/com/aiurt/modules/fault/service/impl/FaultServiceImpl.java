package com.aiurt.modules.fault.service.impl;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.fault.service.IFaultService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Date;

/**
 * @Description: fault
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Service
public class FaultServiceImpl extends ServiceImpl<FaultMapper, Fault> implements IFaultService {

    /**
     * 故障上报
     * @param fault 故障对象
     */
    @Override
    public void add(Fault fault) {
        // 故障编号处理
        String majorCode = fault.getMajorCode();
        StringBuilder builder = new StringBuilder("WX");
        builder.append(majorCode).append(DateUtil.format(new Date(), "yyyyMMddHHmm"));

    }


}
