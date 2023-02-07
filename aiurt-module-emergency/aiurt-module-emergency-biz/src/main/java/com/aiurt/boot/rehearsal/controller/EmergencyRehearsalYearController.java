package com.aiurt.boot.rehearsal.controller;

import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalYearDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalYear;
import com.aiurt.boot.rehearsal.service.IEmergencyRehearsalYearService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: emergency_rehearsal_year
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
@Api(tags = "应急演练管理年演练计划")
@RestController
@RequestMapping("/emergency/emergencyRehearsalYear")
@Slf4j
public class EmergencyRehearsalYearController extends BaseController<EmergencyRehearsalYear, IEmergencyRehearsalYearService> {
    @Autowired
    private IEmergencyRehearsalYearService emergencyRehearsalYearService;

    /**
     * 应急演练管理-年演练计划分页列表查询
     *
     * @param
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "应急演练管理-年演练计划分页列表查询")
    @ApiOperation(value = "应急演练管理-年演练计划分页列表查询", notes = "应急演练管理-年演练计划分页列表查询")
    @PermissionData(pageComponent = "emergency/emergencyExercise/annualExercisePlan")
    @GetMapping(value = "/list")
    public Result<IPage<EmergencyRehearsalYear>> queryPageList(EmergencyRehearsalYearDTO emergencyRehearsalYearDTO,
                                                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                               HttpServletRequest req) {
        Page<EmergencyRehearsalYear> page = new Page<EmergencyRehearsalYear>(pageNo, pageSize);
        IPage<EmergencyRehearsalYear> pageList = emergencyRehearsalYearService.queryPageList(page, emergencyRehearsalYearDTO);
        return Result.OK(pageList);
    }


    /**
     * 应急演练管理-年演练计划通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "应急演练管理-年演练计划通过id删除")
    @ApiOperation(value = "应急演练管理-年演练计划通过id删除", notes = "应急演练管理-年演练计划通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        emergencyRehearsalYearService.delete(id);
        return Result.OK("删除成功!");
    }

    /**
     * 应急演练管理-年演练计划通过id查询
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "应急演练管理-年演练计划通过id查询", notes = "应急演练管理-年演练计划通过id查询")
    @GetMapping(value = "/queryById")
    public Result<EmergencyRehearsalYear> queryById(@RequestParam(name = "id", required = true) String id) {
        EmergencyRehearsalYear emergencyRehearsalYear = emergencyRehearsalYearService.getById(id);
        if (emergencyRehearsalYear == null) {
            return Result.error("未找到对应数据!");
        }
        return Result.OK(emergencyRehearsalYear);
    }

    /**
     * 应急演练管理-导出年演练计划excel
     */
    @ApiOperation(value = "应急演练管理-导出年演练计划excel", notes = "应急演练管理-导出年演练计划excel")
    @RequestMapping(value = "/exportXls")
    public void exportXls(HttpServletRequest request, HttpServletResponse response, String ids ,String orgCode) {
        emergencyRehearsalYearService.exportXls(request, response, ids,orgCode);
//        return super.exportXls(request, emergencyRehearsalYear, EmergencyRehearsalYear.class, "emergency_rehearsal_year");
    }
//
//    /**
//      * 通过excel导入数据
//    *
//    * @param request
//    * @param response
//    * @return
//    */
//    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
//    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
//        return super.importExcel(request, response, EmergencyRehearsalYear.class);
//    }

}
