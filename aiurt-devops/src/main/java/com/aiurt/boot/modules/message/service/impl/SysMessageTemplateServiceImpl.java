package com.aiurt.boot.modules.message.service.impl;

import com.swsc.copsms.modules.message.service.ISysMessageTemplateService;
import com.swsc.copsms.common.system.base.service.impl.BaseServiceImpl;
import com.swsc.copsms.modules.message.entity.SysMessageTemplate;
import com.swsc.copsms.modules.message.mapper.SysMessageTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @Description: 消息模板
 * @Author: swsc
 * @Date:  2019-04-09
 * @Version: V1.0
 */
@Service
public class SysMessageTemplateServiceImpl extends BaseServiceImpl<SysMessageTemplateMapper, SysMessageTemplate> implements ISysMessageTemplateService {

    @Autowired
    private SysMessageTemplateMapper sysMessageTemplateMapper;


    @Override
    public List<SysMessageTemplate> selectByCode(String code) {
        return sysMessageTemplateMapper.selectByCode(code);
    }
}
