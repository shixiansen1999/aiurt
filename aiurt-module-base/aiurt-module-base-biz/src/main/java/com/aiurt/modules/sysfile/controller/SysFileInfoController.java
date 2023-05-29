package com.aiurt.modules.sysfile.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.sysfile.entity.SysFileInfo;
import com.aiurt.modules.sysfile.param.SysFileInfoParam;
import com.aiurt.modules.sysfile.service.ISysFileInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 文件下载记录接口
 * @Author: wgp
 * @Date: 2023-05-25
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "文件下载记录接口")
@RestController
@RequestMapping("/sys/fileinfo")
public class SysFileInfoController {

    @Resource
    private ISysFileInfoService sysFileInfoService;

    /**
     * 保存下载记录
     *
     * @param sysFileInfo
     * @return
     */
    @AutoLog(value = "保存下载记录")
    @ApiOperation(value = "保存下载记录", notes = "保存下载记录")
    @PostMapping(value = "/addDownload")
    public Result<SysFileInfo> addDownload(HttpServletRequest req, @RequestBody SysFileInfo sysFileInfo) {
        SysFileInfo addSysFileInfo = sysFileInfoService.addDownload(sysFileInfo);
        return Result.OK(addSysFileInfo);
    }

    /**
     * 根据文件id、下载时间、下载用户、下载状态、班组查询下载记录分页列表
     *
     * @param sysFileInfoParam
     * @return
     */
    @AutoLog(value = "根据文件id、下载时间、下载用户、下载状态、班组查询下载记录分页列表")
    @ApiOperation(value = "根据文件id、下载时间、下载用户、下载状态、班组查询下载记录分页列表", notes = "根据文件id、下载时间、下载用户、下载状态、班组查询下载记录分页列表")
    @GetMapping(value = "/queryPageDownloadList")
    public Result<IPage<SysFileInfo>> queryPageDownloadList(SysFileInfoParam sysFileInfoParam, HttpServletRequest request) {
        Page<SysFileInfo> page = new Page<>(sysFileInfoParam.getPageNo(), sysFileInfoParam.getPageSize());
        page =sysFileInfoService.queryPageDownloadList(page,sysFileInfoParam);
        return Result.OK(page);
    }

    /**
     * 导出下载记录
     *
     * @param fileId 文件ID，指定要导出下载的报告列表的文件的唯一标识符
     * @return ModelAndView对象，用于渲染导出下载报告列表的视图
     */
    @AutoLog(value = "文档表-下载记录导出")
    @ApiOperation(value = "文档表-下载记录导出", notes = "文档表-下载记录导出")
    @GetMapping(value = "/reportExportDownloadList")
    public ModelAndView reportExportDownloadList(@RequestParam(value = "fileId", required = true) Long fileId) {
        ModelAndView mv = sysFileInfoService.reportExportDownloadList(fileId);
        return mv;
    }
}
