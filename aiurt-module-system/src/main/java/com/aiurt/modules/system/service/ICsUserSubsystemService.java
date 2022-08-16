package com.aiurt.modules.system.service;

import com.aiurt.modules.system.entity.CsUserSubsystem;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.CsUserSubsystemModel;

import java.util.List;

/**
 * @Description: 用户子系统表
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
public interface ICsUserSubsystemService extends IService<CsUserSubsystem> {

    /**
     * 根据用户id获取站点
     * @param id
     * @return
     */
    List<CsUserSubsystemModel> getSubsystemByUserId(String id);

}
