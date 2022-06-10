package com.aiurt.boot.modules.manage.service.impl;

import com.swsc.copsms.modules.manage.entity.Work;
import com.swsc.copsms.modules.manage.mapper.WorkMapper;
import com.swsc.copsms.modules.manage.service.IWorkService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: cs_work
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class WorkServiceImpl extends ServiceImpl<WorkMapper, Work> implements IWorkService {

}
