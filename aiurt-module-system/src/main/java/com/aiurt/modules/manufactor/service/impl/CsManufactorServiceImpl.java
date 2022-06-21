package com.aiurt.modules.manufactor.service.impl;

import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.aiurt.modules.manufactor.mapper.CsManufactorMapper;
import com.aiurt.modules.manufactor.service.ICsManufactorService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: cs_manufactor
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Service
public class CsManufactorServiceImpl extends ServiceImpl<CsManufactorMapper, CsManufactor> implements ICsManufactorService {
    @Autowired
    private CsManufactorMapper csManufactorMapper;
    /**
     * 添加
     *
     * @param csManufactor
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(CsManufactor csManufactor) {
        //todo 上传附件
        csManufactorMapper.insert(csManufactor);
        return Result.OK("添加成功！");
    }
    /**
     * 修改
     *
     * @param csManufactor
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(CsManufactor csManufactor) {
        //todo 上传附件
        csManufactorMapper.updateById(csManufactor);
        return Result.OK("编辑成功！");
    }
}
