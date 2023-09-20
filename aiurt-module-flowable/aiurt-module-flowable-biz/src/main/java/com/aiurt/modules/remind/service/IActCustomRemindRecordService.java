package com.aiurt.modules.remind.service;

import com.aiurt.modules.remind.entity.ActCustomRemindRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 流程催办记录
 * @Author: aiurt
 * @Date:   2023-09-12
 * @Version: V1.0
 */
public interface IActCustomRemindRecordService extends IService<ActCustomRemindRecord> {


    /**
     * 根据流程实例id查询催办记录
     * @param processInstanceId 流程实例id
     * @param userName 用户名
     * @return 催办记录
     */
    ActCustomRemindRecord getByProcessInstanceId(String processInstanceId, String userName);
}
