package com.aiurt.boot.modules.patrol.service.impl;

import com.swsc.copsms.modules.patrol.entity.NumberGenerate;
import com.swsc.copsms.modules.patrol.mapper.NumberGenerateMapper;
import com.swsc.copsms.modules.patrol.service.INumberGenerateService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 任务编号表
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Service
public class NumberGenerateServiceImpl extends ServiceImpl<NumberGenerateMapper, NumberGenerate> implements INumberGenerateService {

}
