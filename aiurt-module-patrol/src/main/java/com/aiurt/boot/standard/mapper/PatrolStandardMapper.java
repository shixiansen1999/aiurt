package com.aiurt.boot.standard.mapper;

import com.aiurt.boot.entity.patrol.standard.PatrolStandard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

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
    List<PatrolStandard> pageList (Page page, PatrolStandard patrolStandard);

}
