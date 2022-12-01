package com.aiurt.boot.team.service;

import com.aiurt.boot.team.dto.EmergencyTeamDTO;
import com.aiurt.boot.team.entity.EmergencyTeam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

/**
 * @Description: emergency_team
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyTeamService extends IService<EmergencyTeam> {

    /**
     * 根据code获取各类名称
     * @param emergencyTeam
     */
    void translate(EmergencyTeam emergencyTeam);

    /**
     * 获取应急队伍人员
     * @param emergencyTeam
     * @return
     */
    EmergencyTeam getCrew(EmergencyTeam emergencyTeam);

    /**
     * 应急队伍添加
     * @param emergencyTeam
     * @return
     */
    Result<String> add(EmergencyTeam emergencyTeam);
    /**
     * 应急队伍编辑
     * @param emergencyTeam
     * @return
     */
    Result<String> edit(EmergencyTeam emergencyTeam);
    /**
     * 应急队伍删除
     * @param id
     * @return
     */
    Result<String> delete(String id);

    /**
     * 应急队伍查询训练记录
     * @param id
     * @return
     */
    Result<EmergencyTeam> getTrainingRecordById(String id);

    IPage<EmergencyTeam> queryPageList(EmergencyTeamDTO emergencyTeamDTO, Integer pageNo, Integer pageSize);
}
