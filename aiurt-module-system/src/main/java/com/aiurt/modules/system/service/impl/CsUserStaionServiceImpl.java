package com.aiurt.modules.system.service.impl;

import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.service.ICsStationService;
import com.aiurt.modules.system.entity.CsUserStaion;
import com.aiurt.modules.system.mapper.CsUserStaionMapper;
import com.aiurt.modules.system.service.ICsUserStaionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.system.vo.CsUserStationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 用户站点表
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
@Service
public class CsUserStaionServiceImpl extends ServiceImpl<CsUserStaionMapper, CsUserStaion> implements ICsUserStaionService {

    @Autowired
    private ICsStationService csStationService;

    @Override
    public List<CsUserStationModel> getStationByUserId(String id) {
        return baseMapper.getStationByUserId(id);
    }

    @Override
    public List<CsUserStationModel> queryAllStation() {
        LambdaQueryWrapper<CsStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CsStation::getDelFlag, 0);

        List<CsStation> list = csStationService.list(wrapper);

        List<CsUserStationModel> modelList = list.stream().map(csStation -> {
            CsUserStationModel csUserStationModel = new CsUserStationModel();
            csUserStationModel.setStationId(csStation.getId());
            csUserStationModel.setStationName(csStation.getStationName());
            csUserStationModel.setStationCode(csStation.getStationCode());
            csUserStationModel.setLineCode(csStation.getLineCode());
            return csUserStationModel;
        }).collect(Collectors.toList());
        return modelList;
    }
}
