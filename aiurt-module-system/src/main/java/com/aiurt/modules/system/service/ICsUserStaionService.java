package com.aiurt.modules.system.service;

import com.aiurt.modules.system.entity.CsUserStaion;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.system.vo.CsUserStationModel;

import java.util.List;

/**
 * @Description: 用户站点表
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
public interface ICsUserStaionService extends IService<CsUserStaion> {

    /**
     * 根据用户id获取站点
     *
     * @param id
     * @return
     */
    List<CsUserStationModel> getStationByUserId(String id);

    /**
     * 获取所有站点
     * @return
     */
    List<CsUserStationModel> queryAllStation();
}
