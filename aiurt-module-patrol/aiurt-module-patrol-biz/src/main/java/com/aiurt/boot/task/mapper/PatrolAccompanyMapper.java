package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.dto.PatrolAccompanyDTO;
import com.aiurt.boot.task.entity.PatrolAccompany;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: patrol_accompany
 * @Author: aiurt
 * @Date:   2022-06-28
 * @Version: V1.0
 */
public interface PatrolAccompanyMapper extends BaseMapper<PatrolAccompany> {

    /**
     * app-获取同行人名称
     * @param patrolNumber
     * @return
     */
    List<PatrolAccompanyDTO> getAccompanyName(String patrolNumber);
}
