package com.aiurt.boot.standard.service;

import com.aiurt.boot.standard.dto.InspectionStandardDto;
import com.aiurt.boot.standard.dto.PatrolStandardDto;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: patrol_standard
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface IPatrolStandardService extends IService<PatrolStandard> {
    /**
     * 分页查询
     * @param page
     * @param patrolStandard
     * @return
     */
    IPage<PatrolStandardDto> pageList (Page page, PatrolStandard patrolStandard);
    /**
     * 分页列表查询配置巡检项的表
     * @param page
     * @param patrolStandard
     * @return
     */
    IPage<PatrolStandardDto> pageLists (Page page, PatrolStandardDto patrolStandard);

    /**
     * 获取专业
     * @param professionCode
     * @param subsystemCode
     * @return
     */
    List<InspectionStandardDto> lists(String professionCode, String subsystemCode);

}
