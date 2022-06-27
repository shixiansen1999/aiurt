package com.aiurt.boot.standard.mapper;

import com.aiurt.boot.standard.dto.InspectionStandardDto;
import com.aiurt.boot.standard.dto.PatrolStandardDto;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: patrol_standard
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface PatrolStandardMapper extends BaseMapper<PatrolStandard> {
    /**
     * 分页查询
     * @param page
     * @param patrolStandard
     * @return
     */
    List<PatrolStandardDto> pageList (@Param("page") Page page, @Param("patrolStandard") PatrolStandard patrolStandard);

    /**
     * 获取分页
     * @param professionCode
     * @return
     */
    List<InspectionStandardDto> list(@Param("professionCode")String professionCode, @Param("subsystemCode") String subsystemCode);
}
