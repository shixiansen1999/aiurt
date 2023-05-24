package com.aiurt.modules.sysfile.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.sysfile.entity.SysFile;
import com.aiurt.modules.sysfile.param.SysFileParam;
import com.aiurt.modules.sysfile.param.SysFileWebParam;
import com.aiurt.modules.sysfile.service.ISysFileManageService;
import com.aiurt.modules.sysfile.vo.SysFileManageVO;
import com.aiurt.modules.sysfile.vo.SysFileVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author:wgp
 * @create: 2023-05-22 16:57
 * @Description:
 */
@Slf4j
@Api(tags = "文档表")
@RestController
@RequestMapping("/sys/file")
public class SysFileManageController {

    @Resource
    private ISysFileManageService sysFileManageService;

    /**
     * 获取文档分页列表查询
     *
     * @param sysFile
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "文档表-分页列表查询")
    @ApiOperation(value = "文档表-分页列表查询", notes = "文档表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SysFileManageVO>> getFilePageList(SysFileWebParam sysFile,
                                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                          HttpServletRequest request) {
        Page<SysFileManageVO> page = new Page<>(pageNo, pageSize);
        page = sysFileManageService.getFilePageList(page, sysFile);
        return Result.OK(page);
    }

    /**
     * 添加文件
     *
     * @param req   HttpServletRequest对象，用于获取请求相关信息
     * @param files 待添加的文件信息集合
     * @return 添加结果，包含SysFile对象
     */
    @AutoLog(value = "文档表-添加")
    @ApiOperation(value = "文档表-添加", notes = "文档表-添加")
    @PostMapping(value = "/add")
    public Result<SysFile> addFile(HttpServletRequest req, @RequestBody @Validated List<SysFileParam> files) {
        Result<SysFile> result = sysFileManageService.addFile(files);
        return result;
    }

    /**
     * 编辑文件
     *
     * @param req     HttpServletRequest对象，用于获取请求相关信息
     * @param sysFileParam SysFileParam对象，待编辑的文件信息
     * @return 编辑结果，包含SysFile对象
     */
    @AutoLog(value = "文档表-编辑")
    @ApiOperation(value = "文档表-编辑", notes = "文档表-编辑")
    @PutMapping(value = "/edit")
    public Result<SysFile> editFile(HttpServletRequest req, @RequestBody SysFileParam sysFileParam) {
        int result = sysFileManageService.editFile(sysFileParam);
        return result > 0 ? Result.OK("编辑文档成功") : Result.error("编辑文档失败");
    }

    /**
     * 通过id删除文件
     *
     * @param id 文件ID，待删除的文件的唯一标识符
     * @return 删除结果，表示文件删除成功与否的通用结果对象
     */
    @AutoLog(value = "文档表-通过id删除", operateType = 4, permissionUrl = "/document/documentManage")
    @ApiOperation(value = "文档表-通过id删除", notes = "文档表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        int result = sysFileManageService.removeById(id);
        return result > 0 ? Result.OK("删除文档成功") : Result.error("删除文档失败");
    }

    /**
     * 通过id查询
     *
     * @param id 文件ID，待查询的文件的唯一标识符
     * @return 查询文件结果
     */
    @AutoLog(value = "文档表-通过id查询")
    @ApiOperation(value = "文档表-通过id查询", notes = "文档表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<SysFileVO> queryById(@RequestParam(name = "id", required = true) String id) {
        SysFileVO result = sysFileManageService.getById(id);
        return Result.OK(result);
    }

    /**
     * 增加文件下载次数
     *
     * @param id 文件id
     * @return
     */
    @AutoLog(value = "增加文件下载次数")
    @ApiOperation(value = "增加文件下载次数", notes = "增加文件下载次数")
    @PostMapping(value = "/addCount")
    public Result<?> addCount(@RequestParam("id") Long id) {
        boolean result = sysFileManageService.addCount(id);
        return result ? Result.ok() : Result.error("更改次数失败");
    }

    /**
     * 导出下载报告列表
     *
     * @param request  HttpServletRequest对象，用于获取请求相关信息
     * @param response HttpServletResponse对象，用于设置响应相关信息
     * @param fileId   文件ID，指定要导出下载的报告列表的文件的唯一标识符
     * @return ModelAndView对象，用于渲染导出下载报告列表的视图
     */
    @AutoLog(value = "文档表-下载记录导出")
    @ApiOperation(value = "文档表-下载记录导出", notes = "文档表-下载记录导出")
    @RequestMapping(value = "/reportExportDownloadList")
    public ModelAndView reportExportDownloadList(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 @RequestParam(value = "fileId", required = true) Long fileId) {
        ModelAndView mv = sysFileManageService.reportExportDownloadList(fileId);
        return mv;
    }
}
