package com.aiurt.boot.api;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.task.mapper.PatrolTaskUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.vo.PortraitTaskModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PersonnelPortraitPatrolApiImpl implements PersonnelPortraitPatrolApi {

    @Autowired
    private PatrolTaskUserMapper patrolTaskUserMapper;

    @Override
    public Map<Integer, Long> getPatrolTaskNumber(String userId, int flagYearAgo, int thisYear) {
        Map<Integer, Long> map = new HashMap<>(8);
        List<PortraitTaskModel> list = patrolTaskUserMapper.getPatrolTaskNumber(userId, flagYearAgo, thisYear, PatrolConstant.TASK_COMPLETE);
        if (CollUtil.isNotEmpty(list)) {
            for (PortraitTaskModel portraitPatrol : list) {
                map.put(portraitPatrol.getYear(), portraitPatrol.getNumber());
            }
        }
        return map;
    }
}
