package com.aiurt.modules.stock.controller;

import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.stock.entity.StockLevel2Check;
import com.aiurt.modules.stock.service.IStockLevel2CheckService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
 * @Description: 二级库盘点
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "二级库管理-二级库盘点管理")
@RestController
@RequestMapping("/stock/stockLevel2Check")
public class StockLevel2CheckController {

    @Autowired
    private IStockLevel2CheckService iStockLevel2CheckService;
    @Autowired
    private SysBaseApiImpl sysBaseApi;

    /**
     * 分页列表查询
     *
     * @param stockLevel2Check
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "二级库管理-二级库盘点管理-分页列表查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/secondLevelWarehouse/StockLevel2CheckList")
    @ApiOperation(value = "二级库管理-二级库盘点管理-分页列表查询", notes = "二级库管理-二级库盘点管理-分页列表查询")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "secondLevelWarehouse/StockLevel2CheckList")
    public Result<IPage<StockLevel2Check>> queryPageList(StockLevel2Check stockLevel2Check,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         HttpServletRequest req) {
        Result<IPage<StockLevel2Check>> result = new Result<IPage<StockLevel2Check>>();
        Page<StockLevel2Check> page = new Page<StockLevel2Check>(pageNo, pageSize);
        IPage<StockLevel2Check> pageList = iStockLevel2CheckService.pageList(page, stockLevel2Check);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @AutoLog(value = "二级库管理-二级库盘点管理-添加", operateType = 2, operateTypeAlias = "添加", permissionUrl = "/secondLevelWarehouse/StockLevel2CheckList")
    @ApiOperation(value = "二级库管理-二级库盘点管理-添加", notes = "二级库管理-二级库盘点管理-添加")
    @PostMapping(value = "/add")
    public Result<StockLevel2Check> add(@RequestBody StockLevel2Check stockLevel2Check) {
        Result<StockLevel2Check> result = new Result<StockLevel2Check>();
        try {
            iStockLevel2CheckService.add(stockLevel2Check);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 新增获取二级库盘点编号
     * @param
     * @return
     */
    @AutoLog(value = "二级库管理-二级库盘点管理-获取盘点单号", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/secondLevelWarehouse/StockLevel2CheckList")
    @ApiOperation(value = "二级库管理-二级库盘点管理-新增获取二级库盘点编号", notes = "二级库管理-二级库盘点管理-新增获取二级库盘点编号")
    @GetMapping(value = "/getStockCheckCode")
    public Result<StockLevel2Check> getStockCheckCode() throws ParseException {
        return Result.ok(iStockLevel2CheckService.getStockCheckCode());
    }



    /**
     * 单独更改状态
     * @param
     * @return
     */
    @AutoLog(value = "二级库管理-二级库盘点管理-执行中", operateType = 3, operateTypeAlias = "修改", permissionUrl = "/secondLevelWarehouse/StockLevel2CheckList")
    @ApiOperation(value = "二级库管理-二级库盘点管理-执行中", notes = "二级库管理-二级库盘点管理-执行中")
    @GetMapping(value = "/changeStatus")
    public Result<?> changeStatus(@RequestParam(name = "id", required = true) String id,
                                  @RequestParam(name = "status", required = true) String status) throws ParseException {
        StockLevel2Check stockLevel2Check = iStockLevel2CheckService.getById(id);
        stockLevel2Check.setStatus(status);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        stockLevel2Check.setCheckStartTime(sdf.parse(sdf.format(new Date())));
        iStockLevel2CheckService.updateById(stockLevel2Check);
        return Result.ok("操作成功！");
    }

    /**
     * 获取仓库所属机构人员
     * @param
     * @return
     */
    @AutoLog(value = "二级库管理-二级库盘点管理-获取仓库所属机构人员", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/secondLevelWarehouse/StockLevel2CheckList")
    @ApiOperation(value = "二级库管理-二级库盘点管理-获取仓库所属机构人员", notes = "二级库管理-二级库盘点管理-获取仓库所属机构人员")
    @GetMapping(value = "/getStockOrgUsers")
    public Result<?> getStockOrgUsers(@RequestParam(name = "warehouseCode", required = true) String warehouseCode) throws ParseException {
        return iStockLevel2CheckService.getStockOrgUsers(warehouseCode);
    }

    /**
     * 二级库盘点详情查询
     * @param id
     * @return
     */
    @AutoLog(value = "二级库管理-二级库盘点管理-详情查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/secondLevelWarehouse/StockLevel2CheckList")
    @ApiOperation(value = "二级库管理-二级库盘点管理-详情查询", notes = "二级库管理-二级库盘点管理-详情查询")
    @GetMapping(value = "/queryById")
    public Result<StockLevel2Check> queryById(@RequestParam(name = "id", required = true) String id) {
        StockLevel2Check stockLevel2Check = iStockLevel2CheckService.getById(id);
        return Result.ok(stockLevel2Check);
    }

    @AutoLog(value = "二级库管理-二级库盘点管理-下发", operateType = 3, operateTypeAlias = "修改", permissionUrl = "/secondLevelWarehouse/StockLevel2CheckList")
    @ApiOperation(value = "二级库管理-二级库盘点管理-下发", notes = "二级库管理-二级库盘点管理-下发")
    @GetMapping(value = "/sendStockCheck")
    public Result<StockLevel2Check> sendStockCheck(@RequestParam(name = "id", required = true) String id) {
        StockLevel2Check stockLevel2Check = iStockLevel2CheckService.getById(id);
        MessageDTO message = new MessageDTO();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        message.setFromUser(sysUser.getUsername());
        String checkerId = stockLevel2Check.getCheckerId();
//        String[] ids = new String[1];
//        ids[0] = checkerId;
//        List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(ids);
        message.setToUser(checkerId==null?"":checkerId);
        message.setTitle("二级库盘点下发通知");
        message.setContent("您有一个新的二级库盘点任务。");
        message.setCategory("2");
        sysBaseApi.sendSysAnnouncement(message);
        stockLevel2Check.setStatus(CommonConstant.STOCK_LEVEL2_CHECK_3);
        iStockLevel2CheckService.updateById(stockLevel2Check);
        return Result.ok("下发成功！");
    }

    @AutoLog(value = "二级库管理-二级库盘点管理-编辑", operateType = 3, operateTypeAlias = "修改", permissionUrl = "/secondLevelWarehouse/StockLevel2CheckList")
    @ApiOperation(value = "二级库管理-二级库盘点管理-编辑", notes = "二级库管理-二级库盘点管理-编辑")
    @PostMapping(value = "/edit")
    public Result<StockLevel2Check> edit(@RequestBody StockLevel2Check stockLevel2Check) {
        Result<StockLevel2Check> result = new Result<StockLevel2Check>();
        StockLevel2Check stockLevel2CheckEntity = iStockLevel2CheckService.getById(stockLevel2Check.getId());
        if (stockLevel2CheckEntity == null) {
            result.onnull("未找到对应实体");
        } else {
            boolean ok = iStockLevel2CheckService.edit(stockLevel2Check);
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

    @AutoLog(value = "二级库管理-二级库盘点管理-通过id删除", operateType = 4, operateTypeAlias = "删除", permissionUrl = "/secondLevelWarehouse/StockLevel2CheckList")
    @ApiOperation(value = "二级库管理-二级库盘点管理-通过id删除", notes = "二级库管理-二级库盘点管理-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            StockLevel2Check stockLevel2Check = iStockLevel2CheckService.getById(id);
            iStockLevel2CheckService.removeById(stockLevel2Check);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    @AutoLog(value = "二级库管理-二级库盘点管理-批量删除", operateType = 4, operateTypeAlias = "删除", permissionUrl = "/secondLevelWarehouse/StockLevel2CheckList")
    @ApiOperation(value = "二级库盘点分类-批量删除", notes = "二级库盘点分类-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<String> result = new Result<String>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            iStockLevel2CheckService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    @AutoLog(value = "二级库管理-二级库盘点管理-导出", operateType = 6, operateTypeAlias = "导出", permissionUrl = "/secondLevelWarehouse/StockLevel2CheckList")
    @ApiOperation(value = "导出", notes = "导出")
    @GetMapping(value = "/export")
    public void eqFaultAnaExport(@RequestParam(name = "ids", defaultValue = "") String ids,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        iStockLevel2CheckService.eqExport(ids, request, response);
    }

}
