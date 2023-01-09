package com.aiurt.boot.rehearsal.controller;

import com.aiurt.boot.plan.entity.EmergencyPlan;
import com.aiurt.boot.plan.service.IEmergencyPlanService;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalMonth;
import com.aiurt.boot.rehearsal.service.IEmergencyRehearsalMonthService;
import com.aiurt.boot.rehearsal.vo.EmergencyRehearsalMonthVO;
import com.aiurt.common.aspect.annotation.AutoLog;
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
import java.util.Optional;

/**
 * @Description: emergency_rehearsal_month
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
@Api(tags = "应急月演练计划")
@RestController
@RequestMapping("/emergency/emergencyRehearsalMonth")
@Slf4j
public class EmergencyRehearsalMonthController extends BaseController<EmergencyRehearsalMonth, IEmergencyRehearsalMonthService> {
    @Autowired
    private IEmergencyRehearsalMonthService emergencyRehearsalMonthService;
    @Autowired
    private IEmergencyPlanService emergencyPlanService;

    /**
     * 应急月演练计划-分页列表查询
     *
     * @param emergencyRehearsalMonth
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "应急月演练计划-分页列表查询")
    @ApiOperation(value = "应急月演练计划-分页列表查询", notes = "应急月演练计划-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<EmergencyRehearsalMonthVO>> queryPageList(EmergencyRehearsalMonth emergencyRehearsalMonth,
                                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                  HttpServletRequest req) {
        Page<EmergencyRehearsalMonthVO> page = new Page<EmergencyRehearsalMonthVO>(pageNo, pageSize);
        IPage<EmergencyRehearsalMonthVO> pageList = emergencyRehearsalMonthService.queryPageList(page, emergencyRehearsalMonth);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param emergencyRehearsalMonth
     * @return
     */
    @AutoLog(value = "应急月演练计划-添加")
    @ApiOperation(value = "应急月演练计划-添加", notes = "应急月演练计划-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody EmergencyRehearsalMonth emergencyRehearsalMonth) {
        String monthPlanId = emergencyRehearsalMonthService.addMonthPlan(emergencyRehearsalMonth);
        return Result.OK("添加成功！", monthPlanId);
    }

    /**
     * 应急月演练计划-编辑
     *
     * @param emergencyRehearsalMonth
     * @return
     */
    @AutoLog(value = "应急月演练计划-编辑")
    @ApiOperation(value = "应急月演练计划-编辑", notes = "应急月演练计划-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody EmergencyRehearsalMonth emergencyRehearsalMonth) {
        emergencyRehearsalMonthService.updateById(emergencyRehearsalMonth);
        return Result.OK("编辑成功!");
    }

    /**
     * 应急月演练计划-通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "应急月演练计划-通过id删除")
    @ApiOperation(value = "应急月演练计划-通过id删除", notes = "应急月演练计划-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        emergencyRehearsalMonthService.delete(id);
        return Result.OK("删除成功!");
    }


    /**
     * 应急月演练计划-通过id查询
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "应急月演练计划-通过id查询", notes = "应急月演练计划-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<EmergencyRehearsalMonth> queryById(@RequestParam(name = "id", required = true) String id) {
        EmergencyRehearsalMonth emergencyRehearsalMonth = emergencyRehearsalMonthService.getById(id);
        if (emergencyRehearsalMonth == null) {
            return Result.error("未找到对应数据");
        }
        Optional.ofNullable(emergencyRehearsalMonth.getSchemeId()).ifPresent(schemeId -> {
            EmergencyPlan emergencyPlan = emergencyPlanService.getById(schemeId);
            Optional.ofNullable(emergencyPlan).ifPresent(ep -> {
                emergencyRehearsalMonth.setSchemeName(ep.getEmergencyPlanName());
            });
        });
        return Result.OK(emergencyRehearsalMonth);
    }
//
//    /**
//     * 导出excel
//     *
//     * @param request
//     * @param emergencyRehearsalMonth
//     */
//    @RequestMapping(value = "/exportXls")
//    public ModelAndView exportXls(HttpServletRequest request, EmergencyRehearsalMonth emergencyRehearsalMonth) {
//        return super.exportXls(request, emergencyRehearsalMonth, EmergencyRehearsalMonth.class, "emergency_rehearsal_month");
//    }
//
//    /**
//     * 通过excel导入数据
//     *
//     * @param request
//     * @param response
//     * @return
//     */
//    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
//    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
//        return super.importExcel(request, response, EmergencyRehearsalMonth.class);
//    }

}
