package com.aiurt.boot.standard.service;

import com.aiurt.boot.standard.dto.InspectionStandardDto;
import com.aiurt.boot.standard.dto.PatrolStandardDto;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Description: patrol_standard
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface IPatrolStandardService extends IService<PatrolStandard> {
    /**
     * 分页查询
     * @param page
     * @param patrolStandard
     * @return
     */
    IPage<PatrolStandardDto> pageList (Page page, PatrolStandard patrolStandard);
    /**
     * 分页列表查询配置巡检项的表
     * @param page
     * @param patrolStandard
     * @return
     */
    IPage<PatrolStandardDto> pageLists (Page page, PatrolStandardDto patrolStandard);

    /**
     * 获取专业
     * @param professionCode
     * @param subsystemCode
     * @return
     */
    List<InspectionStandardDto> lists(String professionCode, String subsystemCode);

    /**
     * 巡检标准导出
     * @param request
     * @param response
     * @param patrolStandard
     * @return
     */
    void exportXls(HttpServletRequest request, HttpServletResponse response, PatrolStandard patrolStandard);

    /**
     * 巡检标准导入
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 巡检标准模板导出
     * @param response
     * @param request
     * @throws IOException
     */
    void getImportTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException;

    /**
     * @param patrolStandardDto
     */
    void getDeviceTypeName(PatrolStandardDto patrolStandardDto);
}
