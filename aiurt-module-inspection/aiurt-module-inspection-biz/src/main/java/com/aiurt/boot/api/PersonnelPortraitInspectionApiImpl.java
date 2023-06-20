package com.aiurt.boot.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.aiurt.boot.constant.InspectionConstant;
import org.jeecg.common.system.vo.PortraitTaskModel;
import com.aiurt.boot.task.mapper.RepairTaskUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 * @description: 报表统计-人员画像
 */
@Slf4j
@Service
public class PersonnelPortraitInspectionApiImpl implements PersonnelPortraitInspectionApi {

    @Autowired
    private RepairTaskUserMapper repairTaskUserMapper;

    @Override
    public Map<Integer, Long> getInspectionNumber(String userId, int flagYearAgo, int thisYear) {
        Map<Integer, Long> map = new HashMap<>(8);
        List<PortraitTaskModel> list = repairTaskUserMapper.getInspectionNumber(userId, flagYearAgo, thisYear, InspectionConstant.COMPLETED);
        if (CollUtil.isNotEmpty(list)) {
            for (PortraitTaskModel portraitTask : list) {
                map.put(portraitTask.getYear(), portraitTask.getNumber());
            }
        }
        return map;
    }
}
