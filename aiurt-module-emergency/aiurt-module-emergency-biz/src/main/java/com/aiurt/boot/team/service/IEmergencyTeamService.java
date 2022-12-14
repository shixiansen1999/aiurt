package com.aiurt.boot.team.service;

import com.aiurt.boot.team.dto.EmergencyTeamDTO;
import com.aiurt.boot.team.entity.EmergencyTeam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

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
     * @param emergencyTeam
     * @return
     */
    void delete(EmergencyTeam emergencyTeam );

    /**
     * 应急队伍查询训练记录
     * @param id
     * @return
     */
    Result<EmergencyTeam> getTrainingRecordById(String id);

    /**
     * 查询列表
     * @param emergencyTeamDTO
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<EmergencyTeam> queryPageList(EmergencyTeamDTO emergencyTeamDTO, Integer pageNo, Integer pageSize);
    /**
     * 根据部门查询队伍
     * @param orgCode
     * @return
     */
    Result<List<EmergencyTeam>> getTeamByCode(String orgCode);
    /**
     * 根据专业权限查找应急队伍
     * @param
     * @return
     */
    Result<List<EmergencyTeam>> getTeamByMajor();

    /**
     * 导入
     * @param request
     * @param response
     * @return
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response);
    /**
     * 模板下载
     * @param response
     * @throws IOException
     */
    void exportTemplateXls(HttpServletResponse response) throws IOException;
    /**
     * 应急队伍导出
     * @param request
     * @param emergencyTeamDTO
     * @return
     */
    ModelAndView exportTeamXls(HttpServletRequest request, EmergencyTeamDTO emergencyTeamDTO);
    /**
     * 应急队伍人员导出excel
     * @param request
     * @param id
     * @return
     */
    ModelAndView exportCrewXls(HttpServletRequest request, String id);
}
