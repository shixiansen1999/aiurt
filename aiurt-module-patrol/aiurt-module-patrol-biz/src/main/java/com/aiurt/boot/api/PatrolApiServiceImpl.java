package com.aiurt.boot.api;

import cn.hutool.core.date.DateUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.dto.PatrolWorkLogDTO;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.mapper.PatrolTaskDeviceMapper;
import com.aiurt.boot.task.mapper.PatrolTaskMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PatrolApiServiceImpl implements PatrolApi {

    @Autowired
    private PatrolTaskMapper patrolTaskMapper;
    @Autowired
    private PatrolTaskDeviceMapper patrolTaskDeviceMapper;

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

    @Override
    public String getUserTask() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //获取当前登录人的全部任务
        List<PatrolTask> patrolTasks = patrolTaskMapper.getUserTask(sysUser.getId(), new Date());
        List<PatrolWorkLogDTO> dtoList = new ArrayList<>();
        List<String> list = new ArrayList<>();
        for (PatrolTask task : patrolTasks) {
            //获取当前用户，在这个任务下，提交的所有工单
            List<PatrolTaskDevice> devices = patrolTaskDeviceMapper.selectList(new LambdaQueryWrapper<PatrolTaskDevice>().
                    eq(PatrolTaskDevice::getTaskId, task.getId()).eq(PatrolTaskDevice::getUserId, sysUser.getId()).eq(PatrolTaskDevice::getStatus, 2));
            //获取这个任务下的工单所对应的站点
            for (PatrolTaskDevice patrolTaskDevice : devices) {
                PatrolWorkLogDTO dto = new PatrolWorkLogDTO();
                String stationName = patrolTaskDeviceMapper.getLineStationName(patrolTaskDevice.getStationCode());
                PatrolStandard standardName = patrolTaskDeviceMapper.getStandardName(patrolTaskDevice.getId());
                String submitName = patrolTaskDeviceMapper.getSubmitName(patrolTaskDevice.getUserId());
                String deviceStationName = standardName+"-"+stationName+" 巡视人："+submitName+"。";
                list.add(deviceStationName);
                dto.setPatrolTaskTable(standardName.getName());
                dto.setStation(stationName);
                dto.setName(submitName);
                dtoList.add(dto);
            }

        }
        return list.toString();
    }

}
