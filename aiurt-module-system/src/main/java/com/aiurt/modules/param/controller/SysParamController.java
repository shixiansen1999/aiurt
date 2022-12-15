package com.aiurt.modules.param.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.param.dto.SysParamDTO;
import com.aiurt.modules.param.entity.SysParam;
import com.aiurt.modules.param.service.ISysParamService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @Description: 系统参数配置sys_param
 * @Author: aiurt
 * @Date: 2022-12-15
 * @Version: V1.0
 */
@Api(tags = "系统参数配置")
@RestController
@RequestMapping("/param/sysParam")
@Slf4j
public class SysParamController extends BaseController<SysParam, ISysParamService> {
    @Autowired
    private ISysParamService sysParamService;

    /**
     * 系统参数配置-分页列表查询
     *
     * @param sysParam
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @ApiOperation(value = "系统参数配置-分页列表查询", notes = "系统参数配置-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SysParam>> queryPageList(SysParamDTO sysParamDTO,
                                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                 HttpServletRequest req) {
        Page<SysParam> page = new Page<SysParam>(pageNo, pageSize);
        IPage<SysParam> pageList = sysParamService.queryPageList(page, sysParamDTO);
        return Result.OK(pageList);
    }

    /**
     * 系统参数配置-添加
     *
     * @param sysParam
     * @return
     */
    @ApiOperation(value = "系统参数配置-添加", notes = "系统参数配置-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@Validated(value = SysParam.Save.class) @RequestBody SysParam sysParam) {
        String id = sysParamService.add(sysParam);
        return Result.OK("添加成功！", id);
    }

    /**
     * 系统参数配置-编辑
     *
     * @param sysParam
     * @return
     */
    @ApiOperation(value = "系统参数配置-编辑", notes = "系统参数配置-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@Validated(value = SysParam.Update.class) @RequestBody SysParam sysParam) {
        String id = sysParamService.edit(sysParam);
        return Result.OK("编辑成功!", id);
    }

    /**
     * 系统参数配置-通过id删除
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "系统参数配置-通过id删除", notes = "系统参数配置-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam @ApiParam(name = "id", value = "记录ID") String id) {
        SysParam sysParam = sysParamService.getById(id);
        if (ObjectUtil.isEmpty(sysParam)) {
            return Result.error("未找到对应数据!");
        }
        sysParamService.removeById(sysParam);
        return Result.OK("删除成功!");
    }


    /**
     * 系统参数配置-通过id查询
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "系统参数配置-通过id查询", notes = "系统参数配置-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<SysParam> queryById(@RequestParam @ApiParam(name = "id", value = "记录ID") String id) {
        SysParam sysParam = sysParamService.getById(id);
        if (sysParam == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(sysParam);
    }

    /**
     * 系统参数配置-通过参数编号查询
     *
     * @param code
     * @return
     */
    @ApiOperation(value = "系统参数配置-通过参数编号查询", notes = "系统参数配置-通过参数编号查询")
    @GetMapping(value = "/queryByCode")
    public Result<SysParam> queryByCode(@RequestParam @ApiParam(name = "code", value = "参数编号") String code) {
        SysParam sysParam = sysParamService.lambdaQuery()
                .eq(SysParam::getDelFlag, 0)
                .eq(SysParam::getCode, code)
                .last("limit 1")
                .one();
        if (sysParam == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(sysParam);
    }
//
//    /**
//    * 导出excel
//    *
//    * @param request
//    * @param sysParam
//    */
//    @RequestMapping(value = "/exportXls")
//    public ModelAndView exportXls(HttpServletRequest request, SysParam sysParam) {
//        return super.exportXls(request, sysParam, SysParam.class, "sys_param");
//    }

//    /**
//      * 通过excel导入数据
//    *
//    * @param request
//    * @param response
//    * @return
//    */
//    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
//    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
//        return super.importExcel(request, response, SysParam.class);
//    }

}
