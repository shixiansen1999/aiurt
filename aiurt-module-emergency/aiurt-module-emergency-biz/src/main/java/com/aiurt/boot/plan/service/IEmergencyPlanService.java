package com.aiurt.boot.plan.service;

import com.aiurt.boot.plan.dto.EmergencyPlanDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanQueryDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanRecordDTO;
import com.aiurt.boot.plan.entity.EmergencyPlan;
import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalYearDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalYear;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Description: emergency_plan
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyPlanService extends IService<EmergencyPlan> {
    /**
     * 应急预案列表查询
     * @param page
     * @param emergencyPlanQueryDto
     * @return
     */
    IPage<EmergencyPlan> queryPageList(Page<EmergencyPlan> page, EmergencyPlanQueryDTO emergencyPlanQueryDto);

    /**
     * 应急预案列表审核
     * @param page
     * @param emergencyPlanQueryDto
     * @return
     */
    IPage<EmergencyPlan> queryWorkToDo(Page<EmergencyPlan> page, EmergencyPlanQueryDTO emergencyPlanQueryDto);


    /**
     * 保存并添加
     * @param emergencyPlanDto
     * @return
     */
    String saveAndAdd(EmergencyPlanDTO emergencyPlanDto);

    /**
     * 编辑
     * @param emergencyPlanDto
     * @return
     */
    String edit(EmergencyPlanDTO emergencyPlanDto);

    /**
     * 变更
     * @param emergencyPlanDto
     * @return
     */
    EmergencyPlanDTO change(EmergencyPlanDTO emergencyPlanDto);

    /**
     * 删除
     * @param id
     * @return
     */
    void delete(String id);

    /**
     * 应急预案台账提交
     * @param id
     * @return
     */
    String commit(String id);

    /**
     * 应急预案台账启用和停用
     * @param id
     * @return
     */
    String openOrStop(String id);


    /**
     * 应急预案台账通过id查询
     * @param id
     * @return
     */
    EmergencyPlanDTO queryById(String id);

    /**
     * 应急预案导出数据
     * @param response
     * @param id
     */
    void exportXls( HttpServletResponse response, String id);

    /**
     * 应急预案台账导入
     * @param request
     * @param response
     * @return
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response);

    /**
     * 应急预案台账模板下载
     * @param response
     * @throws IOException
     */
    void exportTemplateXls(HttpServletResponse response,HttpServletRequest request) throws IOException;
}
