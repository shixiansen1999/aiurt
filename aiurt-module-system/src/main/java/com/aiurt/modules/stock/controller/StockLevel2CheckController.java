package com.aiurt.modules.stock.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.CommonTodoStatus;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.constant.enums.TodoTaskTypeEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.stock.entity.StockLevel2Check;
import com.aiurt.modules.stock.service.IStockLevel2CheckService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.aiurt.modules.todo.dto.TodoDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

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
    @Autowired
    private ISysParamAPI iSysParamAPI;
    @Autowired
    private ISTodoBaseAPI isTodoBaseAPI;
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
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        stockLevel2Check.setStatus(CommonConstant.STOCK_LEVEL2_CHECK_3);
        iStockLevel2CheckService.updateById(stockLevel2Check);

        try {
            //发送通知
            MessageDTO messageDTO = new MessageDTO(sysUser.getUsername(),stockLevel2Check.getCheckerId(), "2级库盘点" + DateUtil.today(), null);

            //构建消息模板
            HashMap<String, Object> map = new HashMap<>();
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, stockLevel2Check.getId());
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE,  SysAnnmentTypeEnum.MATERIAL_WAREHOUSING.getType());
            map.put("stockCheckCode",stockLevel2Check.getStockCheckCode());
            String warehouseName= sysBaseApi.getWarehouseNameByCode(stockLevel2Check.getWarehouseCode());
            map.put("warehouseName",warehouseName);
            LoginUser userByName = sysBaseApi.getUserByName(stockLevel2Check.getCheckerId());
            map.put("checkName", userByName.getRealname());
            map.put("time", DateUtil.format(stockLevel2Check.getPlanStartTime(), "yyyy-MM-dd HH:mm:ss"));
            messageDTO.setData(map);


            messageDTO.setData(map);
            //业务类型，消息类型，消息模板编码，摘要，发布内容
            messageDTO.setTemplateCode(CommonConstant.STOCKLEVEL2CHECK_SERVICE_NOTICE);
            SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE);
            messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
            messageDTO.setMsgAbstract("2级库物资盘点");
            messageDTO.setPublishingContent("请在计划开始时间内盘点，并填写盘点记录结果");
            messageDTO.setCategory(CommonConstant.MSG_CATEGORY_10);
            sysBaseApi.sendTemplateMessage(messageDTO);
            //发送待办
            TodoDTO todoDTO = new TodoDTO();
            todoDTO.setData(map);
            SysParamModel sysParamModelTodo = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE_PROCESS);
            todoDTO.setType(ObjectUtil.isNotEmpty(sysParamModelTodo) ? sysParamModelTodo.getValue() : "");
            todoDTO.setTitle("2级库盘点" + DateUtil.today());
            todoDTO.setMsgAbstract("2级库物资盘点");
            todoDTO.setPublishingContent("请在计划开始时间内盘点，并填写盘点记录结果");
            todoDTO.setCurrentUserName(stockLevel2Check.getCheckerId());
            todoDTO.setBusinessKey(stockLevel2Check.getId());
            todoDTO.setBusinessType(TodoBusinessTypeEnum.MATERIAL_WAREHOUSING.getType());
            todoDTO.setTaskType(TodoTaskTypeEnum.SPARE_PART.getType());
            todoDTO.setTodoType(CommonTodoStatus.TODO_STATUS_0);
            todoDTO.setTemplateCode(CommonConstant.STOCKLEVEL2CHECK_SERVICE_NOTICE);

            isTodoBaseAPI.createTodoTask(todoDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void eqFaultAnaExport(StockLevel2Check stockLevel2Check,
                                 @RequestParam(name = "ids",required =  false) String ids,HttpServletRequest request,HttpServletResponse response) {
        iStockLevel2CheckService.eqExport(stockLevel2Check,ids, request, response);
    }

}
