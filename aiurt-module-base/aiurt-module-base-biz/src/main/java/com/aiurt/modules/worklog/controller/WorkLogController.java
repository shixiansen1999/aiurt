package com.aiurt.modules.worklog.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.result.LogCountResult;
import com.aiurt.common.result.LogResult;
import com.aiurt.common.result.LogSubmitCount;
import com.aiurt.common.result.WorkLogResult;
import com.aiurt.modules.worklog.dto.WorkLogDTO;
import com.aiurt.modules.worklog.entity.WorkLog;
import com.aiurt.modules.worklog.param.LogCountParam;
import com.aiurt.modules.worklog.param.WorkLogParam;
import com.aiurt.modules.worklog.service.IWorkLogService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.*;

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
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
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
    public Result<IPage<WorkLogResult>> queryConfirmList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                         @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                         WorkLogParam param, HttpServletRequest req) {
        Result<IPage<WorkLogResult>> result = new Result<IPage<WorkLogResult>>();
        Page<WorkLogResult> page = new Page<WorkLogResult>(pageNo, pageSize);
        IPage<WorkLogResult> pageList = workLogDepotService.queryConfirmList(page,param,req);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
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
            workLogDepotService.add(dto,req);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            result.error500(e.getMessage());
        }
        return result;
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
            Result.ok("修改成功");
        }catch (Exception e) {
            log.error(e.getMessage(),e);
            Result.error(e.getMessage());
        }
        return  Result.ok("修改成功");
    }

    /**
     *   通过id假删除
     * @param id
     * @return
     */
    @AutoLog(value = "工作日志-通过id删除")
    @ApiOperation(value="工作日志-通过id删除", notes="工作日志-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name="id",required=true) Integer id) {
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
            result.error500("未找到对应实体");
        }else {
            result.setResult(workLogDepot);
            result.setSuccess(true);
        }
        return result;
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
     * 工作日志查看
     * @param id
     * @return
     */
    @AutoLog(value = "工作日志查看")
    @ApiOperation(value="工作日志查看", notes="工作日志查看")
    @GetMapping(value = "/queryDetail")
    public Result<WorkLogResult> queryDetail(@RequestParam String id) {
        Result<WorkLogResult> result = new Result<WorkLogResult>();
        WorkLogResult detailById = workLogDepotService.getDetailById(id);
        result.setResult(detailById);
        return result;
    }

    /**
     * 工作日志确认
     * @param id
     * @return
     */
    @AutoLog(value = "工作日志确认")
    @ApiOperation(value="工作日志确认", notes="工作日志确认")
    @GetMapping(value = "/confirm")
    public Result<?> confirm(@RequestParam Integer id) {
        WorkLog byId = workLogDepotService.getById(id);
        if (byId != null) {
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
        workLogDepotService.lambdaUpdate().in(WorkLog::getId,split).update(new WorkLog().setCheckStatus(1));
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
        result.setResult(logCount);
        return result;
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
}
