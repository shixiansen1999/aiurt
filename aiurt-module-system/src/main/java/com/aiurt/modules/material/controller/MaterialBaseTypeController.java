package com.aiurt.modules.material.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.entity.DeviceAssembly;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.material.service.IMaterialBaseTypeService;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.mapper.CsSubsystemUserMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 设备
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "物资分类")
@RestController
@RequestMapping("/material/materialBaseType")
public class MaterialBaseTypeController {
    @Autowired
    private IMaterialBaseTypeService iMaterialBaseTypeService;
    @Autowired
    private IMaterialBaseService iMaterialBaseService;
    @Autowired
    private ICsSubsystemService csSubsystemService;
    @Autowired
    private ICsMajorService csMajorService;

    /**
     * 分页列表查询
     *
     * @param materialBaseType
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "物资分类-分页列表查询")
    @ApiOperation(value = "物资分类-分页列表查询", notes = "物资分类-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<MaterialBaseType>> queryPageList(MaterialBaseType materialBaseType,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         @RequestParam(name = "majorCode", required = false) String majorCode,
                                                         @RequestParam(name = "systemCode", required = false) String systemCode,
                                                         @RequestParam(name = "id", required = false) String id,
                                                         @RequestParam(name = "baseTypeName", required = false) String baseTypeName,
                                                         @RequestParam(name = "status", required = false) String status,
                                                         HttpServletRequest req) {
        Result<IPage<MaterialBaseType>> result = new Result<IPage<MaterialBaseType>>();
        Map<String, String[]> parameterMap = req.getParameterMap();

        QueryWrapper<MaterialBaseType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", 0);
        if(id != null && !"".equals(id)){
            queryWrapper.and(wrapper->{
                wrapper.eq("id",id);
                wrapper.or().eq("pid",id);
            });
        }
        if(baseTypeName != null && !"".equals(baseTypeName)){
            queryWrapper.like("base_type_name", baseTypeName);
        }
        if(status != null && !"".equals(status)){
            queryWrapper.eq("status", status);
        }
        if(majorCode != null && !"".equals(majorCode)){
            queryWrapper.eq("major_code", majorCode);
        }
        if(systemCode != null && !"".equals(systemCode)){
            queryWrapper.eq("system_code", systemCode);
        }
        queryWrapper.orderByDesc("create_time");
        Page<MaterialBaseType> page = new Page<MaterialBaseType>(pageNo, pageSize);
        IPage<MaterialBaseType> pageList = iMaterialBaseTypeService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 物资分类列表结构查询（无分页。用于左侧树）
     * @param majorCode
     * @param systemCode
     * @param req
     * @return
     */
    @AutoLog(value = "物资分类列表结构查询（无分页。用于左侧树）")
    @ApiOperation(value = "物资分类列表结构查询（无分页。用于左侧树）", notes = "物资分类列表结构查询（无分页。用于左侧树）")
    @GetMapping(value = "/selectList")
    public Result<List<MaterialBaseType>> selectList(
                                               @RequestParam(name = "majorCode", required = false) String majorCode,
                                               @RequestParam(name = "systemCode", required = false) String systemCode,
                                               HttpServletRequest req) {
        Result<List<MaterialBaseType>> result = new Result<List<MaterialBaseType>>();
        List<MaterialBaseType> materialBaseTypeList = iMaterialBaseTypeService.list(new QueryWrapper<MaterialBaseType>().
                eq("major_code",majorCode).eq("system_code",systemCode).eq("del_flag", 0).orderByDesc("create_time"));
        result.setSuccess(true);
        result.setResult(materialBaseTypeList);
        return result;
    }

    /**
     * 左侧树
     * @param req
     * @return
     */
    @AutoLog(value = "物资分类左侧树")
    @ApiOperation(value = "物资分类左侧树")
    @GetMapping(value = "/treeLeft")
    public Result<?> treeLeft(
            HttpServletRequest req) {
        List<CsMajor> majorList = csMajorService.list(new LambdaQueryWrapper<CsMajor>().eq(CsMajor::getDelFlag,0));
        List<CsSubsystem> systemList = csSubsystemService.list(new LambdaQueryWrapper<CsSubsystem>().eq(CsSubsystem::getDelFlag,0));
        List<MaterialBaseType> materialBaseTypeList = iMaterialBaseTypeService.list(new LambdaQueryWrapper<MaterialBaseType>().eq(MaterialBaseType::getDelFlag,0));
        List<MaterialBaseType> materialBaseTypeListres = iMaterialBaseTypeService.treeList(materialBaseTypeList,"0");
        systemList.forEach(csSubsystem -> {
            List sysList = materialBaseTypeListres.stream().filter(materialBaseType-> csSubsystem.getSystemCode().equals(materialBaseType.getSystemCode())).collect(Collectors.toList());
            csSubsystem.setMaterialBaseTypeList(sysList);
        });
        majorList.forEach(major -> {
            List sysList = systemList.stream().filter(system-> system.getMajorCode().equals(major.getMajorCode())).collect(Collectors.toList());
            major.setChildren(sysList);
            List sysListType = materialBaseTypeListres.stream().filter(materialBaseType-> major.getMajorCode().equals(materialBaseType.getMajorCode())&&(materialBaseType.getSystemCode()==null || "".equals(materialBaseType.getSystemCode()))).collect(Collectors.toList());
            major.setMaterialBaseTypeList(sysListType);
        });
        return Result.OK(majorList);
    }

    /**
     * 物资分类树结构查询
     * @param majorCode
     * @param systemCode
     * @param id
     * @param req
     * @return
     */
    @AutoLog(value = "物资分类树结构查询")
    @ApiOperation(value = "物资分类树结构查询")
    @GetMapping(value = "/treeList")
    public Result<List<MaterialBaseType>> treeList(
            @RequestParam(name = "majorCode", required = false) String majorCode,
            @RequestParam(name = "systemCode", required = false) String systemCode,
            @RequestParam(name = "id", required = false) String id,
            HttpServletRequest req) {
        Result<List<MaterialBaseType>> result = new Result<List<MaterialBaseType>>();
        QueryWrapper<MaterialBaseType> queryWrapper = new QueryWrapper<MaterialBaseType>().eq("del_flag", 0);
        if(majorCode != null && !"".equals(majorCode)){
            queryWrapper.eq("major_code", majorCode);
        }
        if(systemCode != null && !"".equals(systemCode)){
            queryWrapper.eq("system_code", systemCode);
        }
        if(id != null && !"".equals(id)){
            queryWrapper.eq("pid", id);
        }
        List<MaterialBaseType> materialBaseTypeList = iMaterialBaseTypeService.list(queryWrapper.orderByDesc("create_time"));
        List<MaterialBaseType> materialBaseTypeListres = iMaterialBaseTypeService.treeList(materialBaseTypeList,id);
        result.setSuccess(true);
        result.setResult(materialBaseTypeListres);
        return result;
    }

    /**
     * 物资分类-添加
     * @param materialBaseType
     * @return
     */
    @AutoLog(value = "物资分类-添加")
    @ApiOperation(value = "物资分类-添加", notes = "物资分类-添加")
    @PostMapping(value = "/add")
    public Result<MaterialBaseType> add(@RequestBody MaterialBaseType materialBaseType) {
        Result<MaterialBaseType> result = new Result<MaterialBaseType>();
        try {
            //code不能重复
            final int count = (int) iMaterialBaseTypeService.count(new LambdaQueryWrapper<MaterialBaseType>().eq(MaterialBaseType::getBaseTypeCode, materialBaseType.getBaseTypeCode()).eq(MaterialBaseType::getDelFlag, 0).last("limit 1"));
            if (count > 0){
                return Result.error("物资分类编号不能重复");
            }
            iMaterialBaseTypeService.save(materialBaseType);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 物资分类详情查询
     * @param id
     * @return
     */
    @ApiOperation(value = "物资分类详情查询", notes = "物资分类详情查询")
    @PostMapping(value = "/queryById")
    public Result<MaterialBaseType> queryById(@RequestParam(name = "id", required = true) String id) {
        MaterialBaseType materialBaseType = iMaterialBaseTypeService.getById(id);
        String pid = materialBaseType.getPid();
        if("0".equals(pid)){
            materialBaseType.setPidName("");
        }else{
            MaterialBaseType pm = iMaterialBaseTypeService.getById(pid);
            materialBaseType.setPidName(pm.getBaseTypeName());
        }
        return Result.ok(materialBaseType);
    }

    /**
     * 物资分类-编辑
     * @param materialBaseType
     * @return
     */
    @AutoLog(value = "物资分类-编辑")
    @ApiOperation(value = "物资分类-编辑", notes = "物资分类-编辑")
    @PutMapping(value = "/edit")
    public Result<MaterialBaseType> edit(@RequestBody MaterialBaseType materialBaseType) {
        Result<MaterialBaseType> result = new Result<MaterialBaseType>();
        MaterialBaseType materialBaseTypeEntity = iMaterialBaseTypeService.getById(materialBaseType.getId());
        if (materialBaseTypeEntity == null) {
            result.onnull("未找到对应实体");
        } else {
            final int count = (int) iMaterialBaseTypeService.count(new LambdaQueryWrapper<MaterialBaseType>().ne(MaterialBaseType::getId,materialBaseType.getId()).eq(MaterialBaseType::getBaseTypeCode, materialBaseType.getBaseTypeCode()).eq(MaterialBaseType::getDelFlag, 0).last("limit 1"));
            if (count > 0){
                return Result.error("物资分类编号不能重复");
            }
            boolean ok = iMaterialBaseTypeService.updateById(materialBaseType);

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

    /**
     * 物资分类-通过id删除
     * @param id
     * @return
     */
    @AutoLog(value = "物资分类-通过id删除")
    @ApiOperation(value = "物资分类-通过id删除", notes = "物资分类-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            //是否有对应的物资在使用该分类
            MaterialBaseType materialBaseType = iMaterialBaseTypeService.getById(id);
            String baseTypeCode = materialBaseType.getBaseTypeCode();
            List<MaterialBase> materialBaseList = iMaterialBaseService.list(new QueryWrapper<MaterialBase>().eq("base_type_code",baseTypeCode).eq("del_flag",0));
            if(materialBaseList != null && materialBaseList.size()>0){
                return Result.error("该物资分类正在使用中，无法删除");
            }
            List<MaterialBaseType> materialBaseTypeList = iMaterialBaseTypeService.list(new QueryWrapper<MaterialBaseType>().eq("pid",id));
            if(materialBaseTypeList != null && materialBaseTypeList.size()>0){
                return Result.error("该物资分类正在使用中，无法删除");
            }
            materialBaseType.setDelFlag(1);
            iMaterialBaseTypeService.updateById(materialBaseType);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    /**
     * 物资分类-批量删除
     * @param ids
     * @return
     */
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
                MaterialBaseType materialBaseType = iMaterialBaseTypeService.getById(id);
                String baseTypeCode = materialBaseType.getBaseTypeCode();
                List<MaterialBase> materialBaseList = iMaterialBaseService.list(new QueryWrapper<MaterialBase>().eq("base_type_code",baseTypeCode).eq("del_flag",0));
                List<MaterialBaseType> materialBaseTypeList = iMaterialBaseTypeService.list(new QueryWrapper<MaterialBaseType>().eq("pid",id));
                if((materialBaseList != null && materialBaseList.size()>0) || (materialBaseTypeList != null && materialBaseTypeList.size()>0)){
                    res += "baseTypeCode,";
                }else{
                    materialBaseType.setDelFlag(1);
                    iMaterialBaseTypeService.updateById(materialBaseType);
                }
            }
            if(res.contains(",")){
                res = res.substring(0,res.length()-1);
                res += "的物资分类因为正在使用中无法删除，其余物资分类删除成功!";
            }else{
                res = "删除成功!";
            }
            result.success(res);
        }
        return result;
    }
}
