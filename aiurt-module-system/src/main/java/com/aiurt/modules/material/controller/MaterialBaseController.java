package com.aiurt.modules.material.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.entity.DeviceType;
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
        if(baseTypeCode != null && !"".equals(baseTypeCode)){
            queryWrapper.eq("base_type_code", baseTypeCode);
        }
        if(majorCode != null && !"".equals(majorCode)){
            queryWrapper.eq("major_code", majorCode);
        }
        if(systemCode != null && !"".equals(systemCode)){
            queryWrapper.eq("system_code", systemCode);
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
//            MaterialBase materialBase = iMaterialBaseService.getById(id);
//            String code = materialBase.getCode();
//            //是否有对应的设备类型在使用该物资
//            List<DeviceType> materialBaseList = iMaterialBaseService.list(new QueryWrapper<MaterialBase>().eq("base_type_code",baseTypeCode).eq("del_flag",0));
//            if(materialBaseList != null && materialBaseList.size()>0){
//                return Result.error("该物资分类正在使用中，无法删除");
//            }
//            //是否有对应的设备组件在使用该物资
//            materialBaseType.setDelFlag(1);
//            iMaterialBaseTypeService.updateById(materialBaseType);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    @AutoLog(value = "物资分类-批量删除")
    @ApiOperation(value = "物资分类-批量删除", notes = "物资分类-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<Device> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<Device> result = new Result<Device>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            String res = "编号为";
            List<String> strings = Arrays.asList(ids.split(","));
            for(String id : strings){
                MaterialBaseType materialBaseType = iMaterialBaseTypeService.getById(id);
                String baseTypeCode = materialBaseType.getBaseTypeCode();
                List<MaterialBase> materialBaseList = iMaterialBaseService.list(new QueryWrapper<MaterialBase>().eq("base_type_code",baseTypeCode).eq("del_flag",0));
                if(materialBaseList != null && materialBaseList.size()>0){
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
