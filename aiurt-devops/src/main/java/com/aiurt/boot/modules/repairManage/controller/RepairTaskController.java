package com.aiurt.boot.modules.repairManage.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.common.util.RoleAdditionalUtils;
import com.aiurt.boot.common.util.oConvertUtils;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.repairManage.entity.RepairTask;
import com.aiurt.boot.modules.repairManage.service.IRepairTaskService;
import com.aiurt.boot.modules.repairManage.vo.DeviceQueryVO;
import com.aiurt.boot.modules.repairManage.vo.ReTaskDetailVO;
import com.aiurt.boot.modules.repairManage.vo.export.RepairTaskExportVO;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private IStationService stationService;

    @Value("${swsc.host}")
    private String host;

    @Resource
    private RoleAdditionalUtils roleAdditionalUtils;

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
        queryWrapper.eq("del_flag",0);

        final LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        final List<String> departMentList = roleAdditionalUtils.getListDepartIdsByUserId(user.getId());
        if (CollectionUtil.isNotEmpty(departMentList)){
            //根据部门list查询站点list
            final LambdaQueryWrapper<Station> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Station::getTeamId,departMentList).eq(Station::getDelFlag,0);
            final List<Station> list = stationService.list(wrapper);
            final List<Integer> collect = list.stream().map(Station::getId).collect(Collectors.toList());
            queryWrapper.in(CollectionUtil.isNotEmpty(list),"organization_id",collect);
        }

        Page<RepairTask> page = new Page<RepairTask>(pageNo, pageSize);
        IPage<RepairTask> pageList = repairTaskService.page(page, queryWrapper);
        pageList.getRecords().forEach(x -> {
            String organizationId = x.getOrganizationId();
            Station station = stationService.getById(organizationId);
            if (station != null) {
                x.setLineName(station.getLineName());
                x.setStationName(station.getStationName());
                x.setTeamName(station.getTeamName());
            }
        });
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
                              @RequestParam(name = "errorContent", required = false) String errorContent,
                              @RequestParam(name = "url", required = false) String url) {
        return repairTaskService.confirmById(id, confirmStatus, errorContent, url);
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
                              @RequestParam(name = "errorContent", required = false) String errorContent,
                              @RequestParam(name = "url", required = false) String url) {
        return repairTaskService.receiptById(id, receiptStatus, errorContent, url);
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
    @ApiOperation(value = "检修单-导出", notes = "检修单-导出")
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


        List<RepairTaskExportVO> list = new ArrayList<>();

        pageList.forEach(x->{
            final Station station = stationService.getById(x.getOrganizationId());
            x.setLineName(station.getLineName());
            x.setTeamName(station.getTeamName());
            x.setDes(host.concat("repairManage/MaintenanceList?id=").concat(x.getId().toString()));
            x.setStationName(station.getStationName());
            RepairTaskExportVO vo = new RepairTaskExportVO();

            BeanUtils.copyProperties(x,vo);
            vo.setWeeks("第".concat(x.getWeeks()+"").concat("周"));

            list.add(vo);
        });


        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "检修单列表");
        mv.addObject(NormalExcelConstants.CLASS, RepairTaskExportVO.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("检修单列表数据", "导出时间:" + LocalDate.now(), ExcelType.XSSF));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
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
            @ApiImplicitParam(name = "startTime", value = "开始时间,2020-10-01"),
            @ApiImplicitParam(name = "endTime", value = "结束时间,2020-10-07")

    })
    @PostMapping(value = "/getDetailByUser")
    public Result<List<ReTaskDetailVO>> getDetailByUser(@RequestParam(name = "startTime") String startTime,
                                                        @RequestParam(name = "endTime") String endTime) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        return repairTaskService.getDetailByUser(user, startTime, endTime);
    }

    /**
     * 领取检修工单
     *
     * @param
     * @return
     */
//    @AutoLog(value = "app-检修工单领取")
//    @ApiOperation(value = "app-检修工单领取", notes = "app-检修工单领取")
//    @ApiImplicitParam(name = "ids", value = "getDetailByUser接口中返回的RepairPoolList中id集合，按逗号连接")
//    @PostMapping(value = "/receiveByUser")
//    public Result receiveByUser(@RequestParam(name = "ids") String ids,
//                                @RequestParam(name = "workType") String workType,
//                                @RequestParam(name = "planOrderCode", required = false) String planOrderCode,
//                                @RequestParam(name = "planOrderCodeUrl", required = false) String planOrderCodeUrl) {
//        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        return repairTaskService.receiveByUser(user, ids, workType, planOrderCode, planOrderCodeUrl);
//    }

    @AutoLog(value = "app-检修工单执行提交")
    @ApiOperation(value = "app-检修工单执行提交", notes = "app-检修工单执行提交")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "检修单id"),
            @ApiImplicitParam(name = "position", value = "检修地点"),
            @ApiImplicitParam(name = "content", value = "检修记录"),
            @ApiImplicitParam(name = "position", value = "url按逗号分隔"),
            @ApiImplicitParam(name = "processContent", value = "处理结果")
    })
    @PostMapping(value = "/commit")
    public Result commit(@RequestParam(name = "id") String id,
                         @RequestParam(name = "position") String position,
                         @RequestParam(name = "content") String content,
//                         @RequestParam(name ="postionIds")String postionIds,
                         @RequestParam(name = "urls") String urls,
                         @RequestParam(name = "deviceIds", required = false) String deviceIds,
                         @RequestParam(name = "processContent", required = false) String processContent) {
        return repairTaskService.commit(id, position, content, urls, deviceIds, processContent);
    }

    /**
     * 设备检修记录查询
     *
     * @return
     */
    @AutoLog(value = "设备检修记录查询")
    @ApiOperation(value = "设备检修记录查询", notes = "设备检修记录查询")
    @PostMapping(value = "/queryByDevice")
    public Result<IPage<RepairTask>> queryByDevice(@RequestBody @Validated DeviceQueryVO deviceQueryVO) {
        return repairTaskService.queryByDevice(deviceQueryVO);
    }
}
