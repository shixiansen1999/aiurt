package com.aiurt.boot.weeklyplan.controller;

import com.aiurt.boot.weeklyplan.dto.ConstructionWeekPlanCommandDTO;
import com.aiurt.boot.weeklyplan.entity.ConstructionWeekPlanCommand;
import com.aiurt.boot.weeklyplan.service.IConstructionWeekPlanCommandService;
import com.aiurt.boot.weeklyplan.vo.ConstructionUserVO;
import com.aiurt.boot.weeklyplan.vo.ConstructionWeekPlanCommandVO;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map;

/**
 * @Description: construction_week_plan_command
 * @Author: aiurt
 * @Date: 2022-11-22
 * @Version: V1.0
 */
@Api(tags = "施工周计划")
@RestController
@RequestMapping("/weeklyplan/constructionWeekPlanCommand")
@Slf4j
public class ConstructionWeekPlanCommandController extends BaseController<ConstructionWeekPlanCommand, IConstructionWeekPlanCommandService> {
    @Autowired
    private IConstructionWeekPlanCommandService constructionWeekPlanCommandService;

    /**
     * 施工周计划列表查询
     *
     * @param constructionWeekPlanCommandDTO
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "施工周计划列表查询")
    @ApiOperation(value = "施工周计划列表查询", notes = "施工周计划列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<ConstructionWeekPlanCommandVO>> queryPageList(ConstructionWeekPlanCommandDTO constructionWeekPlanCommandDTO,
                                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                      HttpServletRequest req) {
        Page<ConstructionWeekPlanCommandVO> page = new Page<>(pageNo, pageSize);
        IPage<ConstructionWeekPlanCommandVO> pageList = constructionWeekPlanCommandService.queryPageList(page, constructionWeekPlanCommandDTO);
        return Result.OK(pageList);
    }

    /**
     * 施工周计划申报
     *
     * @param constructionWeekPlanCommand
     * @return
     */
    @AutoLog(value = "施工周计划申报")
    @ApiOperation(value = "施工周计划申报", notes = "施工周计划申报")
    @PostMapping(value = "/declaration")
    public Result<String> declaration(@RequestBody ConstructionWeekPlanCommand constructionWeekPlanCommand) {
        String id = constructionWeekPlanCommandService.declaration(constructionWeekPlanCommand);
        return Result.OK("添加成功！", id);
    }

    /**
     * 施工周计划-编辑
     *
     * @param constructionWeekPlanCommand
     * @return
     */
    @AutoLog(value = "施工周计划-编辑")
    @ApiOperation(value = "施工周计划-编辑", notes = "施工周计划-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.POST})
    public Result<String> edit(@RequestBody ConstructionWeekPlanCommand constructionWeekPlanCommand) {
        constructionWeekPlanCommandService.edit(constructionWeekPlanCommand);
        return Result.OK("编辑成功!");
    }

    /**
     * 施工周计划-取消计划
     *
     * @param id
     * @return
     */
    @AutoLog(value = "施工周计划-取消计划")
    @ApiOperation(value = "施工周计划-取消计划", notes = "施工周计划-取消计划")
    @RequestMapping(value = "/cancel", method = {RequestMethod.POST})
    public Result<String> cancel(@ApiParam(name = "id", value = "记录主键ID") @RequestParam("id") String id,
                                 @ApiParam(name = "reason", value = "取消原因") @RequestParam("reason") String reason) {
        constructionWeekPlanCommandService.cancel(id, reason);
        return Result.OK("计划已成功取消!");
    }

    /**
     * 施工周计划-计划提审
     *
     * @param id
     * @return
     */
    @AutoLog(value = "施工周计划-计划提审")
    @ApiOperation(value = "施工周计划-计划提审", notes = "施工周计划-计划提审")
    @RequestMapping(value = "/submit", method = {RequestMethod.POST})
    public Result<String> submit(@ApiParam(name = "id", value = "记录主键ID") @RequestParam("id") String id) {
        constructionWeekPlanCommandService.submit(id);
        return Result.OK("计划提审成功!");
    }

    /**
     * 施工周计划-计划审核
     *
     * @param id
     * @return
     */
    @AutoLog(value = "施工周计划-计划审核")
    @ApiOperation(value = "施工周计划-计划审核", notes = "施工周计划-计划审核")
    @RequestMapping(value = "/audit", method = {RequestMethod.POST})
    public Result<String> audit(@ApiParam(name = "id", value = "记录主键ID")
                                @RequestParam("id") String id,
                                @ApiParam(name = "status", value = "审批状态：0未审批、1同意、2驳回")
                                @RequestParam("status") Integer status) {
        constructionWeekPlanCommandService.audit(id);
        return Result.OK("计划审核成功!");
    }

    /**
     * 施工周计划-根据ID查询计划信息
     *
     * @param id
     * @return
     */
    @AutoLog(value = "施工周计划-根据ID查询计划信息")
    @ApiOperation(value = "施工周计划-根据ID查询计划信息", notes = "施工周计划-根据ID查询计划信息")
    @GetMapping(value = "/queryById")
    public Result<ConstructionWeekPlanCommand> queryById(@RequestParam(name = "id", required = true) String id) {
        ConstructionWeekPlanCommand constructionWeekPlanCommand = constructionWeekPlanCommandService.queryById(id);
        return Result.OK(constructionWeekPlanCommand);
    }

    @ApiOperation(value = "周计划审核", notes = "周计划审核")
    @GetMapping(value = "/queryWorkToDo")
    public Result<IPage<ConstructionWeekPlanCommandVO>> queryWorkToDo(ConstructionWeekPlanCommandDTO constructionWeekPlanCommandDTO,
                                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<ConstructionWeekPlanCommandVO> page = new Page<>(pageNo, pageSize);
        IPage<ConstructionWeekPlanCommandVO> pageList = constructionWeekPlanCommandService.queryWorkToDo(page, constructionWeekPlanCommandDTO);
        return Result.OK(pageList);
    }

    /**
     * 施工周计划-根据ID删除计划
     */
    @ApiOperation(value = "施工周计划-根据ID删除计划", notes = "施工周计划-根据ID删除计划")
    @PostMapping(value = "/delete")
    public Result<String> delete(@RequestParam @ApiParam(name = "id", value = "计划主键ID") String id) {
        constructionWeekPlanCommandService.delete(id);
        return Result.OK("删除成功！");
    }

    /**
     * 施工周计划-获取岗位为施工负责人的用户
     *
     * @return
     */
    @ApiOperation(value = "施工周计划-获取岗位为施工负责人的用户", notes = "施工周计划-获取岗位为施工负责人的用户")
    @RequestMapping(value = "/getConstructionUser", method = RequestMethod.GET)
    public Result<?> getConstructionUser() {
        List<ConstructionUserVO> loginUsers = constructionWeekPlanCommandService.getConstructionUser();
        return Result.OK(loginUsers);
    }

    @ApiOperation(value = "下载施工周计划导入模板", notes = "下载施工周计划导入模板")
    @RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
    public void downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("templates/constructionWeekPlanCommand.xlsx");
        InputStream bis = classPathResource.getInputStream();
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        int len = 0;
        while ((len = bis.read()) != -1) {
            out.write(len);
            out.flush();
        }
        out.close();
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @ApiOperation(value = "通过excel导入数据", notes = "通过excel导入数据")
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
                return constructionWeekPlanCommandService.importExcelMaterial(file, params);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return Result.error("文件导入失败！");
    }

    /**
     * 施工周计划-导出周计划
     *
     * @return
     */
    @ApiOperation(value = "施工周计划-导出周计划", notes = "施工周计划-导出周计划")
    @RequestMapping(value = "/exportXls", method = {RequestMethod.GET, RequestMethod.POST})
    public void exportXls(HttpServletRequest request, HttpServletResponse response,
                          @ApiParam(name = "lineCode", value = "线路编码") String lineCode,
                          @ApiParam(name = "startDate", value = "所属周起始时间，格式yyyy-MM-dd")
                          @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                          @ApiParam(name = "endDate", value = "所属周截止时间，格式yyyy-MM-dd")
                          @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        constructionWeekPlanCommandService.exportXls(request, response, lineCode, startDate, endDate);
    }
}
