package com.aiurt.modules.sysfile.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.sysfile.entity.SysFile;
import com.aiurt.modules.sysfile.param.SysFolderParam;
import com.aiurt.modules.sysfile.service.ISysFolderService;
import com.aiurt.modules.sysfile.vo.SysFolderDetailVO;
import com.aiurt.modules.sysfile.vo.SysFolderTreeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 文件夹接口
 * @Author: wgp
 * @Date: 2023-05-23
 * @Version: V1.0
 */
@Validated
@Slf4j
@Api(tags = "文件夹接口")
@RestController
@RequestMapping("/sys/folder")
public class SysFolderController {

    @Resource
    private ISysFolderService sysFolderService;

    /**
     * 查询文件夹树形结构列表
     *
     * @param req  HttpServletRequest对象，用于获取请求相关信息
     * @param name 文件夹名称，用于筛选符合名称的文件夹
     * @param pid  文件夹父级id，用于筛选符合id的文件夹
     * @return 查询结果，包含SysFolderTreeVO对象列表
     */
    @AutoLog(value = "查询文件夹树形结构列表")
    @ApiOperation(value = "查询文件夹树形结构列表", notes = "查询文件夹树形结构列表")
    @GetMapping(value = "/tree")
    public Result<List<SysFolderTreeVO>> queryFolderTree(HttpServletRequest req, String name, Long pid) {
        List<SysFolderTreeVO> result = sysFolderService.queryFolderTree(name, pid);
        return Result.OK(result);
    }

    /**
     * 添加文件夹
     *
     * @param req   HttpServletRequest对象，用于获取请求参数和会话信息
     * @param param SysFolderParam对象，包含要添加的系统文件夹参数信息
     */
    @AutoLog(value = "添加文件夹")
    @ApiOperation(value = "添加文件夹", notes = "添加文件夹")
    @PostMapping(value = "/add")
    public Result<?> add(HttpServletRequest req, @RequestBody @Valid SysFolderParam param) {
        sysFolderService.addFolder(req, param);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑文件夹权限管理
     *
     * @param req   HttpServletRequest对象，用于获取请求参数和会话信息
     * @param param SysFolderParam对象，包含要编辑的系统文件夹参数信息
     * @return Result对象，表示编辑操作的结果
     */
    @AutoLog(value = "编辑文件夹权限管理")
    @ApiOperation(value = "编辑文件夹权限管理", notes = "编辑文件夹权限管理")
    @PostMapping(value = "/edit")
    public Result<?> edit(HttpServletRequest req, @RequestBody SysFolderParam param) {
        sysFolderService.edit(req, param);
        return Result.OK("编辑成功！");
    }

    /**
     * 获取系统文件夹详情
     *
     * @param req HttpServletRequest对象，用于获取请求参数和会话信息
     * @param id  系统文件夹的ID，不能为空
     * @return Result对象，表示获取系统文件夹详情的结果，包含SysFolderDetailVO对象
     */
    @AutoLog(value = "获取系统文件夹详情")
    @ApiOperation(value = "获取系统文件夹详情", notes = "获取系统文件夹详情")
    @GetMapping(value = "/detail")
    public Result<SysFolderDetailVO> detail(HttpServletRequest req,
                                            @RequestParam("id") @NotNull(message = "id不能为空") Long id) {
        SysFolderDetailVO result = sysFolderService.detail(req, id);
        return Result.OK(result);
    }

    /**
     * 删除系统文件夹
     *
     * @param req HttpServletRequest对象，用于获取请求参数和会话信息
     * @param ids 系统文件夹ID列表，不能为空
     * @return Result对象，表示删除系统文件夹操作的结果
     */
    @AutoLog(value = "删除系统文件夹", operateType = 4, permissionUrl = "/document/documentManage")
    @ApiOperation(value = "删除系统文件夹", notes = "删除系统文件夹")
    @PostMapping(value = "/delete")
    public Result<?> deleteFolder(HttpServletRequest req, @RequestBody @NotNull(message = "id不能为空") List<Long> ids) {
        sysFolderService.deleteFolder(req, ids);
        return Result.OK("删除成功!");
    }

    /**
     * 构建文件夹的等级和编码和编码层级数据，兼容历史数据
     *
     * @return
     */
    @PostMapping(value = "/builddata")
    public Result<?> buildData() {
        sysFolderService.buildData();
        return Result.OK("编辑成功！");
    }

    /**
     * 重命名文件夹
     *
     * @return 重命名结果
     */
    @AutoLog(value = "重命名文件")
    @ApiOperation(value = "重命名文件", notes = "重命名文件")
    @PutMapping(value = "/renameFolder")
    public Result<SysFile> renameFolder(@RequestParam(value = "id") Long id,
                                      @RequestParam(value = "name") String name) {
        sysFolderService.renameFolder(id, name);
        return Result.OK("重命名文件成功");
    }
}
