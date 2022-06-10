package com.aiurt.boot.modules.repairManage.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.system.vo.LoginUser;
import com.swsc.copsms.common.util.oConvertUtils;
import com.swsc.copsms.modules.repairManage.entity.RepairTask;
import com.swsc.copsms.modules.repairManage.service.IRepairTaskService;
import com.swsc.copsms.modules.repairManage.vo.ReTaskDetailVO;
import com.swsc.copsms.modules.standardManage.inspectionStrategy.entity.InspectionCodeContent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * @Description: 检修单列表
 * @Author: qian
 * @Date: 2021-09-16
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "检修单列表")
@RestController
@RequestMapping("/repairManage/repairTask")
public class RepairTaskController {
    @Autowired
    private IRepairTaskService repairTaskService;

    /**
     * 分页列表查询
     *
     * @param repairTask
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "检修单列表-分页列表查询")
    @ApiOperation(value = "检修单列表-分页列表查询", notes = "检修单列表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<RepairTask>> queryPageList(RepairTask repairTask,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                   HttpServletRequest req) {
        Result<IPage<RepairTask>> result = new Result<IPage<RepairTask>>();
        QueryWrapper<RepairTask> queryWrapper = QueryGenerator.initQueryWrapper(repairTask, req.getParameterMap());
        Page<RepairTask> page = new Page<RepairTask>(pageNo, pageSize);
        IPage<RepairTask> pageList = repairTaskService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 通过id确认
     *
     * @param id
     * @return
     */
    @AutoLog(value = "检修单列表-通过id确认")
    @ApiOperation(value = "检修单列表-通过id确认", notes = "检修单列表-通过id确认")
    @GetMapping(value = "/confirmById")
    public Result confirmById(@RequestParam(name = "id") String id,
                              @RequestParam(name = "confirmStatus") Integer confirmStatus,
                              @RequestParam(name = "errorContent", required = false) String errorContent) {
        return repairTaskService.confirmById(id, confirmStatus, errorContent);
    }

    /**
     * 通过id验收
     *
     * @param id
     * @return
     */
    @AutoLog(value = "检修单列表-通过id验收")
    @ApiOperation(value = "检修单列表-通过id验收", notes = "检修单列表-通过id验收")
    @GetMapping(value = "/receiptById")
    public Result receiptById(@RequestParam(name = "id") String id,
                              @RequestParam(name = "receiptStatus") Integer receiptStatus,
                              @RequestParam(name = "errorContent", required = false) String errorContent) {
        return repairTaskService.checkById(id, receiptStatus, errorContent);
    }


    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "检修单详情-通过id查询")
    @ApiOperation(value = "检修单详情-通过id查询", notes = "检修单详情-通过id查询")
    @GetMapping(value = "/queryDetailById")
    public Result<ReTaskDetailVO> queryDetailById(@RequestParam(name = "id", required = true) String id) {
        return repairTaskService.queryDetailById(id);
    }


    /**
     * 导出excel
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
        // Step.1 组装查询条件
        QueryWrapper<RepairTask> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                RepairTask repairTask = JSON.parseObject(deString, RepairTask.class);
                queryWrapper = QueryGenerator.initQueryWrapper(repairTask, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<RepairTask> pageList = repairTaskService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "检修单列表");
        mv.addObject(NormalExcelConstants.CLASS, InspectionCodeContent.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("检修单列表数据", "导出人:Jeecg", "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 获取当前用户的检修工单详情
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @AutoLog(value = "app-检修工单列表")
    @ApiOperation(value = "app-获取当前用户检修列表", notes = "app-获取当前用户检修列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime",value = "开始时间,2020-10-01"),
            @ApiImplicitParam(name = "endTime",value = "结束时间,2020-10-07")

    })
    @GetMapping(value = "/getDetailByUser")
    public Result<ReTaskDetailVO> getDetailByUser(@RequestParam(name = "startTime") String startTime,
                                                  @RequestParam(name = "endTime") String endTime) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        return repairTaskService.getDetailByUser(user, startTime, endTime);
    }

    /**
     * 领取检修工单
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "app-检修工单领取")
    @ApiOperation(value = "app-检修工单领取", notes = "app-检修工单领取")
    @ApiImplicitParam(name = "ids",value = "getDetailByUser接口中返回的RepairPoolList中id集合，按逗号连接")
    @GetMapping(value = "/receiveByUser")
    public Result receiveByUser(@RequestParam(name = "ids") String ids) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        return repairTaskService.receiveByUser(user, ids);
    }

}
