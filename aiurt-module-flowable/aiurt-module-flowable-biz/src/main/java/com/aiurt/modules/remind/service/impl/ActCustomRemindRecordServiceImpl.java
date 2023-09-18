package com.aiurt.modules.remind.service.impl;

import com.aiurt.modules.remind.entity.ActCustomRemindRecord;
import com.aiurt.modules.remind.mapper.ActCustomRemindRecordMapper;
import com.aiurt.modules.remind.service.IActCustomRemindRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 流程催办记录
 * @Author: aiurt
 * @Date:   2023-09-12
 * @Version: V1.0
 */
@Service
public class ActCustomRemindRecordServiceImpl extends ServiceImpl<ActCustomRemindRecordMapper, ActCustomRemindRecord> implements IActCustomRemindRecordService {

    /**
     * 根据流程实例id查询催办记录
     *
     * @param processInstanceId 流程实例id
     * @param userName 用户名
     * @return 催办记录
     */
    @Override
    public ActCustomRemindRecord getByProcessInstanceId(String processInstanceId, String userName) {
        LambdaQueryWrapper<ActCustomRemindRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActCustomRemindRecord::getProcessInstanceId, processInstanceId)
                .eq(ActCustomRemindRecord::getRemindUserName, userName).orderByDesc(ActCustomRemindRecord::getLastRemindTime).last("limit 1");
        ActCustomRemindRecord actCustomRemindRecord = baseMapper.selectOne(queryWrapper);
        return actCustomRemindRecord;
    }
}
