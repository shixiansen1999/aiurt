package com.aiurt.modules.system.service.impl;

import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.system.entity.CsUserMajor;
import com.aiurt.modules.system.mapper.CsUserMajorMapper;
import com.aiurt.modules.system.service.ICsUserMajorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private ICsMajorService majorService;

    @Override
    public List<CsUserMajorModel> getMajorByUserId(String id) {
        List<CsUserMajorModel> majorByUserId = csUserMajorMapper.getMajorByUserId(id);
        return majorByUserId;
    }

    /**
     *  查询所有的专业
     * @return
     */
    @Override
    public List<CsUserMajorModel> queryAllMojor() {
        LambdaQueryWrapper<CsMajor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CsMajor::getDelFlag, 0);
        List<CsMajor> list = majorService.list(wrapper);

        List<CsUserMajorModel> majorModelList = list.stream().map(csMajor -> {
            CsUserMajorModel csUserMajorModel = new CsUserMajorModel();


            csUserMajorModel.setMajorCode(csMajor.getMajorCode());
            csUserMajorModel.setMajorName(csMajor.getMajorName());

            csUserMajorModel.setMajorId(csMajor.getId());
            return csUserMajorModel;
        }).collect(Collectors.toList());

        return majorModelList;
    }
}
