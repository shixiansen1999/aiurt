package com.aiurt.modules.material.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.SymbolConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.entity.DeviceAssembly;
import com.aiurt.modules.device.entity.DeviceCompose;
import com.aiurt.modules.device.service.IDeviceAssemblyService;
import com.aiurt.modules.device.service.IDeviceComposeService;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.material.service.IMaterialBaseTypeService;
import com.aliyuncs.CommonResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.xmlbeans.impl.validator.ValidatorUtil;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 设备
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "系统管理-基础数据管理-物资主数据")
@RestController
@RequestMapping("/material/materialBase")
public class MaterialBaseController {

    @Autowired
    private IMaterialBaseService iMaterialBaseService;

    @Autowired
    private IDeviceComposeService iDeviceComposeService;

    @Autowired
    private IDeviceAssemblyService iDeviceAssemblyService;

    /**
     * 分页列表查询
     *
     * @param materialBase
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "系统管理-基础数据管理-物资主数据-分页列表查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/manage/MainMaterialClassification")
    @ApiOperation(value = "系统管理-基础数据管理-物资主数据-分页列表查询", notes = "系统管理-基础数据管理-物资主数据-分页列表查询")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "manage/MainMaterialClassification")
    public Result<IPage<MaterialBase>> queryPageList(MaterialBase materialBase,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         @RequestParam(name = "majorCode", required = false) String majorCode,
                                                         @RequestParam(name = "systemCode", required = false) String systemCode,
                                                         @RequestParam(name = "baseTypeCode", required = false) String baseTypeCode,
                                                         @RequestParam(name = "code", required = false) String code,
                                                         @RequestParam(name = "name", required = false) String name,
                                                         @RequestParam(name = "type", required = false) String type,
                                                         HttpServletRequest req) {
        Result<IPage<MaterialBase>> result = new Result<IPage<MaterialBase>>();
        QueryWrapper<MaterialBase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
        if(majorCode != null && !"".equals(majorCode)){
            queryWrapper.eq("major_code", majorCode);
            if(systemCode != null && !"".equals(systemCode)){
                queryWrapper.eq("system_code", systemCode);
            }else{
                queryWrapper.apply(" (system_code is null or system_code ='') ");
            }
        }
        if(code != null && !"".equals(code)){
            queryWrapper.like("code", code);
        }
        if(name != null && !"".equals(name)){
            queryWrapper.like("name", name);
        }
        if(type != null && !"".equals(type)){
            queryWrapper.eq("type", type);
        }
        if(baseTypeCode != null && !"".equals(baseTypeCode)){
            queryWrapper.apply(" FIND_IN_SET ( '"+baseTypeCode+"' , REPLACE(base_type_code_cc,'/',',')) ");
        }
        queryWrapper.orderByDesc("create_time");
        Page<MaterialBase> page = new Page<MaterialBase>(pageNo, pageSize);
        IPage<MaterialBase> pageList = iMaterialBaseService.page(page, queryWrapper);
        List<MaterialBase> records = pageList.getRecords();
        if(records != null && records.size()>0){
            for(MaterialBase materialBase1 : records){
                MaterialBase translate = iMaterialBaseService.translate(materialBase1);
                BeanUtils.copyProperties(translate, materialBase1);
            }
        }
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 分页列表查询
     *
     * @param materialBase
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "系统管理-基础数据管理-物资主数据-分页列表查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/manage/MainMaterialClassification")
    @ApiOperation(value = "系统管理-基础数据管理-物资主数据-分页列表查询", notes = "系统管理-基础数据管理-物资主数据-分页列表查询")
    @GetMapping(value = "/listLevel2Stock")
    @PermissionData(pageComponent = "manage/MainMaterialClassification")
    public Result<IPage<MaterialBase>> listLevel2Stock(MaterialBase materialBase,
                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                     @RequestParam(name = "majorCode", required = false) String majorCode,
                                                     @RequestParam(name = "systemCode", required = false) String systemCode,
                                                     @RequestParam(name = "baseTypeCode", required = false) String baseTypeCode,
                                                     @RequestParam(name = "code", required = false) String code,
                                                     @RequestParam(name = "name", required = false) String name,
                                                     @RequestParam(name = "type", required = false) String type,
                                                     @RequestParam(name = "codeList", required = false) String codeList,
                                                     HttpServletRequest req) {
        Result<IPage<MaterialBase>> result = new Result<IPage<MaterialBase>>();
        QueryWrapper<MaterialBase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
        if(majorCode != null && !"".equals(majorCode)){
            queryWrapper.eq("major_code", majorCode);
            if(systemCode != null && !"".equals(systemCode)){
                queryWrapper.eq("system_code", systemCode);
            }else{
                queryWrapper.apply(" (system_code is null or system_code ='') ");
            }
        }
        if(codeList != null && !"".equals(codeList)){
            queryWrapper.notIn("code", Arrays.asList(codeList.split(",")));
        }
        if(code != null && !"".equals(code)){
            queryWrapper.like("code", code);
        }
        if(name != null && !"".equals(name)){
            queryWrapper.like("name", name);
        }
        if(type != null && !"".equals(type)){
            queryWrapper.eq("type", type);
        }
        if(baseTypeCode != null && !"".equals(baseTypeCode)){
            queryWrapper.apply(" FIND_IN_SET ( '"+baseTypeCode+"' , REPLACE(base_type_code_cc,'/',',')) ");
        }
        queryWrapper.orderByDesc("create_time");
        Page<MaterialBase> page = new Page<MaterialBase>(pageNo, pageSize);
        IPage<MaterialBase> pageList = iMaterialBaseService.page(page, queryWrapper);
        List<MaterialBase> records = pageList.getRecords();
        if(records != null && records.size()>0){
            for(MaterialBase materialBase1 : records){
                MaterialBase translate = iMaterialBaseService.translate(materialBase1);
                BeanUtils.copyProperties(translate, materialBase1);
            }
        }
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @AutoLog(value = "系统管理-基础数据管理-物资主数据-添加", operateType = 2, operateTypeAlias = "添加", permissionUrl = "/manage/MainMaterialClassification")
    @ApiOperation(value = "系统管理-基础数据管理-物资主数据-添加", notes = "系统管理-基础数据管理-物资主数据-添加")
    @PostMapping(value = "/add")
    public Result<MaterialBase> add(@RequestBody MaterialBase materialBase) {
        Result<MaterialBase> result = new Result<MaterialBase>();
        try {
            final int count = (int) iMaterialBaseService.count(new LambdaQueryWrapper<MaterialBase>().eq(MaterialBase::getCode, materialBase.getCode()).eq(MaterialBase::getDelFlag, 0).last("limit 1"));
            if (count > 0){
                return Result.error("物资编号不能重复");
            }
            String baseTypeCodeCc = materialBase.getBaseTypeCodeCc()==null?"":materialBase.getBaseTypeCodeCc();
            String baseTypeCode = iMaterialBaseService.getCodeByCc(baseTypeCodeCc);
            materialBase.setBaseTypeCode(baseTypeCode);
            iMaterialBaseService.save(materialBase);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    @AutoLog(value = "系统管理-基础数据管理-物资主数据-物资列表查询（无分页）", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/manage/MainMaterialClassification")
    @ApiOperation(value = "物资列表查询-无分页", notes = "物资列表查询-无分页")
    @GetMapping(value = "/listnoPage")
    public Result<?> queryPageList(
                                     @RequestParam(name = "majorCode", required = false) String majorCode,
                                     @RequestParam(name = "systemCode", required = false) String systemCode,
                                     @RequestParam(name = "baseTypeCode", required = false) String baseTypeCode,
                                     @RequestParam(name = "code", required = false) String code,
                                     @RequestParam(name = "name", required = false) String name,
                                     @RequestParam(name = "type", required = false) String type,
                                     HttpServletRequest req) {
        QueryWrapper<MaterialBase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
        if(majorCode != null && !"".equals(majorCode)){
            queryWrapper.eq("major_code", majorCode);
        }
        if(systemCode != null && !"".equals(systemCode)){
            queryWrapper.eq("system_code", systemCode);
        }
        if(baseTypeCode != null && !"".equals(baseTypeCode)){
            queryWrapper.eq("base_type_code", baseTypeCode);
        }
        if(code != null && !"".equals(code)){
            queryWrapper.like("code", code);
        }
        if(name != null && !"".equals(name)){
            queryWrapper.like("name", name);
        }
        if(type != null && !"".equals(type)){
            queryWrapper.eq("type", type);
        }
        queryWrapper.orderByDesc("create_time");
        List<MaterialBase> pageList = iMaterialBaseService.list(queryWrapper);
        List<MaterialBase> listres = new ArrayList<>();
        if(pageList != null && pageList.size()>0){
            for(MaterialBase materialBase1 : pageList){
                MaterialBase materialBase2 = iMaterialBaseService.translate(materialBase1);
                listres.add(materialBase2);
            }
        }
        return Result.OK(listres);
    }

    /**
     * 物资详情查询
     * @param id
     * @return
     */
    @AutoLog(value = "系统管理-基础数据管理-物资主数据-详情查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/manage/MainMaterialClassification")
    @ApiOperation(value = "物资详情查询", notes = "物资详情查询")
    @GetMapping(value = "/queryById")
    public Result<MaterialBase> queryById(@RequestParam(name = "id", required = true) String id) {
        MaterialBase materialBase = iMaterialBaseService.getById(id);
        MaterialBase materialBasefinal = iMaterialBaseService.translate(materialBase);
        return Result.ok(materialBasefinal);
    }

    @AutoLog(value = "系统管理-基础数据管理-物资主数据-编辑", operateType = 3, operateTypeAlias = "修改", permissionUrl = "/manage/MainMaterialClassification")
    @ApiOperation(value = "系统管理-基础数据管理-物资主数据-编辑", notes = "系统管理-基础数据管理-物资主数据-编辑")
    @PutMapping(value = "/edit")
    public Result<MaterialBase> edit(@RequestBody MaterialBase materialBase) {
        Result<MaterialBase> result = new Result<MaterialBase>();
        MaterialBase materialBaseEntity = iMaterialBaseService.getById(materialBase.getId());
        if (materialBaseEntity == null) {
            result.onnull("未找到对应实体");
        } else {
            boolean ok = iMaterialBaseService.updateById(materialBase);
            try{
            }catch (Exception e){
                throw new AiurtBootException(e.getMessage());
            }
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    @AutoLog(value = "系统管理-基础数据管理-物资主数据-通过id删除", operateType = 4, operateTypeAlias = "删除", permissionUrl = "/manage/MainMaterialClassification")
    @ApiOperation(value = "系统管理-基础数据管理-物资主数据-通过id删除", notes = "系统管理-基础数据管理-物资主数据-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            MaterialBase materialBase = iMaterialBaseService.getById(id);
            String code = materialBase.getCode();
            //是否有对应的设备类型在使用该物资
            List<DeviceCompose> deviceComposeList = iDeviceComposeService.list(new QueryWrapper<DeviceCompose>().eq("material_code",code));
            if(deviceComposeList != null && deviceComposeList.size()>0){
                return Result.error("该物资正在使用中，无法删除");
            }
            //是否有对应的设备组件在使用该物资
            List<DeviceAssembly> deviceAssemblyList = iDeviceAssemblyService.list(new QueryWrapper<DeviceAssembly>().eq("material_code",code));
            if(deviceAssemblyList != null && deviceAssemblyList.size()>0){
                return Result.error("该物资正在使用中，无法删除");
            }
            iMaterialBaseService.removeById(materialBase);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    @AutoLog(value = "系统管理-基础数据管理-物资主数据-批量删除", operateType = 4, operateTypeAlias = "删除", permissionUrl = "/manage/MainMaterialClassification")
    @ApiOperation(value = "物资分类-批量删除", notes = "物资分类-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<String> result = new Result<String>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            String res = "编号为";
            List<String> strings = Arrays.asList(ids.split(","));
            for(String id : strings){
                MaterialBase materialBase = iMaterialBaseService.getById(id);
                String code = materialBase.getCode();
                //是否有对应的设备组件在使用该物资
                List<DeviceAssembly> deviceAssemblyList = iDeviceAssemblyService.list(new QueryWrapper<DeviceAssembly>().eq("material_code",code));
                //是否有对应的设备类型在使用该物资
                List<DeviceCompose> deviceComposeList = iDeviceComposeService.list(new QueryWrapper<DeviceCompose>().eq("material_code",code));
                boolean f = (deviceComposeList != null && deviceComposeList.size()>0);
                boolean d = (deviceAssemblyList != null && deviceAssemblyList.size()>0);
                if( f||d ){
                    res += materialBase.getCode() + ",";
                }else{
                    iMaterialBaseService.removeById(materialBase);
                }
            }
            if(res.contains(SymbolConstant.COMMA)){
                res = res.substring(0,res.length()-1);
                res += "的物资因为正在使用中无法删除，其余物资删除成功!";
            }else{
                res = "删除成功!";
            }
            result.success(res);
        }
        return result;
    }
    /**
     * 导出excel
     */
    @ApiOperation(value = "导出excel", notes = "导出excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(MaterialBase materialBase,
                                  @RequestParam(name = "majorCode", required = false) String majorCode,
                                  @RequestParam(name = "systemCode", required = false) String systemCode,
                                  @RequestParam(name = "baseTypeCode", required = false) String baseTypeCode,
                                  @RequestParam(name = "code", required = false) String code,
                                  @RequestParam(name = "name", required = false) String name,
                                  @RequestParam(name = "type", required = false) String type,
                                  HttpServletRequest req) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        Result<IPage<MaterialBase>>s  = this.queryPageList(materialBase,1,9999,majorCode,systemCode,baseTypeCode,code,name,type,req);
        IPage<MaterialBase>IPages=s.getResult();
        List<MaterialBase>list =IPages.getRecords();
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "物资主数据");
        mv.addObject(NormalExcelConstants.CLASS, MaterialBase.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("物资主数据列表数据", "导出人:" + user.getRealname(), "导出信息");
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    /**
     * 通过excel导入数据
     * @param request
     * @param response
     * @return
     */
    @AutoLog(value = "系统管理-基础数据管理-物资主数据-导入", operateType = 5, operateTypeAlias = "导入", permissionUrl = "/manage/MainMaterialClassification")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(1);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                return iMaterialBaseService.importExcelMaterial(file, params);
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

    @AutoLog(value = "系统管理-基础数据管理-物资主数据-下载物资导入模板", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/manage/MainMaterialClassification")
    @ApiOperation(value = "下载物资导入模板", notes = "下载物资导入模板")
    @RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
    public void downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
        ClassPathResource classPathResource =  new ClassPathResource("templates/materialBase.xlsx");
        InputStream bis = classPathResource.getInputStream();
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        int len = 0;
        while ((len = bis.read()) != -1) {
            out.write(len);
            out.flush();
        }
        out.close();
    }
}
