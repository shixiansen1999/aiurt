package com.aiurt.modules.weeklyPlan.service.impl;

import cn.hutool.core.convert.Convert;
import com.aiurt.modules.weeklyPlan.entity.BdLine;
import com.aiurt.modules.weeklyPlan.mapper.BdLineMapper;
import com.aiurt.modules.weeklyPlan.service.IBdLineService;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: bd_line
 * @Author: wgp
 * @Date:   2021-03-26
 * @Version: V1.0
 */
@Service
public class BdLineServiceImpl extends ServiceImpl<BdLineMapper, BdLine> implements IBdLineService {

    @Autowired
    private BdLineMapper bdLineMapper;
    @Autowired
    private ISysBaseAPI sysBaseAPI;

}
