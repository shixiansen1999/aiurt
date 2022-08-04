package com.aiurt.modules.stock.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.stock.entity.StockLevel2Info;
import com.aiurt.modules.stock.entity.StockSubmitMaterials;
import com.aiurt.modules.stock.entity.StockSubmitPlan;
import com.aiurt.modules.stock.service.IStockSubmitMaterialsService;
import com.aiurt.modules.stock.service.IStockSubmitPlanService;
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
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 物资提报计划
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "二级库管理-提报计划")
@RestController
@RequestMapping("/stock/stockSubmitPlan")
public class StockSubmitPlanController {

    @Autowired
    private IStockSubmitPlanService iStockSubmitPlanService;
    @Autowired
    private ISysDepartService iSysDepartService;

    /**
     * 分页列表查询
     *
     * @param stockSubmitPlan
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "二级库管理-提报计划-分页列表查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/secondLevelWarehouse/EscalationPlanList")
    @ApiOperation(value = "二级库管理-提报计划-分页列表查询", notes = "二级库管理-提报计划-分页列表查询")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "secondLevelWarehouse/EscalationPlanList")
    public Result<IPage<StockSubmitPlan>> queryPageList(StockSubmitPlan stockSubmitPlan,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         HttpServletRequest req) {
        Result<IPage<StockSubmitPlan>> result = new Result<IPage<StockSubmitPlan>>();
        QueryWrapper<StockSubmitPlan> queryWrapper = QueryGenerator.initQueryWrapper(stockSubmitPlan, req.getParameterMap());
        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
        queryWrapper.orderByDesc("create_time");
        Page<StockSubmitPlan> page = new Page<StockSubmitPlan>(pageNo, pageSize);
        IPage<StockSubmitPlan> pageList = iStockSubmitPlanService.page(page, queryWrapper);
        List<StockSubmitPlan> records = pageList.getRecords();
        if(records!=null && records.size()>0){
            for(StockSubmitPlan s : records){
                String orgId = s.getOrgId();
                SysDepart sysDepart = iSysDepartService.getById(orgId);
                s.setOrgCode(sysDepart.getOrgCode());
            }
        }
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @AutoLog(value = "二级库管理-提报计划-获取已有数据的部门下拉", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/secondLevelWarehouse/EscalationPlanList")
    @ApiOperation(value = "二级库管理-提报计划-获取已有数据的部门下拉", notes = "二级库管理-提报计划-获取已有数据的部门下拉")
    @GetMapping(value = "/getOrgSelect")
    public Result<?> getOrgSelect(HttpServletRequest req) {
        List<Map<String, Object>> listres = iStockSubmitPlanService.getOrgSelect();
        return Result.OK(listres);
    }

    @AutoLog(value = "二级库管理-提报计划-添加", operateType = 2, operateTypeAlias = "添加", permissionUrl = "/secondLevelWarehouse/EscalationPlanList")
    @ApiOperation(value = "二级库管理-提报计划-添加", notes = "二级库管理-提报计划-添加")
    @PostMapping(value = "/add")
    public Result<StockSubmitPlan> add(@RequestBody StockSubmitPlan stockSubmitPlan) {
        Result<StockSubmitPlan> result = new Result<StockSubmitPlan>();
        try {
            iStockSubmitPlanService.add(stockSubmitPlan);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 修改提报计划状态
     * @param
     * @return
     */
    @AutoLog(value = "二级库管理-提报计划-修改提报计划状态", operateType = 3, operateTypeAlias = "修改", permissionUrl = "/secondLevelWarehouse/EscalationPlanList")
    @ApiOperation(value = "修改提报计划状态", notes = "修改提报计划状态")
    @GetMapping(value = "/submitPlanStatus")
    public Result<String> submitPlan(@RequestParam(name = "status", required = true) String status,
                                     @RequestParam(name = "code", required = true) String code) throws ParseException {
        StockSubmitPlan stockSubmitPlan = iStockSubmitPlanService.getOne(new QueryWrapper<StockSubmitPlan>().eq("code",code));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        stockSubmitPlan.setStatus(status);
        stockSubmitPlan.setSubmitTime(sdf.parse(sdf.format(new Date())));
        boolean ok = iStockSubmitPlanService.updateById(stockSubmitPlan);
        if (ok) {
            return Result.ok("操作成功！");
        }else{
            return Result.ok("操作失败！");
        }
    }

    /**
     * 新增获取提报计划编号
     * @param
     * @return
     */
    @AutoLog(value = "二级库管理-提报计划-获取提报计划编号", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/secondLevelWarehouse/EscalationPlanList")
    @ApiOperation(value = "新增获取提报计划编号", notes = "新增获取提报计划编号")
    @GetMapping(value = "/getSubmitPlanCode")
    public Result<StockSubmitPlan> getSubmitPlanCode() throws ParseException {
        return Result.ok(iStockSubmitPlanService.getSubmitPlanCode());
    }

    /**
     * 物资提报计划详情查询
     * @param id
     * @return
     */
    @AutoLog(value = "二级库管理-提报计划-详情查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/secondLevelWarehouse/EscalationPlanList")
    @ApiOperation(value = "物资提报计划详情查询", notes = "物资提报计划详情查询")
    @GetMapping(value = "/queryById")
    public Result<StockSubmitPlan> queryById(@RequestParam(name = "id", required = true) String id) {
        StockSubmitPlan stockSubmitPlan = iStockSubmitPlanService.getById(id);
        return Result.ok(stockSubmitPlan);
    }

    @AutoLog(value = "二级库管理-提报计划-编辑", operateType = 3, operateTypeAlias = "修改", permissionUrl = "/secondLevelWarehouse/EscalationPlanList")
    @ApiOperation(value = "二级库管理-提报计划-编辑", notes = "二级库管理-提报计划-编辑")
    @PostMapping(value = "/edit")
    public Result<StockSubmitPlan> edit(@RequestBody StockSubmitPlan stockSubmitPlan) {
        Result<StockSubmitPlan> result = new Result<StockSubmitPlan>();
        StockSubmitPlan stockSubmitPlanEntity = iStockSubmitPlanService.getById(stockSubmitPlan.getId());
        if (stockSubmitPlanEntity == null) {
            result.onnull("未找到对应实体");
        } else {
            boolean ok = iStockSubmitPlanService.edit(stockSubmitPlan);
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

    @AutoLog(value = "二级库管理-提报计划-通过id删除", operateType = 4, operateTypeAlias = "删除", permissionUrl = "/secondLevelWarehouse/EscalationPlanList")
    @ApiOperation(value = "二级库管理-提报计划-通过id删除", notes = "二级库管理-提报计划-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            StockSubmitPlan stockSubmitPlan = iStockSubmitPlanService.getById(id);
            iStockSubmitPlanService.removeById(stockSubmitPlan);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    @AutoLog(value = "二级库管理-提报计划-批量删除", operateType = 4, operateTypeAlias = "删除", permissionUrl = "/secondLevelWarehouse/EscalationPlanList")
    @ApiOperation(value = "物资提报计划分类-批量删除", notes = "物资提报计划分类-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<String> result = new Result<String>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            iStockSubmitPlanService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    @AutoLog(value = "二级库管理-提报计划-导出", operateType = 6, operateTypeAlias = "导出", permissionUrl = "/secondLevelWarehouse/EscalationPlanList")
    @ApiOperation(value = "导出", notes = "导出")
    @GetMapping(value = "/export")
    public void eqFaultAnaExport(@RequestParam(name = "ids", defaultValue = "") String ids,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        iStockSubmitPlanService.eqExport(ids, request, response);
    }

    /**
     * 通过excel导入数据
     * @param request
     * @param response
     * @return
     */
    @AutoLog(value = "二级库管理-提报计划-导入", operateType = 5, operateTypeAlias = "导入", permissionUrl = "/secondLevelWarehouse/EscalationPlanList")
    @ApiOperation(value = "二级库管理-提报计划-导入", notes = "二级库管理-提报计划-导入")
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
                return iStockSubmitPlanService.importExcel(file, params);
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

    @AutoLog(value = "二级库管理-提报计划-下载物资导入模板", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/secondLevelWarehouse/EscalationPlanList")
    @ApiOperation(value = "下载物资导入模板", notes = "下载物资导入模板")
    @RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
    public void downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
        ClassPathResource classPathResource =  new ClassPathResource("templates/tbjh.xlsx");
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
