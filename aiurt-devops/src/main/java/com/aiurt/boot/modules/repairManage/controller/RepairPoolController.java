package com.aiurt.boot.modules.repairManage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.system.vo.LoginUser;
import com.swsc.copsms.modules.repairManage.entity.RepairPool;
import com.swsc.copsms.modules.repairManage.service.IRepairPoolService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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

    /**
     * 分页列表查询
     *
     * @param repairPool
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "检修计划池-分页列表查询")
    @ApiOperation(value = "检修计划池-分页列表查询", notes = "检修计划池-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<RepairPool>> queryPageList(RepairPool repairPool,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                   HttpServletRequest req) {
        Result<IPage<RepairPool>> result = new Result<IPage<RepairPool>>();
        QueryWrapper<RepairPool> queryWrapper = QueryGenerator.initQueryWrapper(repairPool, req.getParameterMap());
        queryWrapper.eq("del_flag",0);
        Page<RepairPool> page = new Page<RepairPool>(pageNo, pageSize);
        IPage<RepairPool> pageList = repairPoolService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
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
    public Result assigned(@RequestParam(name = "ids") String ids,
                           @RequestParam(name = "userIds") String userIds,
                           @RequestParam(name = "userNames")String userNames) {
        return repairPoolService.assigned(ids, userIds ,userNames);
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
                                @RequestParam(name = "endTime") String endTime){
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        return repairPoolService.getRepairTask(user.getId(),startTime,endTime);
    }
}
