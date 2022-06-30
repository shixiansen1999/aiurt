package com.aiurt.modules.faultlevel.service.impl;

import com.aiurt.modules.faultlevel.entity.FaultLevel;
import com.aiurt.modules.faultlevel.mapper.FaultLevelMapper;
import com.aiurt.modules.faultlevel.service.IFaultLevelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 故障等级
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
@Service
public class FaultLevelServiceImpl extends ServiceImpl<FaultLevelMapper, FaultLevel> implements IFaultLevelService {
    @Autowired
    private FaultLevelMapper faultLevelMapper;
    /**
     * 添加
     *
     * @param faultLevel
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(FaultLevel faultLevel) {
        //编码不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<FaultLevel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaultLevel::getCode, faultLevel.getCode());
        queryWrapper.eq(FaultLevel::getDelFlag, 0);
        List<FaultLevel> list = faultLevelMapper.selectList(queryWrapper);
        if (!list.isEmpty()) {
            return Result.error("故障分级编码重复，请重新填写！");
        }
        //同一专业下，名称不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<FaultLevel> lineWrapper = new LambdaQueryWrapper<>();
        lineWrapper.eq(FaultLevel::getName, faultLevel.getName());
        lineWrapper.eq(FaultLevel::getMajorCode, faultLevel.getMajorCode());
        lineWrapper.eq(FaultLevel::getDelFlag, 0);
        list = faultLevelMapper.selectList(lineWrapper);
        if (!list.isEmpty()) {
            return Result.error("相同专业下，故障分级名称重复，请重新填写！");
        }
        faultLevelMapper.insert(faultLevel);
        return Result.OK("添加成功！");
    }
    /**
     * 修改
     *
     * @param faultLevel
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(FaultLevel faultLevel) {
        //编码不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<FaultLevel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaultLevel::getCode, faultLevel.getCode());
        queryWrapper.eq(FaultLevel::getDelFlag, 0);
        List<FaultLevel> list = faultLevelMapper.selectList(queryWrapper);
        if (!list.isEmpty() && !list.get(0).getId().equals(faultLevel.getId())) {
            return Result.error("故障分级编码重复，请重新填写！");
        }
        //同一专业下，名称不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<FaultLevel> lineWrapper = new LambdaQueryWrapper<>();
        lineWrapper.eq(FaultLevel::getName, faultLevel.getName());
        lineWrapper.eq(FaultLevel::getMajorCode, faultLevel.getMajorCode());
        lineWrapper.eq(FaultLevel::getDelFlag, 0);
        list = faultLevelMapper.selectList(lineWrapper);
        if (!list.isEmpty() && !list.get(0).getId().equals(faultLevel.getId())) {
            return Result.error("相同专业下，故障分级名称重复，请重新填写！");
        }
        faultLevelMapper.updateById(faultLevel);
        return Result.OK("编辑成功！");
    }
}
