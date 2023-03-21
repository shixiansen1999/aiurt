package com.aiurt.boot.strategy.mapper;

import com.aiurt.boot.plan.dto.StationDTO;
import com.aiurt.boot.strategy.entity.InspectionStrStaRel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: inspection_str_sta_rel
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface InspectionStrStaRelMapper extends BaseMapper<InspectionStrStaRel> {

    /**
     * 查询策略关联的编码
     * @param code
     * @return
     */
    List<StationDTO> selectStationList(String code);

    String selectDepartList(String code);

    String selectSystemList(String code);

    String selectMajorList(String code);
}
