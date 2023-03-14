package com.aiurt.modules.worklog.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.result.*;
import com.aiurt.common.util.ArchiveUtils;
import com.aiurt.modules.worklog.dto.WorkLogDTO;
import com.aiurt.modules.worklog.dto.WorkLogUserTaskDTO;
import com.aiurt.modules.worklog.entity.WorkLog;
import com.aiurt.modules.worklog.param.LogCountParam;
import com.aiurt.modules.worklog.param.WorkLogParam;
import com.aiurt.modules.worklog.service.IWorkLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/20
 * @desc
 */
@Slf4j
@Api(tags="工作日志")
@RestController
@RequestMapping("/worklog/workLogDepot")
public class WorkLogController {
    @Autowired
    private IWorkLogService workLogDepotService;

    @Resource
    private ArchiveUtils archiveUtils;

    /**
     * 工作日志上报-分页列表查询
     * @param pageNo
     * @param pageSize
     * @param param
     * @param req
     * @return
     */
    @AutoLog(value = "工作日志上报-分页列表查询")
    @ApiOperation(value="工作日志上报-分页列表查询", notes="工作日志上报-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<WorkLogResult>> queryPageList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                      @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                      WorkLogParam param, HttpServletRequest req) {
        Result<IPage<WorkLogResult>> result = new Result<IPage<WorkLogResult>>();
        Page<WorkLogResult> page = new Page<WorkLogResult>(pageNo, pageSize);
        IPage<WorkLogResult> pageList = workLogDepotService.pageList(page,param,req);
        /*result.setSuccess(true);
        result.setResult(pageList);*/
        return Result.ok(pageList);
    }

    /**
     * 工作日志确认-分页列表查询
     * @param pageNo
     * @param pageSize
     * @param param
     * @param req
     * @return
     */
    @AutoLog(value = "工作日志确认-分页列表查询")
    @ApiOperation(value="工作日志确认-分页列表查询", notes="工作日志确认-分页列表查询")
    @GetMapping(value = "/confirmList")
    @PermissionData(pageComponent = "workLog/affirm")
    public Result<IPage<WorkLogResult>> queryConfirmList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                         @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                         WorkLogParam param, HttpServletRequest req) {
        Result<IPage<WorkLogResult>> result = new Result<IPage<WorkLogResult>>();
        Page<WorkLogResult> page = new Page<WorkLogResult>(pageNo, pageSize);
        IPage<WorkLogResult> pageList = workLogDepotService.queryConfirmList(page,param,req);
        /*result.setSuccess(true);
        result.setResult(pageList);*/
        return Result.ok(pageList);
    }

    /**
     * 导出excel
     * @param param
     * @param req
     * @return
     */
    @AutoLog(value = "导出excel")
    @ApiOperation(value="导出excel", notes="导出excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(WorkLogParam param, HttpServletRequest req) {
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<WorkLogResult> list = workLogDepotService.exportXls(param,req);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "工作日志列表");
        mv.addObject(NormalExcelConstants.CLASS, WorkLogResult.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("工作日志列表数据",  "导出信息", ExcelType.XSSF));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    /**
     *   添加工作日志
     * @param dto
     * @return
     */
    @AutoLog(value = "工作日志-添加")
    @ApiOperation(value="工作日志-添加", notes="工作日志-添加")
    @PostMapping(value = "/add")
    public Result<WorkLog> add(@RequestBody WorkLogDTO dto, HttpServletRequest req) {
        Result<WorkLog> result = new Result<WorkLog>();
        try {
            if (StrUtil.isNotEmpty(dto.getId())){
                WorkLog workLog = workLogDepotService.getById(dto.getId());
                //如果工作日志已经提交，则不能再保存
                if (workLog.getStatus()==1&&dto.getStatus()==0){
                    Result.error("该工作日志已经提交，无法保存");
                    return result;
                }
                workLogDepotService.editWorkLog(dto);
                result.success("保存成功！");
            }else {
                workLogDepotService.add(dto,req);
                result.success("添加成功！");
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            result.error500(e.getMessage());
        }
        return result;
    }

    @AutoLog(value = "工作日志-获取保存")
    @ApiOperation(value="工作日志-获取保存", notes="工作日志-获取保存")
    @GetMapping(value = "/getSavedWorkLog")
    public Result<WorkLogResult> getSaved(){
        Result<WorkLogResult> result = new Result<WorkLogResult>();
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = loginUser.getId();
        WorkLog workLog =workLogDepotService.getOne(new LambdaQueryWrapper<WorkLog>()
                .eq(WorkLog::getCreateBy,userId).eq(WorkLog::getStatus,0)
                .orderByDesc(WorkLog::getCreateTime).last("limit 0,1"));
        WorkLogResult detailResult = new WorkLogResult();
        if (ObjectUtils.isNotEmpty(workLog)){
            detailResult = workLogDepotService.getDetailById(workLog.getId());
            //result.setResult(detailResult);
        }

        return Result.ok(detailResult);
    }

    /**
     *  编辑
     * @param dto
     * @return
     */
    @AutoLog(value = "工作日志-编辑")
    @ApiOperation(value="工作日志-编辑", notes="工作日志-编辑")
    @PutMapping(value = "/edit")
    public Result<WorkLog> edit(@Valid @RequestBody WorkLogDTO dto) {
        try {
            workLogDepotService.editWorkLog(dto);
        }catch (Exception e) {
            log.error(e.getMessage(),"系统异常");
            Result.error(e.getMessage());
        }
        return  Result.ok("修改成功");
    }
    /**
     *  查看当前用户，当天的检修、巡检、故障的工单
     * @param
     * @return
     */
    @AutoLog(value = "查看当前用户，当天的检修、巡检、故障的工单")
    @ApiOperation(value="查看当前用户，当天的检修、巡检、故障的工单", notes="查看当前用户，当天的检修、巡检、故障的工单")
    @GetMapping(value = "/getUseTask")
    public Result<WorkLogUserTaskDTO> getUseTask() {
        WorkLogUserTaskDTO patrolWorkLogDTO = workLogDepotService.getUseTask();
        return Result.ok(patrolWorkLogDTO);
    }

    /**
     *   通过id假删除
     * @param id
     * @return
     */
    @AutoLog(value = "工作日志-通过id删除")
    @ApiOperation(value="工作日志-通过id删除", notes="工作日志-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name="id",required=true) String id) {
        try {
            workLogDepotService.deleteById(id);
        } catch (Exception e) {
            log.error("删除失败",e.getMessage());
            return Result.error("删除失败:"+e.getMessage());
        }
        return Result.ok("删除成功!");
    }

    /**
     *  批量删除
     * @param ids
     * @return
     */
    @AutoLog(value = "工作日志-批量删除")
    @ApiOperation(value="工作日志-批量删除", notes="工作日志-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<WorkLog> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        Result<WorkLog> result = new Result<WorkLog>();
        if(ids==null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        }else {
            this.workLogDepotService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     * @param id
     * @return
     */
    @AutoLog(value = "工作日志-通过id查询")
    @ApiOperation(value="工作日志-通过id查询", notes="工作日志-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<WorkLog> queryById(@RequestParam(name="id",required=true) String id) {
        Result<WorkLog> result = new Result<WorkLog>();
        WorkLog workLogDepot = workLogDepotService.getById(id);
        if(workLogDepot==null) {
            //result.error500("未找到对应实体");
            return Result.error("未找到对应实体");
        }else {
            /*result.setResult(workLogDepot);
            result.setSuccess(true);*/
            return Result.ok(workLogDepot);
        }
        //return result;
    }

    /**
     * 通过ids查询详情
     * @param ids
     * @return
     */
    @AutoLog(value = "工作日志-通过id查询")
    @ApiOperation(value="工作日志-通过id查询", notes="工作日志-通过id查询")
    @PostMapping(value = "/queryByIds")
    public Result<List<WorkLogResult> > queryByIds(@RequestBody @NotNull(message = "id不能为空") @Size(min = 1,message = "id数量不能少于1") List<Long> ids) {
        //List<WorkLogResult> list= workLogDepotService.detailByIds(ids);
        return Result.ok();
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<WorkLog> listWorkLogDepots = ExcelImportUtil.importExcel(file.getInputStream(), WorkLog.class, params);
                workLogDepotService.saveBatch(listWorkLogDepots);
                return Result.ok("文件导入成功！数据行数:" + listWorkLogDepots.size());
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                return Result.error("文件导入失败:"+e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.ok("文件导入失败！");
    }

    /**
     * 工作日志编辑查看
     * @param id
     * @return
     */
    @AutoLog(value = "工作日志查看")
    @ApiOperation(value="工作日志查看", notes="工作日志查看")
    @GetMapping(value = "/queryDetail")
    public Result<WorkLogDTO> queryDetail(@RequestParam String id) {
        Result<WorkLogDTO> result = new Result<WorkLogDTO>();
        WorkLogDTO detailById = workLogDepotService.getDetailById(id);
        if (detailById.getConfirmStatus()==1 || detailById.getCheckStatus()==1){
            detailById.setEditFlag(false);
        }else {
            detailById.setEditFlag(true);
        }
        //result.setResult(detailById);
        return Result.ok(detailById);
    }

    /**
     * 工作日志通过id查看
     * @param id
     * @return
     */
    @AutoLog(value = "工作日志通过id查看")
    @ApiOperation(value="工作日志通过id查看", notes="工作日志通过id查看")
    @GetMapping(value = "/queryWorkLogDetail")
    public Result<WorkLogDetailResult> queryWorkLogDetail(@RequestParam String id) {
        Result<WorkLogDetailResult> result = new Result<WorkLogDetailResult>();
        WorkLogDetailResult detailById = workLogDepotService.queryWorkLogDetail(id);
        // result.setResult(detailById);
        return Result.ok(detailById);
    }

    /**
     * 查询当最新的未完成事项（当前登录人所拥有的部门权限）
     * @return
     */
    @AutoLog(value = "查询当最新的未完成事项")
    @ApiOperation(value="查询当最新的未完成事项", notes="查询当最新的未完成事项")
    @GetMapping(value = "/getLastUnfinishedMatters")
    public Result<String> getUnfinishedMatters() {

        return workLogDepotService.getUnfinishedMatters();
    }

    /**
     * 工作日志确认
     * @param id
     * @return
     */
    @AutoLog(value = "工作日志确认")
    @ApiOperation(value="工作日志确认", notes="工作日志确认")
    @GetMapping(value = "/confirm")
    public Result<?> confirm(@RequestParam String id) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        WorkLog byId = workLogDepotService.getById(id);
        if (byId != null) {
            List<String> list = StrUtil.splitTrim(byId.getSucceedId(), ",");
            if(CollUtil.isNotEmpty(list)){
                list = list.stream().filter(l->l.equals(user.getId())).collect(Collectors.toList());
                if(CollUtil.isEmpty(list))
                {
                    throw new AiurtBootException("您不是该日志的接班人！");
                }
            }
            byId.setConfirmStatus(1).setSucceedTime(new Date());
            workLogDepotService.updateById(byId);
            return Result.ok("确认成功");

        }else {
            return Result.error("未查询需确认记录");
        }
    }

    /**
     *  批量审阅
     * @param ids
     * @return
     */
    @AutoLog(value = "工作日志-批量审阅")
    @ApiOperation(value="工作日志-批量审阅", notes="工作日志-批量审阅")
    @GetMapping(value = "/checkBatch")
    public Result<?> checkBatch(@RequestParam String ids) {
        if (ids == null){
            return Result.error("请选择需审阅记录");
        }
        String[] split = ids.split(",");
        List<WorkLog> workLogList = workLogDepotService.list(new LambdaQueryWrapper<WorkLog>().in(WorkLog::getId, split));
        List<WorkLog> collect = workLogList.stream().filter(w -> w.getSucceedId() != null && 0 == w.getConfirmStatus()).collect(Collectors.toList());
        if(collect.size()>0)
            {
                return Result.error("有接班人，需确认后，才能审批！");
            }
        else
        {
            workLogDepotService.lambdaUpdate().in(WorkLog::getId,split).update(new WorkLog().setCheckStatus(1));
        }
        return Result.ok("审阅成功");
    }

    /**
     * 根据当前登录人id获取巡检检修故障待办消息
     * @param nowday
     * @return
     */
    @AutoLog(value = "根据当前登录人id获取巡检检修故障待办消息")
    @ApiOperation(value="根据当前登录人id获取巡检检修故障待办消息", notes="根据当前登录人id获取巡检检修故障待办消息")
    @GetMapping(value = "/getWaitMessage")
    public Result<LogResult> getWaitMessage(@RequestParam String nowday, HttpServletRequest req) {
        Result<LogResult> waitMessage = workLogDepotService.getWaitMessage(nowday,req);
        return waitMessage;
    }

    /**
     * 日志统计
     * @param param
     * @return
     */
    @AutoLog(value = "日志统计")
    @ApiOperation(value="日志统计", notes="日志统计")
    @GetMapping(value = "/getLogCount")
    public Result<List<LogCountResult>> getLogCount(LogCountParam param) {
        Result<List<LogCountResult>> result = new Result<>();
        List<LogCountResult> logCount = workLogDepotService.getLogCount(param);
        //result.setResult(logCount);
        return Result.ok(logCount);
    }

    /**
     * 首页工作日志提交数量
     * @param startTime
     * @param endTime
     * @return
     */
    @AutoLog(value = "首页工作日志提交数量")
    @ApiOperation(value="首页工作日志提交数量", notes="首页工作日志提交数量")
    @GetMapping(value = "/getLogSubmitNum")
    public Result<LogSubmitCount> getLogSubmitNum(@RequestParam(name = "dayStart", required = false) String startTime, @RequestParam(name = "dayEnd", required = false) String endTime) {
        Result<LogSubmitCount> logSubmitNum = workLogDepotService.getLogSubmitNum(startTime, endTime);
        return logSubmitNum;
    }

    /**
     * @Description: 今日工作内容
     * @author: niuzeyu
     */
    @AutoLog(value = "今日工作内容")
    @ApiOperation(value = "今日工作内容", notes = "今日工作内容")
    @GetMapping(value = "/getTodayJobContent")
    public Result getTodayJobContent(@RequestParam(name = "nowday", required = false) String nowday) {
        Result<Map> result = new Result<>();
        Map map = workLogDepotService.getTodayJobContent(nowday);
        //result.setResult(map);
        return Result.ok(map);
    }


    /**
     * 日志归档
     * @param request
     * @param data
     * @return
     */
    @AutoLog(value = "日志归档")
    @ApiOperation(value = "日志归档", notes = "日志归档")
    @PostMapping(value = "/archiveWorkLog")
    public Result archiveWorkLog(HttpServletRequest request, @RequestBody List<WorkLogResult> data) {
        if (data == null || data.size() == 0) {
            return Result.error("没有选择要归档的文件");
        }

        // 获取token先看有没有归档权限
        String token = null;
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        try {
            token = archiveUtils.getToken(sysUser.getUsername());
            System.out.println(token);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("系统异常");
        }
        if (StringUtils.isEmpty(token)) {
            return Result.error("没有归档权限");
        }

        // 获取登录用户信息
        Map userInfo = archiveUtils.getArchiveUser(sysUser.getUsername(), token);
        if (ObjectUtil.isEmpty(userInfo)) {
            return Result.error("没有归档权限");
        }

        // 通过id获取档案类型信息，拿到refileFolderId
        Map typeInfo = archiveUtils.getTypeInfoById(token);
        String refileFolderId = typeInfo.get("refileFolderId").toString();

        // 逐条归档
        String finalToken = token;
        String finalArchiveUserId = userInfo.get("ID").toString();
        String username = userInfo.get("Name").toString();
        String sectId = typeInfo.get("sectId").toString();

        data.forEach(workLogResult -> {
            workLogDepotService.archWorkLog(workLogResult, finalToken, finalArchiveUserId, refileFolderId, username, sectId);
        });

        return Result.ok("归档成功");
    }
}
