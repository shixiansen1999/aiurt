package com.aiurt.modules.position.service.impl;


import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.mapper.CsLineMapper;
import com.aiurt.modules.position.service.ICsLineService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: cs_line
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Service
public class CsLineServiceImpl extends ServiceImpl<CsLineMapper, CsLine> implements ICsLineService {
    @Autowired
    private CsLineMapper csLineMapper;
    /**
     * 添加
     *
     * @param csLine
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(CsLine csLine) {
        //编码不能重复，判断数据库中是否存在，如不存在则可继续添加
        List<CsLine> list = csLineMapper.selectCode(csLine.getLineCode());
        if (!list.isEmpty()) {
            return Result.error("编码重复，请重新填写！");
        }
        //排序不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsLine> lineWrapper = new LambdaQueryWrapper<>();
        lineWrapper.eq(CsLine::getSort, csLine.getSort());
        lineWrapper.eq(CsLine::getDelFlag, 0);
        list = csLineMapper.selectList(lineWrapper);
        if (!list.isEmpty()) {
            return Result.error("一级的排序重复，请重新填写！");
        }
        //名称不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsLine> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(CsLine::getLineName, csLine.getLineName());
        nameWrapper.eq(CsLine::getDelFlag, 0);
        list = csLineMapper.selectList(nameWrapper);
        if (!list.isEmpty()) {
            return Result.error("一级名称重复，请重新填写！");
        }
        csLineMapper.insert(csLine);
        return Result.OK("添加成功！");
    }
    /**
     * 修改
     *
     * @param csLine
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(CsLine csLine) {
        //编码不能重复，判断数据库中是否存在，如不存在则可继续添加
        List<CsLine> list = csLineMapper.selectCode(csLine.getLineCode());
        if (!list.isEmpty() && !list.get(0).getId().equals(csLine.getId())) {
            return Result.error("编码重复，请重新填写！");
        }
        //排序不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsLine> lineWrapper = new LambdaQueryWrapper<>();
        lineWrapper.eq(CsLine::getSort, csLine.getSort());
        lineWrapper.eq(CsLine::getDelFlag, 0);
        list = csLineMapper.selectList(lineWrapper);
        if (!list.isEmpty() && !list.get(0).getId().equals(csLine.getId())) {
            return Result.error("一级的排序重复，请重新填写！");
        }
        //名称不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsLine> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(CsLine::getLineName, csLine.getLineName());
        nameWrapper.eq(CsLine::getDelFlag, 0);
        list = csLineMapper.selectList(nameWrapper);
        if (!list.isEmpty() && !list.get(0).getId().equals(csLine.getId())) {
            return Result.error("一级名称重复，请重新填写！");
        }
        csLineMapper.updateById(csLine);
        return Result.OK("编辑成功！");
    }
}
