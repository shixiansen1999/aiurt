package com.aiurt.modules.system.service;

import com.aiurt.modules.system.entity.CsUserDepart;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.system.vo.CsUserDepartModel;

import java.util.List;

/**
 * @Description: 用户部门权限表
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
public interface ICsUserDepartService extends IService<CsUserDepart> {
    /**
     * 根据用户id获取部门权限
     * @param id
     * @return
     */
    List<CsUserDepartModel> getDepartByUserId(String id);
}
