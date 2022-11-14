package com.aiurt.boot.weeklyplan.service.impl;

import com.aiurt.boot.weeklyplan.entity.BdTemplate;
import com.aiurt.boot.weeklyplan.mapper.BdTemplateMapper;
import com.aiurt.boot.weeklyplan.service.IBdTemplateService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @Description: 周计划模板表
 * @Author: Lai W.
 * @Version: V1.0
 */
@Service
public class BdTemplateServiceImpl extends ServiceImpl<BdTemplateMapper, BdTemplate> implements IBdTemplateService {

    @Autowired
    private BdTemplateMapper bdTemplateMapper;

    /**
     * 查询
     * @return
     */
    @Override
    public List<BdTemplate> queryAll() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        return bdTemplateMapper.selectList(new LambdaQueryWrapper<BdTemplate>().eq(BdTemplate::getUserId, sysUser.getId()));
    }

    @Override
    public boolean saveBatch(Collection<BdTemplate> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<BdTemplate> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<BdTemplate> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(BdTemplate entity) {
        return false;
    }

    @Override
    public BdTemplate getOne(Wrapper<BdTemplate> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<BdTemplate> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<BdTemplate> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }
}
