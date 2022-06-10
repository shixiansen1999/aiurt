package com.aiurt.boot.modules.repairManage.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.common.system.query.QueryGenerator;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.common.util.DateUtils;
import com.aiurt.boot.common.util.RoleAdditionalUtils;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.repairManage.entity.RepairPool;
import com.aiurt.boot.modules.repairManage.service.IRepairPoolService;
import com.aiurt.boot.modules.repairManage.vo.AssignVO;
import com.aiurt.boot.modules.system.entity.SysDepart;
import com.aiurt.boot.modules.system.service.ISysDepartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 检修计划池
 * @Author: qian
 * @Date: 2021-09-16
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "检修计划池")
@RestController
@RequestMapping("/repairManage/repairPool")
public class RepairPoolController {
    @Autowired
    private IRepairPoolService repairPoolService;

    @Autowired
    private ISysDepartService departService;

    @Resource
    private RoleAdditionalUtils roleAdditionalUtils;

    @Autowired
    private IStationService stationService;

    /**
     * 分页列表查询
     *
     * @param repairPool
     * @param req
     * @return
     */
    @AutoLog(value = "检修计划池-分页列表查询")
    @ApiOperation(value = "检修计划池-分页列表查询", notes = "检修计划池-分页列表查询")
    @GetMapping(value = "/list")
    public Result queryPageList(RepairPool repairPool, HttpServletRequest req) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //班组ID
        final String orgId = user.getOrgId();
        if (StrUtil.isEmpty(orgId)) {
            return Result.error(500, "用户班组信息为空");
        }

        QueryWrapper<RepairPool> queryWrapper = QueryGenerator.initQueryWrapper(repairPool, req.getParameterMap());
        queryWrapper.eq("del_flag", 0);
        queryWrapper.orderByAsc("type");

//        final List<String> departMentList = roleAdditionalUtils.getListDepartIdsByUserId(user.getId());
//        queryWrapper.in(CollectionUtil.isNotEmpty(departMentList),"organization_id",departMentList);

        queryWrapper.eq("organization_id", orgId);
        List<RepairPool> list = repairPoolService.list(queryWrapper);
        return Result.ok(list);
    }

    /**
     * 指派检修人员
     *
     * @param
     * @return
     */
    @AutoLog(value = "检修计划池-指派检修人员")
    @ApiOperation(value = "检修计划池-指派检修人员", notes = "检修计划池-指派检修人员")
    @PostMapping(value = "/assigned")
    public Result assigned(@RequestBody @Validated AssignVO assignVO) {
        return repairPoolService.assigned(assignVO);
    }

    /**
     * 调整时间
     *
     * @param
     * @return
     */
    @AutoLog(value = "检修计划池-调整时间")
    @ApiOperation(value = "检修计划池-调整时间", notes = "检修计划池-调整时间")
    @PostMapping(value = "/updateTime")
    public Result updateTime(@RequestParam(name = "ids") String ids,
                             @RequestParam(name = "startTime") String startTime,
                             @RequestParam(name = "endTime") String endTime) {
        return repairPoolService.updateTime(ids, startTime, endTime);
    }


    /**
     * 根据当前用户获取
     *
     * @return
     */
    @AutoLog(value = "检修单详情-根据当前用户获取")
    @ApiOperation(value = "检修单详情-根据当前用户获取", notes = "检修单详情-根据当前用户获取")
    @GetMapping(value = "/getRepairTask")
    public Result getRepairTask(@RequestParam(name = "startTime") String startTime,
                                @RequestParam(name = "endTime") String endTime) {
        return repairPoolService.getRepairTask(startTime, endTime);
    }

    /**
     * 导出excel
     * 导出年检计划
     *
     * @param request
     * @param response
     */
    @ApiOperation(value = "检修池-导出", notes = "检修池-导出")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<RepairPool> pageList = new ArrayList<>();
        if (user != null && StrUtil.isNotEmpty(user.getOrgId())) {
            //获取今年的时间范围
            final LambdaQueryWrapper<RepairPool> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RepairPool::getDelFlag, 0);
            queryWrapper.eq(RepairPool::getOrganizationId, user.getOrgId());
            queryWrapper.and(wrapper -> wrapper.likeRight(RepairPool::getStartTime, DateUtils.getYear()).or()
                    .likeRight(RepairPool::getEndTime, DateUtils.getYear()));
            queryWrapper.orderByAsc(RepairPool::getWeeks);
            pageList = repairPoolService.list(queryWrapper);
        }
        String teamName="";
        if (pageList.size() > 0){
            final String organizationId = pageList.get(0).getOrganizationId();
            final SysDepart depart = departService.getById(organizationId);
            teamName = depart.getDepartName();
        }
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //导出文件名称

        final ExportParams params = new ExportParams("年计划", "班组:" + teamName, "年计划");
        params.setType(ExcelType.XSSF);
        mv.addObject(NormalExcelConstants.FILE_NAME, "年计划");
        mv.addObject(NormalExcelConstants.CLASS, RepairPool.class);
        mv.addObject(NormalExcelConstants.PARAMS,params);
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    @ApiOperation(value = "获取时间范围和周数", notes = "获取时间范围和周数")
    @GetMapping(value = "/getTimeInfo")
    public Result getTimeInfo(@RequestParam(name = "year") int year) {
        return repairPoolService.getTimeInfo(year);
    }
}
