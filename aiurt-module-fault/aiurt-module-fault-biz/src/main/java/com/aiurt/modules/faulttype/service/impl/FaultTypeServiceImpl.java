package com.aiurt.modules.faulttype.service.impl;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.faulttype.entity.FaultType;
import com.aiurt.modules.faulttype.mapper.FaultTypeMapper;
import com.aiurt.modules.faulttype.service.IFaultTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * @Description: fault_type
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
@Service
public class FaultTypeServiceImpl extends ServiceImpl<FaultTypeMapper, FaultType> implements IFaultTypeService {
    @Autowired
    private FaultTypeMapper faultTypeMapper;
    /**
     * 添加
     *
     * @param faultType
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(FaultType faultType) {
        //编码不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<FaultType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaultType::getCode, faultType.getCode());
        queryWrapper.eq(FaultType::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<FaultType> list = faultTypeMapper.selectList(queryWrapper);
        if (!list.isEmpty()) {
            return Result.error("故障分类编码重复，请重新填写！");
        }
        //同一专业下，名称不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<FaultType> lineWrapper = new LambdaQueryWrapper<>();
        lineWrapper.eq(FaultType::getName, faultType.getName());
        lineWrapper.eq(FaultType::getMajorCode, faultType.getMajorCode());
        lineWrapper.eq(FaultType::getDelFlag, CommonConstant.DEL_FLAG_0);
        list = faultTypeMapper.selectList(lineWrapper);
        if (!list.isEmpty()) {
            return Result.error("相同专业下，故障分类名称重复，请重新填写！");
        }
        faultTypeMapper.insert(faultType);
        return Result.OK("添加成功！");
    }
    /**
     * 修改
     *
     * @param faultType
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(FaultType faultType) {
        //编码不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<FaultType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaultType::getCode, faultType.getCode());
        queryWrapper.eq(FaultType::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<FaultType> list = faultTypeMapper.selectList(queryWrapper);
        if (!list.isEmpty() && !list.get(0).getId().equals(faultType.getId())) {
            return Result.error("故障分类编码重复，请重新填写！");
        }
        //同一专业下，名称不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<FaultType> lineWrapper = new LambdaQueryWrapper<>();
        lineWrapper.eq(FaultType::getName, faultType.getName());
        lineWrapper.eq(FaultType::getMajorCode, faultType.getMajorCode());
        lineWrapper.eq(FaultType::getDelFlag, CommonConstant.DEL_FLAG_0);
        list = faultTypeMapper.selectList(lineWrapper);
        if (!list.isEmpty() && !list.get(0).getId().equals(faultType.getId())) {
            return Result.error("故障分类编码重复，请重新填写！");
        }
        faultTypeMapper.updateById(faultType);
        return Result.OK("编辑成功！");
    }
}
