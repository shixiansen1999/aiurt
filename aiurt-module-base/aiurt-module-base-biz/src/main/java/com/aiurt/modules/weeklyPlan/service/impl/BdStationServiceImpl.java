package com.aiurt.modules.weeklyplan.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.weeklyplan.entity.BdStation;
import com.aiurt.modules.weeklyplan.mapper.BdStationMapper;
import com.aiurt.modules.weeklyplan.service.IBdStationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.TokenUtils;
import org.jeecg.common.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 工作场所表，存储用户工作场所信息
 * @Author: wgp
 * @Date: 2021-03-29
 * @Version: V1.0
 */
@Service
public class BdStationServiceImpl extends ServiceImpl<BdStationMapper, BdStation> implements IBdStationService {

}
