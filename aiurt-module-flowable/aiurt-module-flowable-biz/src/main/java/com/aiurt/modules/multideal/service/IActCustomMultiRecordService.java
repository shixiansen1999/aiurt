package com.aiurt.modules.multideal.service;

import com.aiurt.modules.multideal.entity.ActCustomMultiRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 多实例加、减签记录
 * @Author: aiurt
 * @Date:   2023-09-25
 * @Version: V1.0
 */
public interface IActCustomMultiRecordService extends IService<ActCustomMultiRecord> {

    /**
     * 根据父executionId查询 加签记录
     * @param parentExecutionId
     * @param userName 用户名
     * @return
     */
    public List<ActCustomMultiRecord> listByParentExecutionId(String userName, String parentExecutionId);

    /**
     * 根据父executionId查询 加签记录
     * @param userName
     * @param executionId 执行实例
     * @return
     */
    public List<ActCustomMultiRecord> listByExecutionId(String userName, String executionId);
}
