package com.aiurt.modules.material.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.entity.DeviceAssembly;
import com.aiurt.modules.device.entity.DeviceCompose;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.device.service.IDeviceAssemblyService;
import com.aiurt.modules.device.service.IDeviceComposeService;
import com.aiurt.modules.device.service.IDeviceTypeService;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.material.service.IMaterialBaseTypeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: 设备
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "物资")
@RestController
@RequestMapping("/material/materialBase")
public class MaterialBaseController {

    @Autowired
    private IMaterialBaseService iMaterialBaseService;

    @Autowired
    private IMaterialBaseTypeService iMaterialBaseTypeService;

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
    @AutoLog(value = "物资-分页列表查询")
    @ApiOperation(value = "物资-分页列表查询", notes = "物资-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<MaterialBase>> queryPageList(MaterialBase materialBase,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         @RequestParam(name = "majorCode", required = false) String majorCode,
                                                         @RequestParam(name = "systemCode", required = false) String systemCode,
                                                         @RequestParam(name = "baseTypeCode", required = false) String baseTypeCode,
                                                         HttpServletRequest req) {
        Result<IPage<MaterialBase>> result = new Result<IPage<MaterialBase>>();
        Map<String, String[]> parameterMap = req.getParameterMap();

        QueryWrapper<MaterialBase> queryWrapper = QueryGenerator.initQueryWrapper(materialBase, parameterMap);
        queryWrapper.eq("del_flag", 0);
        if(majorCode != null && !"".equals(majorCode)){
            queryWrapper.eq("major_code", majorCode);
        }
        if(systemCode != null && !"".equals(systemCode)){
            queryWrapper.eq("system_code", systemCode);
        }
        if(baseTypeCode != null && !"".equals(baseTypeCode)){
            queryWrapper.apply(" FIND_IN_SET ( "+baseTypeCode+" , REPLACE(base_type_code_cc,'/',',') ");
//            queryWrapper.eq("base_type_code", baseTypeCode);
        }
        queryWrapper.orderByDesc("create_time");
        Page<MaterialBase> page = new Page<MaterialBase>(pageNo, pageSize);
        IPage<MaterialBase> pageList = iMaterialBaseService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @AutoLog(value = "物资-添加")
    @ApiOperation(value = "物资-添加", notes = "物资-添加")
    @PostMapping(value = "/add")
    public Result<MaterialBase> add(@RequestBody MaterialBase materialBase) {
        Result<MaterialBase> result = new Result<MaterialBase>();
        try {
            String majorCode = materialBase.getMajorCode();
            String systemCode = materialBase.getSystemCode()==null?"":materialBase.getSystemCode();
            String baseTypeCode = materialBase.getBaseTypeCode();
            String finalstr = majorCode + systemCode + baseTypeCode;
            String newBaseCode = iMaterialBaseService.getNewBaseCode(finalstr);
            materialBase.setCode(newBaseCode);
            iMaterialBaseService.save(materialBase);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    @AutoLog(value = "物资列表查询-无分页")
    @ApiOperation(value = "物资列表查询-无分页", notes = "物资列表查询-无分页")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(
                                     @RequestParam(name = "majorCode", required = false) String majorCode,
                                     @RequestParam(name = "systemCode", required = false) String systemCode,
                                     @RequestParam(name = "baseTypeCode", required = false) String baseTypeCode,
                                     @RequestParam(name = "code", required = false) String code,
                                     @RequestParam(name = "name", required = false) String name,
                                     HttpServletRequest req) {
        QueryWrapper<MaterialBase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", 0);
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
    @ApiOperation(value = "物资详情查询", notes = "物资详情查询")
    @PostMapping(value = "/queryById")
    public Result<MaterialBase> queryById(@RequestParam(name = "id", required = true) String id) {
        MaterialBase materialBase = iMaterialBaseService.getById(id);
        MaterialBase materialBasefinal = iMaterialBaseService.translate(materialBase);
        return Result.ok(materialBasefinal);
    }

    @AutoLog(value = "物资-编辑")
    @ApiOperation(value = "物资-编辑", notes = "物资-编辑")
    @PutMapping(value = "/edit")
    public Result<MaterialBase> edit(@RequestBody MaterialBase materialBase) {
        Result<MaterialBase> result = new Result<MaterialBase>();
        MaterialBase MaterialBaseEntity = iMaterialBaseService.getById(materialBase.getId());
        if (MaterialBaseEntity == null) {
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

    @AutoLog(value = "物资-通过id删除")
    @ApiOperation(value = "物资-通过id删除", notes = "物资-通过id删除")
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
            materialBase.setDelFlag(1);
            iMaterialBaseService.updateById(materialBase);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    @AutoLog(value = "物资分类-批量删除")
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
                if((deviceComposeList != null && deviceComposeList.size()>0) || (deviceAssemblyList != null && deviceAssemblyList.size()>0)){
                    res += materialBase.getCode() + ",";
                }else{
                    materialBase.setDelFlag(1);
                    iMaterialBaseService.updateById(materialBase);
                }
            }
            if(res.contains(",")){
                res = res.substring(0,res.length()-1);
                res += "的物资因为正在使用中无法删除，其余物资删除成功!";
            }else{
                res = "删除成功!";
            }
            result.success(res);
        }
        return result;
    }
}
