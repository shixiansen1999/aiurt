package com.aiurt.modules.sysfile.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.sysfile.entity.SysFile;
import com.aiurt.modules.sysfile.param.SysFileParam;
import com.aiurt.modules.sysfile.param.SysFileWebParam;
import com.aiurt.modules.sysfile.service.ISysFileManageService;
import com.aiurt.modules.sysfile.vo.FileAppVO;
import com.aiurt.modules.sysfile.service.ISysFolderFilePermissionService;
import com.aiurt.modules.sysfile.vo.SysFileDetailVO;
import com.aiurt.modules.sysfile.vo.SysFileManageVO;
import com.aiurt.modules.sysfile.vo.TypeNameVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author:wgp
 * @create: 2023-05-22 16:57
 * @Description:
 */
@Slf4j
@Api(tags = "文件表")
@RestController
@RequestMapping("/sys/file")
public class SysFileManageController {

    @Resource
    private ISysFileManageService sysFileManageService;
    @Resource
    private ISysFolderFilePermissionService sysFolderFilePermissionService;

    /**
     * 查询文档分页列表
     *
     * @param sysFile
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "查询文档分页列表")
    @ApiOperation(value = "查询文档分页列表", notes = "查询文档分页列表")
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
     * 查询app文档分页列表
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "查询app文档分页列表")
    @ApiOperation(value = "查询app文档分页列表", notes = "查询app文档分页列表")
    @GetMapping(value = "/getAppPageList")
    public Result<IPage<FileAppVO>> getAppPageList(@RequestParam(name = "parentId", required = false) Long parentId,
                                                   @RequestParam(name = "fileName", required = false) String fileName,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<FileAppVO> page = new Page<>(pageNo, pageSize);
        page = sysFileManageService.getAppPageList(page, parentId, fileName);
        return Result.OK(page);
    }

    /**
     * 添加文件
     *
     * @param req   HttpServletRequest对象，用于获取请求相关信息
     * @param files 待添加的文件信息集合
     * @return 添加结果，包含SysFile对象
     */
    @AutoLog(value = "添加文件")
    @ApiOperation(value = "添加文件", notes = "添加文件")
    @PostMapping(value = "/add")
    public Result<SysFile> addFile(HttpServletRequest req, @RequestBody @Validated List<SysFileParam> files) {
        Result<SysFile> result = sysFileManageService.addFile(files);
        return result;
    }

    /**
     * 编辑文件
     *
     * @param req          HttpServletRequest对象，用于获取请求相关信息
     * @param sysFileParam SysFileParam对象，待编辑的文件信息
     * @return 编辑结果，包含SysFile对象
     */
    @AutoLog(value = "编辑文件")
    @ApiOperation(value = "编辑文件", notes = "编辑文件")
    @PutMapping(value = "/edit")
    public Result<SysFile> editFile(HttpServletRequest req, @RequestBody SysFileParam sysFileParam) {
        sysFileManageService.editFile(sysFileParam);
        return Result.OK("编辑文档成功");
    }

    /**
     * 通过文件id删除文件
     *
     * @param id 文件ID，待删除的文件的唯一标识符
     * @return 删除结果，表示文件删除成功与否的通用结果对象
     */
    @AutoLog(value = "通过文件id删除文件", operateType = 4, permissionUrl = "/document/documentManage")
    @ApiOperation(value = "通过文件id删除文件", notes = "通过文件id删除文件")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        int result = sysFileManageService.removeById(id);
        return result > 0 ? Result.OK("删除文档成功") : Result.error("删除文档失败");
    }

    /**
     * 通过id查询文件
     *
     * @param id 文件ID，待查询的文件的唯一标识符
     * @return 查询文件结果
     */
    @AutoLog(value = "通过id查询文件")
    @ApiOperation(value = "通过id查询文件", notes = "通过id查询文件")
    @GetMapping(value = "/queryById")
    public Result<SysFileDetailVO> queryById(@RequestParam(name = "id", required = true) String id) {
        SysFileDetailVO result = sysFileManageService.queryById(id);
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
     * 通过文件夹id查询该文件夹下的文件的类型
     *
     * @param typeId id类型
     * @return {@code Result<List<TypeNameVO>>}
     */
    @AutoLog(value = "通过文件夹id查询该文件夹下的文件的类型")
    @ApiOperation(value = "通过文件夹id查询该文件夹下的文件的类型", notes = "通过文件夹id查询该文件夹下的文件的类型")
    @GetMapping(value = "/queryByTypeId")
    public Result<List<TypeNameVO>> queryByTypeId(@RequestParam(value = "typeId", required = false) Long typeId) {
        List<TypeNameVO> result = sysFileManageService.queryByTypeId(typeId);
        return Result.OK(result);
    }

    /**
     * 构建数据，新版知识库表结构变动
     *
     * @return
     */
    @PostMapping(value = "/builddata")
    public Result<?> buildData() {
        sysFileManageService.buildData();
        return Result.ok("编辑成功！");
    }

    /**
     * 初始化sys_folder_file_permission表数据
     *
     * @return
     */
    @PostMapping(value = "/saveSysFolderFilePermission")
    public Result<?> saveSysFolderFilePermission() {
        sysFolderFilePermissionService.saveSysFolderFilePermission();
        return Result.ok("编辑成功！");
    }

}
