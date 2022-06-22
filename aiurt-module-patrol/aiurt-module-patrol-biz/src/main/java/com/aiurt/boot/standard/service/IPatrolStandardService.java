package com.aiurt.boot.standard.service;

import com.aiurt.boot.standard.dto.InspectionStandardDto;
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
    IPage<PatrolStandard> pageList (Page page, PatrolStandard patrolStandard);

    /**
     * 获取专业
     * @param professionCode
     * @param subsystemCode
     * @return
     */
    List<InspectionStandardDto> lists(String professionCode, String subsystemCode);

}
