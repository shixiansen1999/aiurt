package com.aiurt.modules.modeler.service.impl;

import com.aiurt.modules.modeler.entity.ActCustomModelInfo;
import com.aiurt.modules.modeler.mapper.ActCustomModelInfoMapper;
import com.aiurt.modules.modeler.service.IActCustomModelInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


/**
 * @Description: flowable流程模板定义信息
 * @Author: aiurt
 * @Date:   2022-07-08
 * @Version: V1.0
 */
@Service
public class ActCustomModelInfoServiceImpl extends ServiceImpl<ActCustomModelInfoMapper, ActCustomModelInfo> implements IActCustomModelInfoService {

    @Override
    public ActCustomModelInfo add(ActCustomModelInfo actCustomModelInfo) {
        return null;
    }
}
