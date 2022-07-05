package com.aiurt.modules.manufactor.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.aiurt.modules.manufactor.mapper.CsManufactorMapper;
import com.aiurt.modules.manufactor.service.ICsManufactorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        //判断厂商编码是否重复
		LambdaQueryWrapper<CsManufactor> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(CsManufactor::getCode, csManufactor.getCode());
        queryWrapper.eq(CsManufactor::getDelFlag, CommonConstant.DEL_FLAG_0);
		List<CsManufactor> list = csManufactorMapper.selectList(queryWrapper);
		if (!list.isEmpty()) {
			return Result.error("厂商编码重复，请重新填写！");
		}
        //判断厂商名称是否重复
        LambdaQueryWrapper<CsManufactor> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(CsManufactor::getName, csManufactor.getName());
        queryWrapper.eq(CsManufactor::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsManufactor> nameList = csManufactorMapper.selectList(nameWrapper);
        if (!nameList.isEmpty()) {
            return Result.error("厂商名称重复，请重新填写！");
        }
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
        CsManufactor manufactor = getById(csManufactor.getId());
        //如果附件修改了，则删除附件表
        if(null!=csManufactor.getFilePath() && !manufactor.getFilePath().equals(csManufactor.getFilePath())){
            //删除附件表 todo
        }
        //判断厂商编码是否重复
        LambdaQueryWrapper<CsManufactor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CsManufactor::getCode, csManufactor.getCode());
        queryWrapper.eq(CsManufactor::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsManufactor> list = csManufactorMapper.selectList(queryWrapper);
        if (!list.isEmpty() && list.get(0).equals(csManufactor.getId())) {
            return Result.error("厂商编码重复，请重新填写！");
        }
        //判断厂商名称是否重复
        LambdaQueryWrapper<CsManufactor> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(CsManufactor::getName, csManufactor.getName());
        queryWrapper.eq(CsManufactor::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsManufactor> nameList = csManufactorMapper.selectList(nameWrapper);
        if (!nameList.isEmpty() && nameList.get(0).equals(csManufactor.getId())) {
            return Result.error("厂商名称重复，请重新填写！");
        }
        csManufactorMapper.updateById(csManufactor);
        return Result.OK("编辑成功！");
    }
}
