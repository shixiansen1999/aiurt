package com.aiurt.modules.device.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.entity.DeviceAssembly;
import com.aiurt.modules.device.entity.DeviceCompose;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.device.service.IDeviceAssemblyService;
import com.aiurt.modules.device.service.IDeviceComposeService;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.device.service.IDeviceTypeService;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @Description: 设备
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "设备(system)")
@RestController
@RequestMapping("/device/device")
public class DeviceController {
    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private IDeviceComposeService iDeviceCompostService;
    @Autowired
    private IDeviceAssemblyService iDeviceAssemblyService;
    @Autowired
    private IDeviceTypeService iDeviceTypeService;

    /**
     * 分页列表查询
     *
     * @param device
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "设备-分页列表查询")
    @ApiOperation(value = "设备-分页列表查询", notes = "设备-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<Device>> queryPageList(Device device,
                                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                               @RequestParam(name = "lineCode", required = false) String lineCode,
                                               @RequestParam(name = "stationCode", required = false) String stationCode,
                                               @RequestParam(name = "positionCode", required = false) String positionCode,
                                               @RequestParam(name = "temporary", required = false) String temporary,
                                               @RequestParam(name = "majorCode", required = false) String majorCode,
                                               @RequestParam(name = "systemCode", required = false) String systemCode,
                                               @RequestParam(name = "deviceTypeCode", required = false) String deviceTypeCode,
                                               @RequestParam(name = "code", required = false) String code,
                                               @RequestParam(name = "name", required = false) String name,
                                               @RequestParam(name = "status", required = false) String status,
                                               HttpServletRequest req) {
        Result<IPage<Device>> result = new Result<IPage<Device>>();
        QueryWrapper<Device> queryWrapper = new QueryWrapper<>();
        if(majorCode != null && !"".equals(majorCode)){
            queryWrapper.eq("major_code", majorCode);
        }
        if(temporary != null && !"".equals(temporary)){
            queryWrapper.eq("temporary", temporary);
        }
        if(systemCode != null && !"".equals(systemCode)){
            queryWrapper.eq("system_code", systemCode);
        }
        if(deviceTypeCode != null && !"".equals(deviceTypeCode)){
            queryWrapper.apply(" FIND_IN_SET ( "+deviceTypeCode+" , REPLACE(device_type_code_cc,'/',',')) ");
        }
        if(lineCode != null && !"".equals(lineCode)){
            queryWrapper.eq("line_code", lineCode);
        }
        if(stationCode != null && !"".equals(stationCode)){
            queryWrapper.eq("station_code", stationCode);
        }
        if(positionCode != null && !"".equals(positionCode)){
            queryWrapper.eq("position_code", positionCode);
        }
        if(code != null && !"".equals(code)){
            queryWrapper.eq("code", code);
        }
        if(name != null && !"".equals(name)){
            queryWrapper.like("name", name);
        }
        if(status != null && !"".equals(status)){
            queryWrapper.eq("status", status);
        }
        queryWrapper.eq("del_flag", 0);
        Page<Device> page = new Page<Device>(pageNo, pageSize);
        IPage<Device> pageList = deviceService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @AutoLog(value = "设备-分页列表查询")
    @ApiOperation(value = "设备-分页列表查询", notes = "设备-分页列表查询")
    @GetMapping(value = "/patrolDevicelist")
    public Result<?> patrolDevicelist(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               HttpServletRequest req) {
//        return Result.ok(deviceService.patrolDevicelist());
        return Result.ok(null);
    }

    @AutoLog(value = "设备-列表查询")
    @ApiOperation(value = "设备-列表查询", notes = "设备-列表查询")
    @GetMapping(value = "/selectList")
    public Result<List<Device>> selectList(
                                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                               @RequestParam(name = "lineCode", required = false) String lineCode,
                                               @RequestParam(name = "stationCode", required = false) String stationCode,
                                               @RequestParam(name = "positionCode", required = false) String positionCode,
                                               @RequestParam(name = "majorCode", required = false) String majorCode,
                                               @RequestParam(name = "systemCode", required = false) String systemCode,
                                               @RequestParam(name = "deviceTypeCode", required = false) String deviceTypeCode,
                                               @RequestParam(name = "code", required = false) String code,
                                               @RequestParam(name = "name", required = false) String name,
                                               @RequestParam(name = "status", required = false) String status,
                                               @RequestParam(name = "ids", required = false) String ids,
                                               HttpServletRequest req) {
        Result<List<Device>> result = new Result<List<Device>>();
        QueryWrapper<Device> queryWrapper = new QueryWrapper<>();
        if(majorCode != null && !"".equals(majorCode)){
            queryWrapper.eq("major_code", majorCode);
        }
        if(systemCode != null && !"".equals(systemCode)){
            queryWrapper.eq("system_code", systemCode);
        }
        if(deviceTypeCode != null && !"".equals(deviceTypeCode)){
            queryWrapper.apply(" FIND_IN_SET ( "+deviceTypeCode+" , REPLACE(device_type_code_cc,'/',',')) ");
        }
        if(lineCode != null && !"".equals(lineCode)){
            queryWrapper.eq("line_code", lineCode);
        }
        if(stationCode != null && !"".equals(stationCode)){
            queryWrapper.eq("station_code", stationCode);
        }
        if(positionCode != null && !"".equals(positionCode)){
            queryWrapper.eq("position_code", positionCode);
        }
        if(code != null && !"".equals(code)){
            queryWrapper.eq("code", code);
        }
        if(name != null && !"".equals(name)){
            queryWrapper.like("name", name);
        }
        if(status != null && !"".equals(status)){
            queryWrapper.eq("status", status);
        }
        if(ids != null && !"".equals(ids) && ids.length()>0){
            queryWrapper.in("id", Arrays.asList(ids.split(",")));
        }
        queryWrapper.eq("del_flag", 0);
        List<Device> devices = deviceService.list(queryWrapper);
        List<Device> deviceList = new ArrayList<>();
        if(devices != null && devices.size()>0){
            for(Device device : devices){
                Device dres = deviceService.translate(device);
                deviceList.add(dres);
            }
        }
        result.setSuccess(true);
        result.setResult(devices);
        return result;
    }

    @ApiOperation(value = "设备详情查询", notes = "设备详情查询")
    @PostMapping(value = "/queryById")
    public Result<Device> queryById(@RequestParam(name = "id", required = true) String deviceId) {
        return deviceService.queryDetailById(deviceId);
    }

    /**
     * 添加时获取设备编号
     * @return
     */
    @AutoLog(value = "设备-添加时获取设备编号")
    @ApiOperation(value = "设备-添加时获取设备编号", notes = "设备-添加时获取设备编号")
    @GetMapping(value = "/getDeviceCode")
    public Result<String> getDeviceCode(
            @RequestParam(name = "majorCode", required = false) String majorCode,
            @RequestParam(name = "systemCode", required = false) String systemCode,
            @RequestParam(name = "deviceTypeCode", required = false) String deviceTypeCode) {
        Result<String> result = new Result<String>();
        try {
//            DeviceType deviceType = iDeviceTypeService.getOne(new QueryWrapper<DeviceType>().eq("code",deviceTypeCode));
//            String deviceTypeCodeCc = iDeviceTypeService.getCcStr(deviceType);
//            if(deviceTypeCodeCc != null && !"".equals(deviceTypeCodeCc)){
//                if(deviceTypeCodeCc.contains("/")){
//                    List<String> strings = Arrays.asList(deviceTypeCodeCc.split("/"));
//                    for(String code : strings){
//                        deviceTypeCode += code;
//                    }
//                }else{
//                    deviceTypeCode = deviceTypeCodeCc;
//                }
//            }
            if(systemCode == null){
                systemCode = "";
            }
            String str = majorCode + systemCode + deviceTypeCode;
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
    @AutoLog(value = "设备-添加")
    @ApiOperation(value = "设备-添加", notes = "设备-添加")
    @PostMapping(value = "/add")
    public Result<Device> add(@RequestBody Device device) {
        Result<Device> result = new Result<Device>();
        try {
            String deviceTypeCode = device.getDeviceTypeCode();
            DeviceType deviceType = iDeviceTypeService.getOne(new QueryWrapper<DeviceType>().eq("code",deviceTypeCode));
            String typeCodeCc = iDeviceTypeService.getCcStr(deviceType);
            device.setDeviceTypeCodeCc(typeCodeCc);
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
                    deviceAssembly.setUnit(deviceCompose.getUnit());
                    deviceAssembly.setManufactorCode(deviceCompose.getManufacturer());
                    deviceAssembly.setPrice(deviceCompose.getPrice().toString());
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
    @AutoLog(value = "设备-编辑")
    @ApiOperation(value = "设备-编辑", notes = "设备-编辑")
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
    @AutoLog(value = "设备-通过id删除")
    @ApiOperation(value = "设备-通过id删除", notes = "设备-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            QueryWrapper<Device> deviceQueryWrapper = new QueryWrapper<>();
            deviceQueryWrapper.eq("id", id);
            Device device = deviceService.getOne(deviceQueryWrapper);
//            device.setDelFlag(1);
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
    @AutoLog(value = "设备-批量删除")
    @ApiOperation(value = "设备-批量删除", notes = "设备-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<String> result = new Result<String>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            List<Device> list = this.deviceService.lambdaQuery().eq(Device::getId, Arrays.asList(ids.split(","))).select(Device::getCode).list();
            list.stream().forEach( deviceAssembly -> deviceAssembly.setDelFlag(1));
            deviceService.removeBatchByIds(list);
            result.success("删除成功!");
        }
        return result;
    }
}
