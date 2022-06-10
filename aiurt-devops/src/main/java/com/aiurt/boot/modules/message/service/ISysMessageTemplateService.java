package com.aiurt.boot.modules.message.service;

import java.util.List;

import com.swsc.copsms.common.system.base.service.BaseService;
import com.swsc.copsms.modules.message.entity.SysMessageTemplate;

/**
 * @Description: 消息模板
 * @Author: swsc
 * @Date:  2019-04-09
 * @Version: V1.0
 */
public interface ISysMessageTemplateService extends BaseService<SysMessageTemplate> {
    List<SysMessageTemplate> selectByCode(String code);
}
