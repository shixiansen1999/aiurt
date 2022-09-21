package com.aiurt.boot.overhaulstatistics.service;


import com.aiurt.boot.task.dto.OverhaulStatisticsDTO;
import com.aiurt.boot.task.mapper.RepairTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zwl
 * @Title:
 * @Description: 检修统计分析业务层
 * @date 2022/9/2011:14
 */
@Service
public class OverhaulStatisticsService {

    @Autowired
    private RepairTaskMapper repairTaskMapper;

    public List<OverhaulStatisticsDTO> getOverhaulList(OverhaulStatisticsDTO condition) {
        repairTaskMapper.readTeamList(condition);
        return null;
    }
}
