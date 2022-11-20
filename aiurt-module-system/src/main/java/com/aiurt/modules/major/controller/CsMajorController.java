package com.aiurt.modules.major.controller;


import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.boot.standard.service.IInspectionCodeService;
import com.aiurt.boot.standard.service.IPatrolStandardService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.device.service.IDeviceTypeService;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.service.IMaterialBaseTypeService;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import java.io.IOException;
import java.util.List;


/**
 * @Description: cs_major
 * @Author: jeecg-boot
 * @Date: 2022-06-21
 * @Version: V1.0
 */
@Api(tags = "系统管理-基础数据-专业")
@RestController
@RequestMapping("/major")
@Slf4j
public class CsMajorController extends BaseController<CsMajor, ICsMajorService> {
    @Autowired
    private ICsMajorService csMajorService;
    @Autowired
    private ICsSubsystemService csSubsystemService;
    @Autowired
    private IMaterialBaseTypeService materialBaseTypeService;
    @Autowired
    private IDeviceTypeService deviceTypeService;
    @Autowired
    private IPatrolStandardService patrolStandardService;
    @Autowired
    private IInspectionCodeService inspectionCodeService;
    @Autowired
    private IFaultService faultService;

    /**
     * 分页列表查询
     *
     * @param csMajor
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "查询", operateType = 1, operateTypeAlias = "专业分页列表查询", permissionUrl = "/major/list")
    @ApiOperation(value = "专业分页列表查询", notes = "专业分页列表查询")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "manage/MajorList")
    public Result<IPage<CsMajor>> queryPageList(CsMajor csMajor,
                                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                HttpServletRequest req) {
        QueryWrapper<CsMajor> queryWrapper = QueryGenerator.initQueryWrapper(csMajor, req.getParameterMap());
        Page<CsMajor> page = new Page<CsMajor>(pageNo, pageSize);
        IPage<CsMajor> pageList = csMajorService.page(page, queryWrapper.lambda().eq(CsMajor::getDelFlag, CommonConstant.DEL_FLAG_0));
        return Result.OK(pageList);
    }

    /**
     * 不分页列表查询
     *
     * @param csMajor
     * @param req
     * @return
     */
    @AutoLog(value = "查询", operateType = 1, operateTypeAlias = "专业不分页列表查询", permissionUrl = "/major/list")
    @ApiOperation(value = "专业列表查询", notes = "专业列表查询")
    @GetMapping(value = "/selectList")
    public Result<?> selectList(CsMajor csMajor,
                                HttpServletRequest req) {
        QueryWrapper<CsMajor> queryWrapper = QueryGenerator.initQueryWrapper(csMajor, req.getParameterMap());
        List<CsMajor> pageList = csMajorService.list(queryWrapper.lambda().eq(CsMajor::getDelFlag, CommonConstant.DEL_FLAG_0));
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param csMajor
     * @return
     */
    @AutoLog(value = "添加", operateType = 2, operateTypeAlias = "添加专业", permissionUrl = "/major/list")
    @ApiOperation(value = "专业添加", notes = "专业添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody CsMajor csMajor) {
        return csMajorService.add(csMajor);
    }

    /**
     * 编辑
     *
     * @param csMajor
     * @return
     */
    @AutoLog(value = "编辑", operateType = 3, operateTypeAlias = "编辑专业", permissionUrl = "/major/list")
    @ApiOperation(value = "专业编辑", notes = "专业编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody CsMajor csMajor) {
        return csMajorService.update(csMajor);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "删除", operateType = 4, operateTypeAlias = "通过id删除专业", permissionUrl = "/major/list")
    @ApiOperation(value = "专业通过id删除", notes = "专业通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        CsMajor csMajor = csMajorService.getById(id);
        //判断是否被子系统使用
        LambdaQueryWrapper<CsSubsystem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CsSubsystem::getMajorCode, csMajor.getMajorCode());
        wrapper.eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsSubsystem> list = csSubsystemService.list(wrapper);
        if (!list.isEmpty()) {
            return Result.error("该专业被子系统使用中，不能删除!");
        }
        //判断是否被设备类型使用
        LambdaQueryWrapper<DeviceType> deviceWrapper = new LambdaQueryWrapper<>();
        deviceWrapper.eq(DeviceType::getMajorCode, csMajor.getMajorCode());
        deviceWrapper.eq(DeviceType::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<DeviceType> deviceList = deviceTypeService.list(deviceWrapper);
        if (!deviceList.isEmpty()) {
            return Result.error("该专业被设备类型使用中，不能删除!");
        }
        //判断是否被物资分类使用
        LambdaQueryWrapper<MaterialBaseType> materWrapper = new LambdaQueryWrapper<>();
        materWrapper.eq(MaterialBaseType::getMajorCode, csMajor.getMajorCode());
        materWrapper.eq(MaterialBaseType::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<MaterialBaseType> materList = materialBaseTypeService.list(materWrapper);
        if (!materList.isEmpty()) {
            return Result.error("该专业被物资分类使用中，不能删除!");
        }
        // 判断是否被巡检标准使用
        LambdaQueryWrapper<PatrolStandard> standardWrapper = new LambdaQueryWrapper<>();
        standardWrapper.eq(PatrolStandard::getProfessionCode, csMajor.getMajorCode());
        standardWrapper.eq(PatrolStandard::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<PatrolStandard> standardList = patrolStandardService.list(standardWrapper);
        if (!standardList.isEmpty()) {
            return Result.error("该专业被巡检标准使用中，不能删除!");
        }
        // 判断是否被检修标准使用
        LambdaQueryWrapper<InspectionCode> insWrapper = new LambdaQueryWrapper<>();
        insWrapper.eq(InspectionCode::getMajorCode, csMajor.getMajorCode());
        insWrapper.eq(InspectionCode::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<InspectionCode> insList = inspectionCodeService.list(insWrapper);
        if (!insList.isEmpty()) {
            return Result.error("该专业被检修标准使用中，不能删除!");
        }
        // 判断是否被故障上报使用
        LambdaQueryWrapper<Fault> faultWrapper = new LambdaQueryWrapper<>();
        faultWrapper.eq(Fault::getMajorCode, csMajor.getMajorCode());
        List<Fault> faultList = faultService.list(faultWrapper);
        if (!faultList.isEmpty()) {
            return Result.error("该专业被故障上报使用中，不能删除!");
        }
        csMajor.setDelFlag(CommonConstant.DEL_FLAG_1);
        csMajorService.updateById(csMajor);
        return Result.OK("删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "查询", operateType = 1, operateTypeAlias = "通过id查询专业", permissionUrl = "/major/list")
    @ApiOperation(value = "专业通过id查询", notes = "专业通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        CsMajor csMajor = csMajorService.getById(id);
        if (csMajor == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(csMajor);
    }
    /**
     * 下载专业导入模板
     *
     * @param response
     * @param request
     * @throws IOException
     */
    @AutoLog(value = "下载专业导入模板")
    @ApiOperation(value = "下载专业导入模板", notes = "下载专业导入模板")
    @RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
    public ModelAndView downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String remark ="专业信息导入模板\n" +
                "填写须知：\n" +
                "1.请勿增加、删除、或修改表格中的字段顺序、字段名称；\n" +
                "2.请严格按照数据规范填写，并填写完所有必填项，红底白字列为必填项；\n" +
                "字段说明：\n" +
                "1.角色编号：必填字段，且不能重复，支持数字、英文字母、符号等；\n" +
                "2.角色名称：必填字段，且不能重复，支持数字、英文字母、符号等；";
        return super.exportTemplateXls("", CsMajor.class, "专业信息导入模板",remark);
    }

    /**
     * 专业导出
     *
     * @param csMajor
     * @param request
     * @return
     */
    @AutoLog(value = "专业导出")
    @ApiOperation(value = "专业导出", notes = "专业导出")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(CsMajor csMajor, HttpServletRequest request) {
        // Step.1 组装查询条件
        QueryWrapper<CsMajor> queryWrapper = QueryGenerator.initQueryWrapper(csMajor, request.getParameterMap());
        queryWrapper.lambda().eq(CsMajor::getDelFlag, CommonConstant.DEL_FLAG_0);
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<CsMajor> pageList = csMajorService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "专业导出");
        //excel注解对象Class
        mv.addObject(NormalExcelConstants.CLASS, CsMajor.class);
        //自定义表格参数
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("专业导出", "专业导出"));
        //导出数据列表
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @ApiOperation(value = "导入数据", notes = "导入数据")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
       return csMajorService.importExcel(request,response);
    }

}
