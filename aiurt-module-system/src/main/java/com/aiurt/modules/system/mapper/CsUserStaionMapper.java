package com.aiurt.modules.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.aiurt.modules.system.entity.CsUserStaion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.common.system.vo.CsUserStationModel;

/**
 * @Description: 用户站点表
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
public interface CsUserStaionMapper extends BaseMapper<CsUserStaion> {
    List<String> getStaionIds(@Param("userId") String userId);

    /**
     * 根据用户id获取站点
     *
     * @param id
     * @return
     */
    List<CsUserStationModel> getStationByUserId(String id);
}
