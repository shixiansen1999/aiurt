package com.aiurt.modules.system.service.impl;

import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.aiurt.modules.system.entity.CsUserSubsystem;
import com.aiurt.modules.system.mapper.CsUserSubsystemMapper;
import com.aiurt.modules.system.service.ICsUserSubsystemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.system.vo.CsUserSubsystemModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 用户子系统表
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
@Service
public class CsUserSubsystemServiceImpl extends ServiceImpl<CsUserSubsystemMapper, CsUserSubsystem> implements ICsUserSubsystemService {

    @Autowired
    private CsUserSubsystemMapper csUserSubsystemMapper;

    @Autowired
    private ICsSubsystemService subsystemService;

    /**
     * 根据用户id查询
     * @param id
     * @return
     */
    @Override
    public List<CsUserSubsystemModel> getSubsystemByUserId(String id) {
        List<CsUserSubsystemModel> subsystemModelList = csUserSubsystemMapper.getSubsystemByUserId(id);
        return subsystemModelList;
    }

    /**
     * 查询所有的子系统数据
     * @return
     */
    @Override
    public List<CsUserSubsystemModel> queryAllSubsystem() {
        LambdaQueryWrapper<CsSubsystem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CsSubsystem::getDelFlag, 0);

        List<CsSubsystem> list = subsystemService.list(wrapper);

        List<CsUserSubsystemModel> modelList = list.stream().map(subsystem -> {
            CsUserSubsystemModel csUserSubsystemModel = new CsUserSubsystemModel();
            csUserSubsystemModel.setSystemId(subsystem.getId());
            csUserSubsystemModel.setSystemName(subsystem.getSystemName());
            csUserSubsystemModel.setSystemCode(subsystem.getSystemCode());
            csUserSubsystemModel.setMajorCode(subsystem.getMajorCode());
            return csUserSubsystemModel;
        }).collect(Collectors.toList());
        return modelList;
    }
}
