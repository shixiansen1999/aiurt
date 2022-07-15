package com.aiurt.modules.system.service.impl;

import com.aiurt.modules.system.entity.CsUserDepart;
import com.aiurt.modules.system.mapper.CsUserDepartMapper;
import com.aiurt.modules.system.service.ICsUserDepartService;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 用户部门权限表
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
@Service
public class CsUserDepartServiceImpl extends ServiceImpl<CsUserDepartMapper, CsUserDepart> implements ICsUserDepartService {
    @Autowired
    private CsUserDepartMapper csUserDepartMapper;

    @Override
    public List<CsUserDepartModel> getDepartByUserId(String id) {
        List<CsUserDepartModel> departByUserId = csUserDepartMapper.getDepartByUserId(id);
        return departByUserId;
    }
}
