package com.aiurt.modules.system.service;

import com.aiurt.modules.system.entity.CsUserMajor;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.system.vo.CsUserMajorModel;

import java.util.List;

/**
 * @Description: 用户专业表
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
public interface ICsUserMajorService extends IService<CsUserMajor> {
    /**
     * 根据用户id获取专业权限
     * @param id
     * @return
     */
    List<CsUserMajorModel> getMajorByUserId(String id);

    /**
     * 查询所有的专业
     * @return
     */
    List<CsUserMajorModel> queryAllMojor();
}
