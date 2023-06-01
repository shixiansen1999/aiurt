package com.aiurt.modules.floodpreventioninformation.controller;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.floodpreventioninformation.entity.FloodPreventionInformation;
import com.aiurt.modules.floodpreventioninformation.service.IFloodPreventionInformationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * @Description: flood_prevention_information
 * @Author: zwl
 * @Date:   2023-04-24
 * @Version: V1.0
 */
@Api(tags="防汛信息")
@RestController
@RequestMapping("/flood/prevention/information")
@Slf4j
public class FloodPreventionInformationController extends BaseController<FloodPreventionInformation, IFloodPreventionInformationService> {

    @Autowired
    private IFloodPreventionInformationService iFloodPreventionInformationService;

    @Autowired
    private ISysBaseAPI baseApi;


    @AutoLog(value = "查询防汛信息",operateType = 1,operateTypeAlias = "查询防汛信息",permissionUrl = "/flood/prevention/information/list")
    @ApiOperation(value="查询防汛信息", notes="查询防汛信息")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "system/floodPrevention")
    public Result<IPage<FloodPreventionInformation>> list(FloodPreventionInformation floodPreventionInformation,
                                          @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                          @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        Page<FloodPreventionInformation> page = new Page<FloodPreventionInformation>(pageNo, pageSize);
        IPage<FloodPreventionInformation> pageList = iFloodPreventionInformationService.getList(page, floodPreventionInformation);
        return Result.OK(pageList);
    }



    /**
     * 添加
     * @param floodPreventionInformation
     * @return
     */
    @AutoLog(value = "防汛信息-添加", operateType = 2, operateTypeAlias = "防汛信息-添加", permissionUrl = "/flood/prevention/information/add")
    @ApiOperation(value = "防汛信息-添加", notes = "防汛信息-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody FloodPreventionInformation floodPreventionInformation) {
        try {
             if (StrUtil.isNotBlank(floodPreventionInformation.getStationCode())){
                 floodPreventionInformation.setStationName(baseApi.getPosition(floodPreventionInformation.getStationCode()));
             }
            iFloodPreventionInformationService.save(floodPreventionInformation);
        }catch (Exception e) {
            log.error("新增失败！", e.getMessage());
            return Result.error("新增失败!");
        }
        return Result.ok("新增成功!");
    }

    /**
     * 编辑
     * @param floodPreventionInformation
     * @return
     */
    @AutoLog(value = "防汛信息-编辑", operateType = 2, operateTypeAlias = "防汛信息-编辑", permissionUrl = "/flood/prevention/information/edit")
    @ApiOperation(value = "防汛信息-编辑", notes = "防汛信息-编辑")
    @PostMapping(value = "/edit")
    public Result<?> edit(@RequestBody FloodPreventionInformation floodPreventionInformation) {
        try {
            if (StrUtil.isNotBlank(floodPreventionInformation.getStationCode())){
                floodPreventionInformation.setStationName(baseApi.getPosition(floodPreventionInformation.getStationCode()));
            }
            iFloodPreventionInformationService.updateById(floodPreventionInformation);
        }catch (Exception e) {
            log.error("编辑失败！", e.getMessage());
            return Result.error("编辑失败!");
        }
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "防汛信息-通过id删除", operateType = 4, operateTypeAlias = "防汛信息-通过id删除", permissionUrl = "/isystem/floodPrevention")
    @ApiOperation(value = "防汛信息-通过id删除", notes = "防汛信息-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            QueryWrapper<FloodPreventionInformation> deviceQueryWrapper = new QueryWrapper<>();
            deviceQueryWrapper.eq("id", id);
            FloodPreventionInformation floodPreventionInformation = iFloodPreventionInformationService.getOne(deviceQueryWrapper);
            iFloodPreventionInformationService.removeById(floodPreventionInformation);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }


    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "防汛信息-批量删除", operateType = 4, operateTypeAlias = "防汛信息-批量删除", permissionUrl = "/isystem/floodPrevention")
    @ApiOperation(value = "防汛信息-批量删除", notes = "防汛信息-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<String> result = new Result<String>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            iFloodPreventionInformationService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @ApiOperation(value = "防汛信息-通过excel导入数据", notes = "防汛信息-通过excel导入数据")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return iFloodPreventionInformationService.importExcel(request,response);
    }


    /**
     * 下载模板
     */
    @AutoLog(value = "防汛信息-下载模板", operateType =  6, operateTypeAlias = "防汛信息-下载模板", permissionUrl = "")
    @ApiOperation(value="防汛信息-下载模板", notes="防汛信息-下载模板")
    @RequestMapping(value = "/exportTemplateXls",method = RequestMethod.GET)
    public void exportTemplateXl(HttpServletResponse response, HttpServletRequest request) throws IOException {
          iFloodPreventionInformationService.exportTemplateXl(response);
    }

}
