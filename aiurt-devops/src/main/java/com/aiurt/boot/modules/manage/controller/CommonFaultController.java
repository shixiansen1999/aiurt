package com.aiurt.boot.modules.manage.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.util.oConvertUtils;
import com.swsc.copsms.modules.device.entity.DeviceType;
import com.swsc.copsms.modules.device.service.IDeviceTypeService;
import com.swsc.copsms.modules.manage.entity.CommonFault;
import com.swsc.copsms.modules.manage.entity.Station;
import com.swsc.copsms.modules.manage.entity.Subsystem;
import com.swsc.copsms.modules.manage.model.DeviceModel;
import com.swsc.copsms.modules.manage.model.StationModel;
import com.swsc.copsms.modules.manage.service.ICommonFaultService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.modules.manage.service.ISubsystemService;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: cs_common_fault
 * @Author: qian
 * @Date: 2021-09-16
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "常见故障管理")
@RestController
@RequestMapping("/manage/commonFault")
public class CommonFaultController {
    @Autowired
    private ICommonFaultService commonFaultService;
    @Autowired
    private ISubsystemService subsystemService;
    @Autowired
    private IDeviceTypeService deviceTypeService;

    /**
     * 分页列表查询
     *
     * @param commonFault
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "cs_common_fault-分页列表查询")
    @ApiOperation(value = "cs_common_fault-分页列表查询", notes = "cs_common_fault-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<CommonFault>> queryPageList(CommonFault commonFault,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                    HttpServletRequest req) {
        Result<IPage<CommonFault>> result = new Result<IPage<CommonFault>>();
        QueryWrapper<CommonFault> queryWrapper = QueryGenerator.initQueryWrapper(commonFault, req.getParameterMap());
        Page<CommonFault> page = new Page<CommonFault>(pageNo, pageSize);
        if (commonFault.getSubId() != null && commonFault.getEquipId() == null) {
            Subsystem subsystem = subsystemService.getById(commonFault.getSubId());
            QueryWrapper<DeviceType> temp = new QueryWrapper<DeviceType>();
            temp.eq("system_code", subsystem.getSystemCode());
            temp.eq("status", 1);
            temp.eq("del_flag", 0);
            temp.select("id");
            List<Object> ids = deviceTypeService.listObjs(temp);
            if (ids != null && ids.size() > 0) {
                queryWrapper.in("equip_id", ids);
            }
        }
        IPage<CommonFault> pageList = commonFaultService.page(page, queryWrapper);
        pageList.getRecords().forEach(temp -> {
            Subsystem subsystem = subsystemService.getById(temp.getSubId());
            DeviceType deviceType = deviceTypeService.getById(temp.getEquipId());
            if (subsystem != null) {
                temp.setSystemName(subsystem.getSystemName());
            }
            if (deviceType != null) {
                temp.setEquipName(deviceType.getName());
            }
        });
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param commonFault
     * @return
     */
    @AutoLog(value = "cs_common_fault-添加")
    @ApiOperation(value = "cs_common_fault-添加", notes = "cs_common_fault-添加")
    @PostMapping(value = "/add")
    public Result<CommonFault> add(@RequestBody CommonFault commonFault) {
        Result<CommonFault> result = new Result<CommonFault>();
        try {
            commonFaultService.save(commonFault);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param commonFault
     * @return
     */
    @AutoLog(value = "cs_common_fault-编辑")
    @ApiOperation(value = "cs_common_fault-编辑", notes = "cs_common_fault-编辑")
    @PutMapping(value = "/edit")
    public Result<CommonFault> edit(@RequestBody CommonFault commonFault) {
        Result<CommonFault> result = new Result<CommonFault>();
        CommonFault commonFaultEntity = commonFaultService.getById(commonFault.getId());
        if (commonFaultEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = commonFaultService.updateById(commonFault);
            //TODO 返回false说明什么？
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "cs_common_fault-通过id删除")
    @ApiOperation(value = "cs_common_fault-通过id删除", notes = "cs_common_fault-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            commonFaultService.removeById(id);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "cs_common_fault-批量删除")
    @ApiOperation(value = "cs_common_fault-批量删除", notes = "cs_common_fault-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<CommonFault> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<CommonFault> result = new Result<CommonFault>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.commonFaultService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "cs_common_fault-通过id查询")
    @ApiOperation(value = "cs_common_fault-通过id查询", notes = "cs_common_fault-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<CommonFault> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<CommonFault> result = new Result<CommonFault>();
        CommonFault commonFault = commonFaultService.getById(id);
        if (commonFault == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(commonFault);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
        // Step.1 组装查询条件
        QueryWrapper<CommonFault> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                CommonFault commonFault = JSON.parseObject(deString, CommonFault.class);
                queryWrapper = QueryGenerator.initQueryWrapper(commonFault, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<CommonFault> pageList = commonFaultService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "cs_common_fault列表");
        mv.addObject(NormalExcelConstants.CLASS, CommonFault.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("cs_common_fault列表数据", "导出人:Jeecg", "导出信息"));
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
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<CommonFault> listCommonFaults = ExcelImportUtil.importExcel(file.getInputStream(), CommonFault.class, params);
                commonFaultService.saveBatch(listCommonFaults);
                return Result.ok("文件导入成功！数据行数:" + listCommonFaults.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.ok("文件导入失败！");
    }

    @GetMapping("queryTreeList")
    public Result<List<DeviceModel>> queryTreeList() {
        Result<List<DeviceModel>> result = new Result<List<DeviceModel>>();
        List<Subsystem> subsystemList = subsystemService.list(new LambdaQueryWrapper<Subsystem>().eq(Subsystem::getDelFlag, 0));
        List<DeviceModel> list = new ArrayList<>();
        if (subsystemList != null && subsystemList.size() > 0) {
            subsystemList.forEach(subsystem -> {
                List<DeviceType> deviceTypeList = deviceTypeService.list(new LambdaQueryWrapper<DeviceType>().eq(DeviceType::getDelFlag, 0).eq(DeviceType::getSystemCode, subsystem.getSystemCode()).eq(DeviceType::getStatus, 1));
                subsystem.setDeviceTypeList(deviceTypeList);
                list.add(new DeviceModel(subsystem));
            });
            result.setResult(list);
        }
        return result;
    }

}
