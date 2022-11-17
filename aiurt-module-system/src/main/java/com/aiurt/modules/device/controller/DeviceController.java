package com.aiurt.modules.device.controller;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.common.system.base.view.AiurtEntityExcelView;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.device.Model.DeviceModel;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.entity.DeviceAssembly;
import com.aiurt.modules.device.entity.DeviceCompose;
import com.aiurt.modules.device.service.IDeviceAssemblyService;
import com.aiurt.modules.device.service.IDeviceComposeService;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.device.service.IDeviceTypeService;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 设备
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "设备管理-设备主数据/设备台账（system）")
@RestController
@RequestMapping("/device/device")
public class DeviceController extends BaseController<Device, IDeviceService> {
    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private IDeviceComposeService iDeviceCompostService;
    @Autowired
    private IDeviceAssemblyService iDeviceAssemblyService;
    @Autowired
    private SysBaseApiImpl sysBaseApi;
    @Autowired
    private IDeviceTypeService deviceTypeService;
    @Autowired
    private ICsSubsystemService csSubsystemService;
    @Autowired
    private ICsMajorService csMajorService;
    @Value("${jeecg.path.upload}")
    private String upLoadPath;
    /**
     * 分页列表查询
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "设备管理-设备台账-分页列表查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/equipmentData/masterData")
    @ApiOperation(value = "设备管理-设备主数据-分页列表查询", notes = "设备管理-设备主数据-分页列表查询")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "equipmentData/masterData")
    public Result<IPage<Device>> queryPageList(
                                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                               @RequestParam(name = "codeCc", required = false) String positionCodeCc,
                                               @RequestParam(name = "temporary", required = false) String temporary,
                                               @RequestParam(name = "majorCode", required = false) String majorCode,
                                               @RequestParam(name = "systemCode", required = false) String systemCode,
                                               @RequestParam(name = "deviceTypeCode", required = false) String deviceTypeCode,
                                               @RequestParam(name = "code", required = false) String code,
                                               @RequestParam(name = "name", required = false) String name,
                                               @RequestParam(name = "status", required = false) String status,
                                               @RequestParam(name = "scode", required = false) String scode,
                                               @RequestParam(name = "deviceCodes",required = false) String deviceCodes,
                                               HttpServletRequest req) {
        return getList(pageNo, pageSize, positionCodeCc, temporary, majorCode, systemCode, deviceTypeCode, code, name, status, scode,deviceCodes);
    }

    public Result<IPage<Device>> getList(Integer pageNo, Integer pageSize, String positionCodeCc, String temporary, String majorCode,
                             String systemCode, String deviceTypeCode, String code, String name, String status, String scode,String deviceCodes){
        Result<IPage<Device>> result = new Result<IPage<Device>>();
        Page<Device> page = new Page<Device>(pageNo, pageSize);
        QueryWrapper<Device> queryWrapper = deviceService.getQueryWrapper(scode,positionCodeCc, temporary, majorCode, systemCode, deviceTypeCode, code, name, status);
        if (StrUtil.isNotEmpty(deviceCodes)) {
            queryWrapper.lambda().in(Device::getCode, StrUtil.split(deviceCodes,','));
        }
        IPage<Device> pageList = deviceService.page(page, queryWrapper);
        List<Device> records = pageList.getRecords();
        if(records != null && records.size()>0){
            for(Device d : records){
                //线路
                String lineCode = d.getLineCode()==null?"":d.getLineCode();
                //站点
                String stationCode = d.getStationCode()==null?"":d.getStationCode();
                //位置
                String positionCode = d.getPositionCode()==null?"":d.getPositionCode();
                String lineCodeName = sysBaseApi.translateDictFromTable("cs_line", "line_name", "line_code", lineCode);
                String stationCodeName = sysBaseApi.translateDictFromTable("cs_station", "station_name", "station_code", stationCode);
                String positionCodeName = sysBaseApi.translateDictFromTable("cs_station_position", "position_name", "position_code", positionCode);
                String positionCodeCcName = lineCodeName ;
                if(stationCodeName != null && !"".equals(stationCodeName)){
                    positionCodeCcName +=  CommonConstant.SYSTEM_SPLIT_STR + stationCodeName  ;
                }
                if(!"".equals(positionCodeName) && positionCodeName != null){
                    positionCodeCcName += CommonConstant.SYSTEM_SPLIT_STR + positionCodeName;
                }
                d.setPositionCodeCcName(positionCodeCcName);
            }
        }
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 分页列表查询
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "设备管理-设备台账-分页列表查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/equipmentData/masterData")
    @ApiOperation(value = "设备管理-设备主数据-分页列表查询", notes = "设备管理-设备主数据-分页列表查询")
    @GetMapping(value = "/listTz")
    @PermissionData(pageComponent = "/equipmentData/standingBook")
    public Result<IPage<Device>> queryPageTzList(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "codeCc", required = false) String positionCodeCc,
            @RequestParam(name = "temporary", required = false) String temporary,
            @RequestParam(name = "majorCode", required = false) String majorCode,
            @RequestParam(name = "systemCode", required = false) String systemCode,
            @RequestParam(name = "deviceTypeCode", required = false) String deviceTypeCode,
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "scode", required = false) String scode,
            HttpServletRequest req) {
        return getList(pageNo, pageSize, positionCodeCc, temporary, majorCode, systemCode, deviceTypeCode, code, name, status, scode,null);
    }

    @AutoLog(value = "设备管理-设备主数据-列表查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/equipmentData/masterData")
    @ApiOperation(value = "设备管理-设备主数据-列表查询", notes = "设备管理-设备主数据-列表查询")
    @GetMapping(value = "/selectList")
    public Result<List<Device>> selectList(
            @RequestParam(name = "codeCc", required = false) String positionCodeCc,
            @RequestParam(name = "temporary", required = false) String temporary,
            @RequestParam(name = "majorCode", required = false) String majorCode,
            @RequestParam(name = "systemCode", required = false) String systemCode,
            @RequestParam(name = "deviceTypeCode", required = false) String deviceTypeCode,
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "ids", required = false) String ids,
                                               HttpServletRequest req) {
        QueryWrapper<Device> queryWrapper = deviceService.getQueryWrapper(null,positionCodeCc, temporary, majorCode, systemCode, deviceTypeCode, code, name, status);
        if(ids != null && !"".equals(ids)){
            queryWrapper.in("id",Arrays.asList(ids.split(",")));
        }
        List<Device> devices = deviceService.list(queryWrapper);
        List<Device> deviceList = new ArrayList<>();
        if(devices != null && devices.size()>0){
            for(Device device : devices){
                Device dres = deviceService.translate(device);
                deviceList.add(dres);
            }
        }
        return Result.OK(deviceList);
    }

    @AutoLog(value = "设备管理-设备主数据-详情查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/equipmentData/masterData")
    @ApiOperation(value = "设备详情查询", notes = "设备详情查询")
    @GetMapping(value = "/queryById")
    public Result<Device> queryById(@RequestParam(name = "id", required = true) String deviceId) {
        return deviceService.queryDetailById(deviceId);
    }

    /**
     * 添加时获取设备编号
     * @return
     */
    @AutoLog(value = "设备管理-设备主数据-添加时获取设备编号", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/equipmentData/masterData")
    @ApiOperation(value = "设备管理-设备主数据-添加时获取设备编号", notes = "设备管理-设备主数据-添加时获取设备编号")
    @GetMapping(value = "/getDeviceCode")
    public Result<String> getDeviceCode(
            @RequestParam(name = "majorCode", required = false) String majorCode,
            @RequestParam(name = "systemCode", required = false) String systemCode,
            @RequestParam(name = "deviceTypeCode", required = false) String deviceTypeCode) {
        Result<String> result = new Result<String>();
        try {
            if(systemCode == null){
                systemCode = "";
            }
            String codeCc = deviceService.getCodeByCc(deviceTypeCode);
            String str = majorCode + systemCode + codeCc;
            Device device = deviceService.getOne(new LambdaQueryWrapper<Device>().likeRight(Device::getCode, str)
                    .eq(Device::getDelFlag, 0).orderByDesc(Device::getCreateTime).last("limit 1"));
            String format = "";
            if(device != null){
                String code = device.getCode();
                String numstr = code.substring(code.length()-5);
                format = String.format("%05d", Long.parseLong(numstr) + 1);
            }else{
                format = "00001";
            }
            result.success(str + format);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 添加
     * @param device
     * @return
     */
    @AutoLog(value = "设备管理-设备主数据-添加", operateType = 2, operateTypeAlias = "添加", permissionUrl = "/equipmentData/masterData")
    @ApiOperation(value = "设备管理-设备主数据-添加", notes = "设备管理-设备主数据-添加")
    @PostMapping(value = "/add")
    public Result<Device> add(@RequestBody Device device) {
        Result<Device> result = new Result<Device>();
        try {
            String deviceTypeCodeCc = device.getDeviceTypeCodeCc()==null?"":device.getDeviceTypeCodeCc();
            String deviceTypeCode = deviceService.getCodeByCc(deviceTypeCodeCc);
            device.setDeviceTypeCode(deviceTypeCode);
            String positionCodeCc = device.getPositionCodeCc()==null?"":device.getPositionCodeCc();
            if(!"".equals(positionCodeCc)){
                if(positionCodeCc.contains(CommonConstant.SYSTEM_SPLIT_STR)){
                    String[] split = positionCodeCc.split(CommonConstant.SYSTEM_SPLIT_STR);
                    int length = split.length;
                    switch (length){
                        case 2:
                            device.setLineCode(split[0]);
                            device.setStationCode(split[1]);
                            break;
                        case 3:
                            device.setLineCode(split[0]);
                            device.setStationCode(split[1]);
                            device.setPositionCode(split[2]);
                            break;
                        default:
                            device.setLineCode(positionCodeCc);
                            device.setStationCode("");
                            device.setPositionCode("");
                    }
                }else{
                    device.setLineCode(positionCodeCc);
                }
            }
            deviceService.save(device);
            List<DeviceCompose> deviceComposeList = iDeviceCompostService.list(new QueryWrapper<DeviceCompose>().eq("device_type_code",deviceTypeCode));
            if(deviceComposeList != null && deviceComposeList.size()>0){
                for(DeviceCompose deviceCompose : deviceComposeList){
                    DeviceAssembly deviceAssemblyOld = iDeviceAssemblyService.getOne(new LambdaQueryWrapper<DeviceAssembly>().likeRight(DeviceAssembly::getCode, deviceCompose.getMaterialCode())
                            .eq(DeviceAssembly::getDelFlag, 0).orderByDesc(DeviceAssembly::getCreateTime).last("limit 1"));
                    String code = deviceCompose.getMaterialCode();
                    String format = "";
                    if(deviceAssemblyOld != null){
                        String codeold = deviceAssemblyOld.getCode();
                        String numstr = codeold.substring(codeold.length()-3);
                        format = String.format("%03d", Long.parseLong(numstr) + 1);
                    }else{
                        format = "001";
                    }
                    DeviceAssembly deviceAssembly = new DeviceAssembly();
                    deviceAssembly.setDeviceCode(device.getCode());
                    deviceAssembly.setMaterialCode(deviceCompose.getMaterialCode());
                    deviceAssembly.setCode(code + format);
                    deviceAssembly.setMaterialName(deviceCompose.getMaterialName());
                    deviceAssembly.setBaseTypeCode(deviceCompose.getBaseTypeCode());
                    deviceAssembly.setSpecifications(deviceCompose.getSpecifications());
                    deviceAssembly.setUnit(deviceCompose.getUnit()==null?"":deviceCompose.getUnit());
                    deviceAssembly.setManufactorCode(deviceCompose.getManufacturer()==null?"":deviceCompose.getManufacturer());
                    deviceAssembly.setPrice(deviceCompose.getPrice()==null?null:deviceCompose.getPrice().toString());
                    deviceAssembly.setDeviceTypeCode(deviceCompose.getDeviceTypeCode());
                    iDeviceAssemblyService.save(deviceAssembly);
                }
            }
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
     * @param device
     * @return
     */
    @AutoLog(value = "设备管理-设备主数据-编辑", operateType = 3, operateTypeAlias = "修改", permissionUrl = "/equipmentData/masterData")
    @ApiOperation(value = "设备管理-设备主数据-编辑", notes = "设备管理-设备主数据-编辑")
    @PutMapping(value = "/edit")
    public Result<Device> edit(@RequestBody Device device) {
        Result<Device> result = new Result<Device>();
        Device deviceEntity = deviceService.getById(device.getId());
        if (deviceEntity == null) {
            result.onnull("未找到对应实体");
        } else {
            final int count = (int) deviceService.count(new LambdaQueryWrapper<Device>().ne(Device::getId,device.getId()).eq(Device::getCode, device.getCode()).eq(Device::getDelFlag, 0).last("limit 1"));
            if (count > 0){
                return Result.error("设备编号不能重复");
            }
            String positionCodeCc = device.getPositionCodeCc()==null?"":device.getPositionCodeCc();
            if(!"".equals(positionCodeCc)){
                if(positionCodeCc.contains(CommonConstant.SYSTEM_SPLIT_STR)){
                    String[] split = positionCodeCc.split(CommonConstant.SYSTEM_SPLIT_STR);
                    int length = split.length;
                    switch (length){
                        case 2:
                            device.setLineCode(split[0]);
                            device.setStationCode(split[1]);
                            break;
                        case 3:
                            device.setLineCode(split[0]);
                            device.setStationCode(split[1]);
                            device.setPositionCode(split[2]);
                            break;
                        default:
                            device.setLineCode(positionCodeCc);
                            device.setStationCode("");
                            device.setPositionCode("");
                    }
                }else{
                    return Result.error("设备位置必须选择线路和站点！");
                }
            }
            boolean ok = deviceService.updateById(device);

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
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "设备管理-设备主数据-通过id删除", operateType = 4, operateTypeAlias = "删除", permissionUrl = "/equipmentData/masterData")
    @ApiOperation(value = "设备管理-设备主数据-通过id删除", notes = "设备管理-设备主数据-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            QueryWrapper<Device> deviceQueryWrapper = new QueryWrapper<>();
            deviceQueryWrapper.eq("id", id);
            Device device = deviceService.getOne(deviceQueryWrapper);
            deviceService.removeById(device);
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
    @AutoLog(value = "设备管理-设备主数据-批量删除", operateType = 4, operateTypeAlias = "删除", permissionUrl = "/equipmentData/masterData")
    @ApiOperation(value = "设备管理-设备主数据-批量删除", notes = "设备管理-设备主数据-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<String> result = new Result<String>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            List<Device> list = this.deviceService.lambdaQuery().eq(Device::getId, Arrays.asList(ids.split(","))).select(Device::getCode).list();
            deviceService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }


    /**
     * 导出excel
     *
     */
    @AutoLog(value = "设备主数据模板下载", operateType =  6, operateTypeAlias = "导出excel", permissionUrl = "")
    @ApiOperation(value="设备主数据模板下载", notes="设备主数据模板下载")
    @RequestMapping(value = "/exportTemplateXls")
    public ModelAndView exportTemplateXl() {
        // Step.1 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new AiurtEntityExcelView());
        //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.CLASS, DeviceModel.class);
        // 自定义导出列
        mv.addObject(NormalExcelConstants.EXPORT_FIELDS, "");
        ExportParams exportParams = new ExportParams();
        exportParams.setTitle("设备主数据模板");
        exportParams.setImageBasePath(upLoadPath);
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @ApiOperation(value = "通过excel导入数据", notes = "通过excel导入数据")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {

       return deviceService.importExcel(request,response);
    }

    /**
     * 专业导出
     *
     * @param device
     * @param request
     * @return
     */
    @AutoLog(value = "设备主数据导出")
    @ApiOperation(value = "设备主数据导出", notes = "设备主数据导出")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(Device device, HttpServletRequest request) {
        // Step.1 组装查询条件
        QueryWrapper<Device> queryWrapper = QueryGenerator.initQueryWrapper(device, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // Step.2 获取导出数据
        List<Device> pageList = service.list(queryWrapper);
        List<Device> exportList = null;

        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        } else {
            exportList = pageList;
        }
        String title = "设备主数据";
        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.FILE_NAME, title);
        mv.addObject(NormalExcelConstants.CLASS, Device.class);
        //update-begin--Author:liusq  Date:20210126 for：图片导出报错，ImageBasePath未设置--------------------
        ExportParams  exportParams=new ExportParams(title + "报表", "导出人:" + sysUser.getRealname(),ExcelType.XSSF);
        exportParams.setImageBasePath(upLoadPath);
        //update-end--Author:liusq  Date:20210126 for：图片导出报错，ImageBasePath未设置----------------------
        mv.addObject(NormalExcelConstants.PARAMS,exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }
}
