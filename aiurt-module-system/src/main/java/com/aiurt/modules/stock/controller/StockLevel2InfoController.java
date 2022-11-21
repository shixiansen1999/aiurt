package com.aiurt.modules.stock.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.SymbolConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.common.util.ImportExcelUtil;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.entity.vo.CsMajorImportVO;
import com.aiurt.modules.stock.entity.StockInOrderLevel2;
import com.aiurt.modules.stock.entity.StockLevel2;
import com.aiurt.modules.stock.entity.StockLevel2Info;
import com.aiurt.modules.stock.entity.StockLevel2InfoVo;
import com.aiurt.modules.stock.service.IStockInOrderLevel2Service;
import com.aiurt.modules.stock.service.IStockLevel2InfoService;
import com.aiurt.modules.stock.service.IStockLevel2Service;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.service.ISysDepartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 二级库
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "系统管理-基础数据-二级库仓库")
@RestController
@RequestMapping("/stock/stockLevel2Info")
public class StockLevel2InfoController extends BaseController<StockLevel2Info,IStockLevel2InfoService> {

    @Autowired
    private IStockLevel2InfoService iStockLevel2InfoService;
    @Autowired
    private IStockInOrderLevel2Service iStockInOrderLevel2Service;
    @Autowired
    private IStockLevel2Service iStockLevel2Service;
    @Autowired
    private ISysDepartService iSysDepartService;

    /**
     * 分页列表查询
     *
     * @param stockLevel2Info
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "系统管理-基础数据-二级库仓库-分页列表查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/manage/StockLevelTwoList")
    @ApiOperation(value = "系统管理-基础数据-二级库仓库-分页列表查询", notes = "系统管理-基础数据-二级库仓库-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<StockLevel2Info>> queryPageList(StockLevel2Info stockLevel2Info,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         HttpServletRequest req) {
        Result<IPage<StockLevel2Info>> result = new Result<IPage<StockLevel2Info>>();
        QueryWrapper<StockLevel2Info> queryWrapper = QueryGenerator.initQueryWrapper(stockLevel2Info, req.getParameterMap());
        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
        queryWrapper.orderByDesc("create_time");
        Page<StockLevel2Info> page = new Page<StockLevel2Info>(pageNo, pageSize);
        IPage<StockLevel2Info> pageList = iStockLevel2InfoService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @AutoLog(value = "系统管理-基础数据-二级库仓库-下拉列表查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/manage/StockLevelTwoList")
    @ApiOperation(value = "系统管理-基础数据-二级库仓库-下拉列表查询", notes = "系统管理-基础数据-二级库仓库-下拉列表查询")
    @GetMapping(value = "/selectList")
    public Result<List<StockLevel2Info>> selectList(StockLevel2Info stockLevel2Info,
                                                        HttpServletRequest req) {
        Result<List<StockLevel2Info>> result = new Result<List<StockLevel2Info>>();
        QueryWrapper<StockLevel2Info> queryWrapper = new QueryWrapper<>();
        if(stockLevel2Info.getWarehouseName() != null && !"".equals(stockLevel2Info.getWarehouseName())){
            queryWrapper.like("warehouse_name",stockLevel2Info.getWarehouseName());
        }
        if(stockLevel2Info.getWarehouseCode() != null && !"".equals(stockLevel2Info.getWarehouseCode())){
            queryWrapper.like("warehouse_code",stockLevel2Info.getWarehouseCode());
        }
        if(stockLevel2Info.getOrganizationId() != null && !"".equals(stockLevel2Info.getOrganizationId())){
            queryWrapper.like("organization_id",stockLevel2Info.getOrganizationId());
        }
        if(stockLevel2Info.getStatus() != null && !"".equals(stockLevel2Info.getStatus())){
            queryWrapper.like("status",stockLevel2Info.getStatus());
        }
        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
        queryWrapper.eq("status", CommonConstant.STOCK_LEVEL2_STATUS_1);
        queryWrapper.orderByDesc("create_time");
        List<StockLevel2Info> stockLevel2Infos = iStockLevel2InfoService.list(queryWrapper);
        result.setSuccess(true);
        result.setResult(stockLevel2Infos);
        return result;
    }

    @AutoLog(value = "系统管理-基础数据-二级库仓库-下拉列表查询(权限控制)", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/manage/StockLevelTwoList")
    @ApiOperation(value = "系统管理-基础数据-二级库仓库-下拉列表查询(权限控制)", notes = "系统管理-基础数据-二级库仓库-下拉列表查询")
    @GetMapping(value = "/selectListAuth")
    @PermissionData(pageComponent = "manage/StockLevelTwoList")
    public Result<List<StockLevel2Info>> selectListAuth(StockLevel2Info stockLevel2Info,
                                                    HttpServletRequest req) {
        Result<List<StockLevel2Info>> result = new Result<List<StockLevel2Info>>();
        QueryWrapper<StockLevel2Info> queryWrapper = new QueryWrapper<>();
        if(stockLevel2Info.getWarehouseName() != null && !"".equals(stockLevel2Info.getWarehouseName())){
            queryWrapper.like("warehouse_name",stockLevel2Info.getWarehouseName());
        }
        if(stockLevel2Info.getWarehouseCode() != null && !"".equals(stockLevel2Info.getWarehouseCode())){
            queryWrapper.like("warehouse_code",stockLevel2Info.getWarehouseCode());
        }
        if(stockLevel2Info.getOrganizationId() != null && !"".equals(stockLevel2Info.getOrganizationId())){
            queryWrapper.like("organization_id",stockLevel2Info.getOrganizationId());
        }
        if(stockLevel2Info.getStatus() != null && !"".equals(stockLevel2Info.getStatus())){
            queryWrapper.like("status",stockLevel2Info.getStatus());
        }
        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
        queryWrapper.eq("status", CommonConstant.STOCK_LEVEL2_STATUS_1);
        queryWrapper.orderByDesc("create_time");
        List<StockLevel2Info> stockLevel2Infos = iStockLevel2InfoService.list(queryWrapper);
        result.setSuccess(true);
        result.setResult(stockLevel2Infos);
        return result;
    }

    @AutoLog(value = "系统管理-基础数据-二级库仓库-添加", operateType = 2, operateTypeAlias = "添加", permissionUrl = "/manage/StockLevelTwoList")
    @ApiOperation(value = "系统管理-基础数据-二级库仓库-添加", notes = "系统管理-基础数据-二级库仓库-添加")
    @PostMapping(value = "/add")
    public Result<StockLevel2Info> add(@RequestBody StockLevel2Info stockLevel2Info) {
        Result<StockLevel2Info> result = new Result<StockLevel2Info>();
        try {
            final int count = (int) iStockLevel2InfoService.count(new LambdaQueryWrapper<StockLevel2Info>().eq(StockLevel2Info::getWarehouseCode, stockLevel2Info.getWarehouseCode()).eq(StockLevel2Info::getDelFlag, 0).last("limit 1"));
            if (count > 0){
                return Result.error("二级库编号不能重复");
            }
            final int countname = (int) iStockLevel2InfoService.count(new LambdaQueryWrapper<StockLevel2Info>().eq(StockLevel2Info::getWarehouseName, stockLevel2Info.getWarehouseName()).eq(StockLevel2Info::getDelFlag, 0).last("limit 1"));
            if (countname > 0){
                return Result.error("二级库名称不能重复");
            }
            String organizationId = stockLevel2Info.getOrganizationId();
            SysDepart sysDepart = iSysDepartService.getById(organizationId);
            stockLevel2Info.setOrgCode(sysDepart.getOrgCode());
            iStockLevel2InfoService.save(stockLevel2Info);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 二级库详情查询
     * @param id
     * @return
     */
    @AutoLog(value = "系统管理-基础数据-二级库仓库-详情查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/manage/StockLevelTwoList")
    @ApiOperation(value = "二级库-详情查询", notes = "二级库-详情查询")
    @GetMapping(value = "/queryById")
    public Result<StockLevel2Info> queryById(@RequestParam(name = "id", required = true) String id) {
        StockLevel2Info stockLevel2Info = iStockLevel2InfoService.getById(id);
        return Result.ok(stockLevel2Info);
    }

    @AutoLog(value = "系统管理-基础数据-二级库仓库-编辑", operateType = 3, operateTypeAlias = "修改", permissionUrl = "/manage/StockLevelTwoList")
    @ApiOperation(value = "系统管理-基础数据-二级库仓库-编辑", notes = "系统管理-基础数据-二级库仓库-编辑")
    @PutMapping(value = "/edit")
    public Result<StockLevel2Info> edit(@RequestBody StockLevel2Info stockLevel2Info) {
        Result<StockLevel2Info> result = new Result<StockLevel2Info>();
        StockLevel2Info stockLevel2InfoEntity = iStockLevel2InfoService.getById(stockLevel2Info.getId());
        if (stockLevel2InfoEntity == null) {
            result.onnull("未找到对应实体");
        } else {
            String organizationId = stockLevel2Info.getOrganizationId();
            SysDepart sysDepart = iSysDepartService.getById(organizationId);
            stockLevel2Info.setOrgCode(sysDepart.getOrgCode());
            boolean ok = iStockLevel2InfoService.updateById(stockLevel2Info);
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

    @AutoLog(value = "系统管理-基础数据-二级库仓库-通过id删除", operateType = 4, operateTypeAlias = "删除", permissionUrl = "/manage/StockLevelTwoList")
    @ApiOperation(value = "系统管理-基础数据-二级库仓库-通过id删除", notes = "系统管理-基础数据-二级库仓库-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            StockLevel2Info stockLevel2Info = iStockLevel2InfoService.getById(id);
            String code = stockLevel2Info.getWarehouseCode();
            //是否有对应的二级库入库在使用该二级库
            List<StockInOrderLevel2> stockInOrderLevel2 = iStockInOrderLevel2Service.list(new QueryWrapper<StockInOrderLevel2>().eq("warehouse_code",code).eq("del_flag", CommonConstant.DEL_FLAG_0));
            if(stockInOrderLevel2 != null && stockInOrderLevel2.size()>0){
                return Result.error("该数据已被其他模块引用。");
            }
            //是否有对应的二级库库存在使用该二级库
            List<StockLevel2> stockLevel2s = iStockLevel2Service.list(new QueryWrapper<StockLevel2>().eq("warehouse_code",code).eq("del_flag", CommonConstant.DEL_FLAG_0));
            if(stockLevel2s != null && stockLevel2s.size()>0){
                return Result.error("该数据已被其他模块引用。");
            }
            iStockLevel2InfoService.removeById(stockLevel2Info);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    @AutoLog(value = "二级库分类-批量删除", operateType = 4, operateTypeAlias = "删除", permissionUrl = "/manage/StockLevelTwoList")
    @ApiOperation(value = "二级库分类-批量删除", notes = "二级库分类-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<String> result = new Result<String>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            String res = "编号为";
            List<String> strings = Arrays.asList(ids.split(","));
            for(String id : strings){
                StockLevel2Info stockLevel2Info = iStockLevel2InfoService.getById(id);
                String code = stockLevel2Info.getWarehouseCode();
                List<StockInOrderLevel2> stockInOrderLevel2 = iStockInOrderLevel2Service.list(new QueryWrapper<StockInOrderLevel2>().eq("warehouse_code",code).eq("del_flag", CommonConstant.DEL_FLAG_0));
                List<StockLevel2> stockLevel2s = iStockLevel2Service.list(new QueryWrapper<StockLevel2>().eq("warehouse_code",code).eq("del_flag", CommonConstant.DEL_FLAG_0));
                boolean a =(stockInOrderLevel2 != null && stockInOrderLevel2.size()>0) || (stockLevel2s != null && stockLevel2s.size()>0);
                if(a){
                    res += stockLevel2Info.getWarehouseCode() + ",";
                }else{
                    iStockLevel2InfoService.removeById(stockLevel2Info);
                }
            }
            if(res.contains(SymbolConstant.COMMA)){
                res = res.substring(0,res.length()-1);
                res += "的二级库已被其他模块引用无法删除，其余二级库删除成功!";
            }else{
                res = "删除成功!";
            }
            result.success(res);
        }
        return result;
    }

    /**
     * 二级仓库管理导出
     * @param request
     * @param stockLevel2Info
     * @return
     */
    @AutoLog(value = "二级仓库管理-二级仓库分页列表-导出excel", operateType =  6, operateTypeAlias = "导出excel", permissionUrl = "/manage/StockLevelTwoList")
    @ApiOperation(value="二级仓库管理-导出excel", notes="二级仓库管理-导出excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StockLevel2Info stockLevel2Info) {
        return super.exportXls(request, stockLevel2Info, StockLevel2Info.class, "二级仓库管理");
    }

    /**
     * 二级仓库管理导入
     * @param request
     * @param response
     * @return
     */
    @AutoLog(value = "二级仓库管理-二级仓库分页列表-通过excel导入数据", operateType =  6, operateTypeAlias = "通过excel导入数据", permissionUrl = "/manage/StockLevelTwoList")
    @ApiOperation(value="二级仓库管理-通过excel导入数据", notes="二级仓库管理-通过excel导入数据")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return iStockLevel2InfoService.importExcel(request,response);

    }

    /**
     * 二级仓库管理导入模板下载
     * @return
     */
    @AutoLog(value = "二级仓库管理导入模板下载", operateType =  6, operateTypeAlias = "导出excel", permissionUrl = "/manage/StockLevelTwoList")
    @ApiOperation(value="二级仓库管理导入模板下载", notes="二级仓库管理导入模板下载")
    @RequestMapping(value = "/exportTemplateXls")
    public ModelAndView exportTemplateXl() {
        String remark = "二级库导入模板\n" +
                "填写须知：\n" +
                "1.请勿增加、删除、或修改表格中的字段顺序、字段名称；\n" +
                "2.请严格按照数据规范填写，并填写完所有必填项，红底白字列为必填项；\n" +
                "字段说明：\n" +
                "1.二级库编号：必填字段；\n" +
                "2.二级库名称：必填字段；\n" +
                "3.组织机构：必填字段，且与系统下拉项保持一致；\n";
        return super.exportTemplateXls("",StockLevel2Info.class, "二级库导入模板",remark);
    }
}
