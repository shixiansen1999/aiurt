package com.aiurt.boot.api;

import cn.hutool.core.date.DateUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.mapper.PatrolTaskMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PatrolApiServiceImpl implements PatrolApi {

    @Autowired
    private PatrolTaskMapper patrolTaskMapper;

    /**
     * 首页-统计日程的巡视完成数
     *
     * @param year
     * @param month
     * @return
     */
    @Override
    public Map<String, Integer> getPatrolFinishNumber(int year, int month) {
        Map<String, Integer> map = new HashMap<>();
        Calendar instance = Calendar.getInstance();
        instance.set(year, month - 1, 1);
        // 所在月的第一天
        Date firstDay = DateUtil.parse(DateUtil.format(instance.getTime(), "yyyy-MM-dd 00:00:00"));
        instance.set(Calendar.DAY_OF_MONTH, instance.getActualMaximum(Calendar.DAY_OF_MONTH));
        // 所在月的最后一天
        Date lastDay = DateUtil.parse(DateUtil.format(instance.getTime(), "yyyy-MM-dd 23:59:59"));
        QueryWrapper<PatrolTask> taskWrapper = new QueryWrapper<>();
        taskWrapper.lambda().eq(PatrolTask::getDelFlag, 0)
                .eq(PatrolTask::getStatus, PatrolConstant.TASK_COMPLETE)
                .between(PatrolTask::getPatrolDate, firstDay, lastDay);
        List<PatrolTask> taskList = patrolTaskMapper.selectList(taskWrapper);

        instance.set(year, month - 1, 1);
        while (instance.get(Calendar.MONTH) == month - 1) {
            String date = DateUtil.format(instance.getTime(), "yyyy/MM/dd");
            int count = (int) taskList.stream().filter(l -> date.equals(DateUtil.format(l.getPatrolDate(), "yyyy/MM/dd"))).count();
            map.put(date, count);
            instance.add(Calendar.DATE, 1);
        }
        return map;
    }

}
