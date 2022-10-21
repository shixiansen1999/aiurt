package com.aiurt.modules.weeklyplan.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.api.vo.TreeEntity;
import com.aiurt.modules.weeklyplan.entity.BdSite;
import com.aiurt.modules.weeklyplan.entity.BdTeam;
import com.aiurt.modules.weeklyplan.mapper.BdSiteMapper;
import com.aiurt.modules.weeklyplan.mapper.BdTeamMapper;
import com.aiurt.modules.weeklyplan.service.IBdSiteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;

import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 工区表，存储工区包含工作场所及对应工班信息
 * @Author: wgp
 * @Date: 2021-03-31
 * @Version: V1.0
 */
@Service
public class BdSiteServiceImpl extends ServiceImpl<BdSiteMapper, BdSite> implements IBdSiteService {

}
