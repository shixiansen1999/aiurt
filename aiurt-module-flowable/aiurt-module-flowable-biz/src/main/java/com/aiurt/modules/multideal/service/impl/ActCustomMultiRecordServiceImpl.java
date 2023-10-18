package com.aiurt.modules.multideal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.multideal.entity.ActCustomMultiRecord;
import com.aiurt.modules.multideal.mapper.ActCustomMultiRecordMapper;
import com.aiurt.modules.multideal.service.IActCustomMultiRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Collections;
import java.util.List;

/**
 * @Description: 多实例加、减签记录
 * @Author: aiurt
 * @Date:   2023-09-25
 * @Version: V1.0
 */
@Service
public class ActCustomMultiRecordServiceImpl extends ServiceImpl<ActCustomMultiRecordMapper, ActCustomMultiRecord> implements IActCustomMultiRecordService {

    /**
     * 根据父executionId查询 加签记录
     *
     * @param parentExecutionId
     * @return
     */
    @Override
    public List<ActCustomMultiRecord> listByParentExecutionId(String userName, String parentExecutionId) {

        if (StrUtil.isBlank(parentExecutionId) || StrUtil.isBlank(userName)) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<ActCustomMultiRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActCustomMultiRecord::getParentExecutionId, parentExecutionId);
        wrapper.eq(ActCustomMultiRecord::getUserName, userName);
        List<ActCustomMultiRecord> multiRecordList = baseMapper.selectList(wrapper);
        return multiRecordList;
    }

    /**
     * 根据父executionId查询 加签记录
     *
     * @param userName
     * @param executionId 执行实例
     * @return
     */
    @Override
    public List<ActCustomMultiRecord> listByExecutionId(String userName, String executionId) {
        if (StrUtil.isBlank(executionId) || StrUtil.isBlank(userName)) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<ActCustomMultiRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActCustomMultiRecord::getExecutionId, executionId);
        wrapper.eq(ActCustomMultiRecord::getUserName, userName);
        List<ActCustomMultiRecord> multiRecordList = baseMapper.selectList(wrapper);
        return multiRecordList;
    }
}
