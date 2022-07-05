package com.aiurt.modules.major.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.mapper.CsMajorMapper;
import com.aiurt.modules.major.service.ICsMajorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: cs_major
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Service
public class CsMajorServiceImpl extends ServiceImpl<CsMajorMapper, CsMajor> implements ICsMajorService {
    @Autowired
    private CsMajorMapper csMajorMapper;
    /**
     * 添加
     *
     * @param csMajor
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(CsMajor csMajor) {
        //专业编码不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsMajor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CsMajor::getMajorCode, csMajor.getMajorCode());
        queryWrapper.eq(CsMajor::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsMajor> list = csMajorMapper.selectList(queryWrapper);
        if (!list.isEmpty()) {
            return Result.error("专业编码重复，请重新填写！");
        }
        //专业名称不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsMajor> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(CsMajor::getMajorName, csMajor.getMajorName());
        nameWrapper.eq(CsMajor::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsMajor> nameList = csMajorMapper.selectList(nameWrapper);
        if (!nameList.isEmpty()) {
            return Result.error("专业名称重复，请重新填写！");
        }
        csMajorMapper.insert(csMajor);
        return Result.OK("添加成功！");
    }
    /**
     * 修改
     *
     * @param csMajor
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(CsMajor csMajor) {
        //专业编码不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsMajor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CsMajor::getMajorCode, csMajor.getMajorCode());
        queryWrapper.eq(CsMajor::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsMajor> list = csMajorMapper.selectList(queryWrapper);
        if (!list.isEmpty() && !list.get(0).getId().equals(csMajor.getId())) {
            return Result.error("专业编码重复，请重新填写！");
        }
        //专业名称不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsMajor> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(CsMajor::getMajorName, csMajor.getMajorName());
        nameWrapper.eq(CsMajor::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsMajor> nameList = csMajorMapper.selectList(nameWrapper);
        if (!nameList.isEmpty() && !nameList.get(0).getId().equals(csMajor.getId())) {
            return Result.error("专业名称重复，请重新填写！");
        }
        csMajorMapper.updateById(csMajor);
        return Result.OK("编辑成功！");
    }
}
