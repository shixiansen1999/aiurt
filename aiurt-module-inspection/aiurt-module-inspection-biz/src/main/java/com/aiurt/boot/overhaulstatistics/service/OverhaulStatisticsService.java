package com.aiurt.boot.overhaulstatistics.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.task.dto.OverhaulStatisticsDTO;
import com.aiurt.boot.task.mapper.RepairTaskMapper;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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

    @Resource
    private InspectionManager manager;

    public List<OverhaulStatisticsDTO> getOverhaulList(OverhaulStatisticsDTO condition) {
        //查询班组的信息
        List<OverhaulStatisticsDTO> statisticsDTOList = repairTaskMapper.readTeamList(condition);

        //查询人员信息
        List<OverhaulStatisticsDTO> nameList = repairTaskMapper.readNameList(condition);

        if (CollectionUtil.isNotEmpty(statisticsDTOList)){
            statisticsDTOList.forEach(e->{
                //查询已完成的班组信息
                condition.setStatus(8L);
                condition.setTaskId(e.getTaskId());
                List<OverhaulStatisticsDTO> dtoList = repairTaskMapper.readTeamList(condition);

                if (CollectionUtil.isNotEmpty(dtoList)){
                //已完成数
                int size2 = dtoList.size();
                //未完成数
                long l = e.getTaskTotal()-Integer.valueOf(size2).longValue();

                List<Integer> status = repairTaskMapper.getStatus(e.getTaskId());
                e.setAbnormalNumber(Integer.valueOf(status.size()).longValue());
                e.setCompletedNumber(Integer.valueOf(size2).longValue());
                e.setNotCompletedNumber(l);
                    e.setOrgName(manager.translateOrg(Arrays.asList(e.getOrgCode())));

                double div = NumberUtil.div(size2, e.getTaskTotal().longValue());
                double i = div*100;
                String string = NumberUtil.round(i, 0).toString();
                e.setCompletionRate(string+"%");
                }
            });
        }
        if (CollectionUtil.isNotEmpty(nameList)){
            nameList.forEach(q->{
                //查询已完成的人员信息
                condition.setStatus(8L);
                condition.setTaskId(q.getTaskId());
                List<OverhaulStatisticsDTO> readNameList = repairTaskMapper.readNameList(condition);

                if (CollectionUtil.isNotEmpty(readNameList)) {
                    //已完成数
                    int size5 = readNameList.size();

                    //未完成数
                    long l = q.getTaskTotal()-Integer.valueOf(size5).longValue();

                    List<Integer> status1 = repairTaskMapper.getStatus(q.getTaskId());
                    q.setAbnormalNumber(Integer.valueOf(status1.size()).longValue());
                    q.setCompletedNumber(Integer.valueOf(size5).longValue());
                    q.setNotCompletedNumber(l);

                    double div1 = NumberUtil.div(size5, q.getTaskTotal().longValue());
                    double i = div1 * 100;
                    String string1 = NumberUtil.round(i, 0).toString();
                    q.setCompletionRate(string1 + "%");

                    String userId = q.getUserId();
                    q.setUserName(repairTaskMapper.getOrgName(userId));
                    String orgCode = repairTaskMapper.getOrgCode(userId);
                    statisticsDTOList.forEach(e->{
                        String orgCode1 = e.getOrgCode();
                        if (orgCode1.equals(orgCode)){
                            e.setNameList(nameList);
                        }
                    });
                }
            });
        }
        return statisticsDTOList;
    }
}
