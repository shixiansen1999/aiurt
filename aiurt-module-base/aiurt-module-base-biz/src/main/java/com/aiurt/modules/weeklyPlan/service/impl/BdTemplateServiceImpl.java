package com.aiurt.modules.weeklyPlan.service.impl;

import com.aiurt.modules.weeklyPlan.entity.BdTemplate;
import com.aiurt.modules.weeklyPlan.mapper.BdTemplateMapper;
import com.aiurt.modules.weeklyPlan.service.IBdTemplateService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.RecursiveTask;

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
}
