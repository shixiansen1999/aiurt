package com.aiurt.boot.overhaulstatistics.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.task.dto.OverhaulStatisticsDTO;
import com.aiurt.boot.task.mapper.RepairTaskMapper;
import com.aiurt.modules.fault.constants.FaultConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zwl
 * @Title:
 * @Description: 检修统计分析业务层
 * @date 2022/9/2011:14
 */
@Service
public class OverhaulStatisticsService{

    @Autowired
    private RepairTaskMapper repairTaskMapper;

    @Resource
    private InspectionManager manager;

    public List<OverhaulStatisticsDTO> getOverhaulList(OverhaulStatisticsDTO condition) {
        //查询班组的信息
        List<OverhaulStatisticsDTO> statisticsDTOList = repairTaskMapper.readTeamList(condition);

        //查询人员信息
        List<OverhaulStatisticsDTO> nameList = repairTaskMapper.readNameList(condition);
        if (CollectionUtil.isNotEmpty(nameList)){
            nameList.forEach(q->{
                //查询已完成的人员信息
                condition.setStatus(8L);
                condition.setTaskId(q.getTaskId());
                List<OverhaulStatisticsDTO> readNameList = repairTaskMapper.readNameList(condition);

                //已完成数
                int size5 = readNameList.size();
                q.setCompletedNumber(Integer.valueOf(size5).longValue());

                //未完成数
                long l = q.getTaskTotal()-Integer.valueOf(size5).longValue();
                q.setNotCompletedNumber(l);

                //完成率
                getCompletionRate(q, size5);

                //异常数量
                List<Integer> status1 = repairTaskMapper.getStatus(q.getTaskId());
                long count = CollUtil.isNotEmpty(status1) ? status1.stream().filter(InspectionConstant.NO_RESULT_STATUS::equals).count() : 0L;
                q.setAbnormalNumber(count);

                //姓名
                String userId = q.getUserId();
                q.setUserName(repairTaskMapper.getRealName(userId));

                //班组编码
                String orgCode = repairTaskMapper.getOrgCode(userId);
                q.setOrgCode(orgCode);
            });
        }
        if (CollectionUtil.isNotEmpty(statisticsDTOList)){
            statisticsDTOList.forEach(e->{
                //查询已完成的班组信息
                condition.setStatus(8L);
                condition.setTaskId(e.getTaskId());
                List<OverhaulStatisticsDTO> dtoList = repairTaskMapper.readTeamList(condition);

                //已完成数
                int size2 = dtoList.size();
                e.setCompletedNumber(Integer.valueOf(size2).longValue());

                //未完成数
                long l = e.getTaskTotal()-Integer.valueOf(size2).longValue();
                e.setNotCompletedNumber(l);

                //班组名称
                e.setOrgName(manager.translateOrg(Arrays.asList(e.getOrgCode())));

                //完成率
                getCompletionRate(e, size2);

                //异常数量
                List<Integer> status = repairTaskMapper.getStatus(e.getTaskId());
                long count = CollUtil.isNotEmpty(status) ? status.stream().filter(InspectionConstant.NO_RESULT_STATUS::equals).count() : 0L;
                e.setAbnormalNumber(count);

                //人员是否属于该班组
                List<OverhaulStatisticsDTO> collect = nameList.stream().filter(y -> y.getOrgCode().equals(e.getOrgCode())).collect(Collectors.toList());
                e.setNameList(collect);
            });
        }
        return statisticsDTOList;
    }

    private void getCompletionRate(OverhaulStatisticsDTO e, int size2) {
        double div = NumberUtil.div(size2, e.getTaskTotal().longValue());
        double i = div*100;
        String string = NumberUtil.round(i, 0).toString();
        e.setCompletionRate(string+"%");
        e.setLeakOverhaulNumber(0L);
        e.setAvgWeekNumber(0L);
        e.setAvgMonthNumber(0L);
    }

}
