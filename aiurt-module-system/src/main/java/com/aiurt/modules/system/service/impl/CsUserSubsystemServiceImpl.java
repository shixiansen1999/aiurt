package com.aiurt.modules.system.service.impl;

import com.aiurt.modules.system.entity.CsUserSubsystem;
import com.aiurt.modules.system.mapper.CsUserSubsystemMapper;
import com.aiurt.modules.system.service.ICsUserSubsystemService;
import org.jeecg.common.system.vo.CsUserSubsystemModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

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
}
