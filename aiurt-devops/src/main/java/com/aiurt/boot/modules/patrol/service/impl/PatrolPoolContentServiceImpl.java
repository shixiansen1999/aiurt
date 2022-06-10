package com.aiurt.boot.modules.patrol.service.impl;

import com.swsc.copsms.modules.patrol.entity.PatrolPoolContent;
import com.swsc.copsms.modules.patrol.mapper.PatrolPoolContentMapper;
import com.swsc.copsms.modules.patrol.service.IPatrolPoolContentService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 巡检人员任务项
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class PatrolPoolContentServiceImpl extends ServiceImpl<PatrolPoolContentMapper, PatrolPoolContent> implements IPatrolPoolContentService {

}
