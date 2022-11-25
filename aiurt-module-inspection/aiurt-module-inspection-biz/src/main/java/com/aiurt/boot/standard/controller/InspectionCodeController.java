package com.aiurt.boot.standard.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.dto.InspectionCodeDTO;
import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.boot.standard.service.IInspectionCodeService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.enums.ModuleType;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: inspection_code
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
@Api(tags = "检修标准")
@RestController
@RequestMapping("/standard/inspectionCode")
@Slf4j
public class InspectionCodeController extends BaseController<InspectionCode, IInspectionCodeService> {
    @Autowired
    private IInspectionCodeService inspectionCodeService;

    /**
     * 分页列表查询检修标准
     *
     * @param inspectionCodeDTO
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "检修标准表-分页列表查询", operateType =  1, operateTypeAlias = "分页列表查询", module = ModuleType.INSPECTION)
    @ApiOperation(value = "检修标准表-分页列表查询", notes = "检修标准表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<InspectionCodeDTO>> queryPageList(InspectionCodeDTO inspectionCodeDTO,
                                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                          HttpServletRequest req) {
        Page<InspectionCodeDTO> page = new Page<InspectionCodeDTO>(pageNo, pageSize);
        IPage<InspectionCodeDTO> pageList = inspectionCodeService.pageList(page, inspectionCodeDTO);
        return Result.OK(pageList);
    }


    /**
     * 分页列表查询是否配置巡检项
     *
     * @param inspectionCodeDTO
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "检修标准表-分页列表查询是否配置巡检项", operateType =  1, operateTypeAlias = "列表查询筛选是否有检查项", module = ModuleType.INSPECTION)
    @ApiOperation(value = "检修标准表-分页列表查询是否配置巡检项", notes = "检修标准表-分页列表查询是否配置巡检项")
    @GetMapping(value = "/lists")
    public Result<IPage<InspectionCodeDTO>> queryPageLists(InspectionCodeDTO inspectionCodeDTO,
                                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                          HttpServletRequest req) {
        Page<InspectionCodeDTO> page = new Page<InspectionCodeDTO>(pageNo, pageSize);
        IPage<InspectionCodeDTO> pageList = inspectionCodeService.pageLists(page, inspectionCodeDTO);
        return Result.OK(pageList);
    }
    /**
     * 添加
     *
     * @param inspectionCode
     * @return
     */
    @AutoLog(value = "检修标准表-添加", operateType =  2, operateTypeAlias = "添加", module = ModuleType.INSPECTION)
    @ApiOperation(value = "检修标准表-添加", notes = "检修标准表-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody InspectionCode inspectionCode) {
        inspectionCodeService.save(inspectionCode);
        return Result.OK("添加成功！");
    }
    /**
     * 编辑
     *
     * @param inspectionCode
     * @return
     */
    @AutoLog(value = "检修标准表-编辑", operateType =  3, operateTypeAlias = "编辑", module = ModuleType.INSPECTION)
    @ApiOperation(value = "检修标准表-编辑", notes = "检修标准表-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody InspectionCode inspectionCode) {
        Integer isAppointDevice = inspectionCode.getIsAppointDevice();
        if (ObjectUtil.isNotEmpty(isAppointDevice) && InspectionConstant.NO_ISAPPOINT_DEVICE.equals(isAppointDevice)) {
            inspectionCode.setDeviceTypeCode(null);
        }
        inspectionCodeService.updateById(inspectionCode);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "检修标准表-通过id删除", operateType =  4, operateTypeAlias = "通过id删除", module = ModuleType.INSPECTION)
    @ApiOperation(value = "检修标准表-通过id删除", notes = "检修标准表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        inspectionCodeService.updateDelFlag(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "检修标准表-批量删除", operateType =  4, operateTypeAlias = "批量删除", module = ModuleType.INSPECTION)
    @ApiOperation(value = "检修标准表-批量删除", notes = "检修标准表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        List<String> id = Arrays.asList(ids.split(","));
        for (String id1 : id) {
            this.delete(id1);
        }
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "检修标准表-通过id查询", operateType =  1, operateTypeAlias = "通过id查询", module = ModuleType.INSPECTION)
    @ApiOperation(value = "检修标准表-通过id查询", notes = "检修标准表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<InspectionCode> queryById(@RequestParam(name = "id", required = true) String id) {
        InspectionCode inspectionCode = inspectionCodeService.getById(id);
        if (inspectionCode == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(inspectionCode);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param inspectionCode
     */
//    @RequestMapping(value = "/exportXls")
//    public ModelAndView exportXls(HttpServletRequest request, InspectionCode inspectionCode) {
//        return super.exportXls(request, inspectionCode, InspectionCode.class, "inspection_code");
//    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
//    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
//    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
//        return super.importExcel(request, response, InspectionCode.class);
//    }

    /**
     *  生成检修标准表编号
     * @param
     * @return
     */
    @AutoLog(value = "生成检修标准表编号")
    @ApiOperation(value="生成检修标准表编号", notes="生成检修标准表编号")
    @GetMapping(value = "/generatePlanCode")
    public Result<String> generatePlanCode() {
        String code="BZ"+System.currentTimeMillis();
        return Result.OK(code);
    }

    /**
     * 检修标准管理-导出excel
     * @param request
     * @param inspectionCode
     * @return
     */
    @AutoLog(value = "检修标准管理-导出excel",  operateType =  4, operateTypeAlias = "导出excel", module = ModuleType.INSPECTION)
    @ApiOperation(value="检修标准管理-导出excel", notes="检修标准管理-导出excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, InspectionCode inspectionCode) {
        // Step.1 组装查询条件
        QueryWrapper<InspectionCode> queryWrapper = QueryGenerator.initQueryWrapper(inspectionCode, request.getParameterMap());
        queryWrapper.lambda().eq(InspectionCode::getDelFlag, CommonConstant.DEL_FLAG_0);
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<InspectionCode> pageList = inspectionCodeService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "检修标准导出");
        //excel注解对象Class
        mv.addObject(NormalExcelConstants.CLASS, InspectionCode.class);
        //自定义表格参数
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("检修标准导出", "检修标准导出"));
        //导出数据列表
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     *
     * @return
     */
    @AutoLog(value = "检修标准导入模板下载", operateType =  4, operateTypeAlias = "导出excel", module = ModuleType.INSPECTION)
    @ApiOperation(value="检修标准导入模板下载", notes="检修标准导入模板下载")
    @RequestMapping(value = "/exportTemplateXls")
    public ModelAndView exportTemplateXl() {
//        String remark = "检修标准导入模板\n" +
//                "填写须知：\n" +
//                "1.请勿增加、删除、或修改表格中的字段顺序、字段名称；\n" +
//                "2.请严格按照数据规范填写，并填写完所有必填项，红底白字列为必填项；\n" +
//                "字段说明：\n" +
//                "1.厂商名称：必填字段；\n" +
//                "2.厂商等级：必填字段，且与系统下拉项保持一致；\n" +
//                "3.联系电话：选填字段，11位数的手机号码；\n" +
//                "4.企业资质文件：支持PNG、JP图片格式；pdf请在系统中直接上传；";
        return super.exportTemplateXls("", InspectionCode.class,"检修标准导入模板","");
    }

    @AutoLog(value = "检修标准-通过excel导入数据", operateType =  6, operateTypeAlias = "通过excel导入数据", module = ModuleType.INSPECTION)
    @ApiOperation(value="检修标准-通过excel导入数据", notes="检修标准-通过excel导入数据")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws Exception{
        return inspectionCodeService.importExcel(request,response);
    }

}
