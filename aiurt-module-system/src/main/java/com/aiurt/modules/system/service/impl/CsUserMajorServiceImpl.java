package com.aiurt.modules.system.service.impl;

import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.system.entity.CsUserMajor;
import com.aiurt.modules.system.mapper.CsUserMajorMapper;
import com.aiurt.modules.system.service.ICsUserMajorService;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 用户专业表
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
@Service
public class CsUserMajorServiceImpl extends ServiceImpl<CsUserMajorMapper, CsUserMajor> implements ICsUserMajorService {
    @Autowired
    private CsUserMajorMapper csUserMajorMapper;

    @Override
    public List<CsUserMajorModel> getMajorByUserId(String id) {
        List<CsUserMajorModel> majorByUserId = csUserMajorMapper.getMajorByUserId(id);
        return majorByUserId;
    }
}
