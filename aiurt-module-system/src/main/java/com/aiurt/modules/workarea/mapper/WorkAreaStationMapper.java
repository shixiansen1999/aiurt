package com.aiurt.modules.workarea.mapper;

import com.aiurt.modules.workarea.entity.WorkAreaStation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: work_area_station
 * @Author: aiurt
 * @Date:   2022-08-11
 * @Version: V1.0
 */
public interface WorkAreaStationMapper extends BaseMapper<WorkAreaStation> {

    /**
     * 根据工区编码，获取站点名称
     * @param code
     * @return
     */
    List<String> getLineStationName(String code);
}
