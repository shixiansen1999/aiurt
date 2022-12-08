package com.aiurt.modules.versioninfo.controller;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.versioninfo.entity.VersionInfo;
import com.aiurt.modules.versioninfo.service.IVersionInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Description: bd_version_info
 * @Author: jeecg-boot
 * @Date: 2021-05-10
 * @Version: V1.0
 */
@Api(tags = "App、常用软件版本信息表")
@RestController
@RequestMapping("/app/bdVersionInfo")
@Slf4j
public class VersionInfoController extends BaseController<VersionInfo, IVersionInfoService> {
    @Autowired
    private IVersionInfoService bdVersionInfoService;
    @Autowired
    private ISysBaseAPI sysBaseAPI;


    /**
     * 查询App版本信息
     *
     * @param versionInfo
     * @param req
     * @return
     */
    @AutoLog(value = "列表查询")
    @ApiOperation(value = "列表查询", notes = "列表查询")
    @GetMapping(value = "/list")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = VersionInfo.class)
    })
    public Result<IPage<VersionInfo>> queryPageList(VersionInfo versionInfo,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        Result<IPage<VersionInfo>> result = new Result<IPage<VersionInfo>>();
        QueryWrapper<VersionInfo> queryWrapper = QueryGenerator.initQueryWrapper(versionInfo, req.getParameterMap());
        queryWrapper.orderByDesc("upload_time");
        if(ObjectUtil.isNotEmpty(versionInfo.getVersionId()))
        {
            queryWrapper.like("version_id", String.valueOf(versionInfo.getVersionId()));
        }
        Page<VersionInfo> page = new Page<VersionInfo>(pageNo, pageSize);
        IPage<VersionInfo> pageList = bdVersionInfoService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * app上传与app版本信息添加
     *
     * @param versionInfo
     * @return
     */
    @AutoLog(value = "app上传")
    @ApiOperation(value = "app上传与app版本信息添加", notes = "app上传与app版本信息添加")
    @PostMapping(value = "/upload")
    @ResponseBody
    public Result<?> upload(@Valid @RequestBody VersionInfo versionInfo) {
/*        QueryWrapper<BdVersionInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time").last("limit 1");
        BdVersionInfo bdVersionInfo1 = bdVersionInfoService.getBaseMapper().selectOne(queryWrapper);*/
        LambdaQueryWrapper<VersionInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VersionInfo::getVersionId, versionInfo.getVersionId());
        long count = bdVersionInfoService.count(queryWrapper);
        if (count > 0) {
            return Result.OK("版本号重复，请更改版本号");
        } else if (StrUtil.isNotBlank(versionInfo.getAndroidUrl())) {
            bdVersionInfoService.insertAppInfo(versionInfo);
            return Result.OK("文件上传成功");
        }
        return Result.error("文件上传失败，请重新上传");
    }

    /**
     * 添加
     *
     * @param versionInfo
     * @return
     */
    @AutoLog(value = "添加")
    @ApiOperation(value = "添加", notes = "添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody VersionInfo versionInfo) {
        String s =versionInfo.getAndroidPackageName();
        List<String> androidFile = Arrays.asList(s.split("\\."));
        VersionInfo versionInfo1 = bdVersionInfoService.selectLatest();
        if(ObjectUtil.isNotEmpty(versionInfo1))
        {
            int count = versionInfo.getVersionId() - versionInfo1.getVersionId();
            if (count !=1) {
                return Result.OK("版本号需要比之前版本大一版，请更改版本号");
            }
        }

            if(!androidFile.get(androidFile.size()-1).equals("apk"))
            {
               return Result.error("文件上传错误，不是.apk文件");
            }

        versionInfo.setUploadTime(new Date());
        bdVersionInfoService.save(versionInfo);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param versionInfo
     * @return
     */
    @AutoLog(value = "编辑")
    @ApiOperation(value = "编辑", notes = "编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@Valid @RequestBody VersionInfo versionInfo) {
        bdVersionInfoService.updateById(versionInfo);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "删除")
    @ApiOperation(value = "通过id删除", notes = "通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        VersionInfo byId = bdVersionInfoService.getById(id);
        if (!Objects.isNull(byId)) {
            String url = byId.getAndroidUrl();
//后期修改
//            sysBaseAPI.deleteFile(url);
            bdVersionInfoService.removeById(id);
            return Result.OK("删除成功!");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
   // @AutoLog(value = "bd_version_info-批量删除")
    @ApiOperation(value = "bd_version_info-批量删除", notes = "bd_version_info-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.bdVersionInfoService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 查看app最新版本
     *
     * @return
     */
     @AutoLog(value = "查看app最新版本")
    @ApiOperation(value = "查看app最新版本", notes = "查看app最新版本")
    @GetMapping(value = "/selectLatest")
    public Result<?> selectLatest() {
          VersionInfo versionInfo = bdVersionInfoService.selectLatest();
         return Result.OK(versionInfo);
    }
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "查看详情", permissionUrl = "/config/BdVersionInfoList")
    @ApiOperation(value = "bd_version_info-通过id查询", notes = "bd_version_info-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        VersionInfo versionInfo = bdVersionInfoService.getById(id);
        if (versionInfo == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(versionInfo);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param versionInfo
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, VersionInfo versionInfo) {
        return super.exportXls(request, versionInfo, VersionInfo.class, "bd_version_info");
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
        return super.importExcel(request, response, VersionInfo.class);
    }



    @RequestMapping("/checkUpdateApp")
    @ApiImplicitParams({
            @ApiImplicitParam(name="version", value="版本号", required = true, paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "有新版本升级", response = VersionInfo.class),
            @ApiResponse(code = 200001, message = "已是最新版本", response = VersionInfo.class)

    })
    @ApiOperation("app更新")
    public Result<VersionInfo> checkUpdateApp(@RequestParam(value = "version") String version) {
        Result<VersionInfo> bdVersionInfoResult = new Result<>();
        VersionInfo versionInfo = bdVersionInfoService.checkUpdateApp(version);

        if (Objects.isNull(versionInfo)) {
            versionInfo = new VersionInfo();
            bdVersionInfoResult.setCode(200001);
            bdVersionInfoResult.setMessage("已是最新版本");
        }else {
            bdVersionInfoResult.setMessage("有新版本升级");
        }
        bdVersionInfoResult.setResult(versionInfo);
        return bdVersionInfoResult;
    }

}
