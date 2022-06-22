package com.aiurt.modules.device.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.entity.DeviceAssembly;
import com.aiurt.modules.device.service.IDeviceAssemblyService;
import com.aiurt.modules.device.service.IDeviceService;
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

/**
 * @Description: 设备
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "设备(system)")
@RestController
@RequestMapping("/device/deviceAssembly")
public class DeviceAssemblyController {
    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private IDeviceAssemblyService iDeviceAssemblyService;

    /**
     * 分页列表查询
     *
     * @param deviceAssembly
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "设备组件-分页列表查询")
    @ApiOperation(value = "设备组件-分页列表查询", notes = "设备组件-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<DeviceAssembly>> queryPageList(DeviceAssembly deviceAssembly,
                                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                               @RequestParam(name = "deviceCode", required = false) Integer deviceCode,
                                               HttpServletRequest req) {
        Result<IPage<DeviceAssembly>> result = new Result<IPage<DeviceAssembly>>();
        Map<String, String[]> parameterMap = req.getParameterMap();
        QueryWrapper<DeviceAssembly> queryWrapper = QueryGenerator.initQueryWrapper(deviceAssembly, parameterMap);
        if (deviceCode != null && !"".equals(deviceCode)) {
            queryWrapper.eq("device_code",deviceCode);
        }
        queryWrapper.eq("del_flag", 0);
        Page<DeviceAssembly> page = new Page<DeviceAssembly>(pageNo, pageSize);
        IPage<DeviceAssembly> pageList = iDeviceAssemblyService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param device
     * @return
     */
    @AutoLog(value = "设备组件-添加")
    @ApiOperation(value = "设备组件-添加", notes = "设备组件-添加")
    @PostMapping(value = "/add")
    public Result<Device> add(@RequestBody Device device) {
        Result<Device> result = new Result<Device>();
        try {
            //code不能重复
            final int count = (int) deviceService.count(new LambdaQueryWrapper<Device>().eq(Device::getCode, device.getCode()).eq(Device::getDelFlag, 0).last("limit 1"));
            if (count > 0){
                return Result.error("设备编号不能重复");
            }
            deviceService.save(device);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

/*
    *//**
     * 通过id删除
     *
     * @param id
     * @return
     *//*
    @AutoLog(value = "设备-通过id删除")
    @ApiOperation(value = "设备-通过id删除", notes = "设备-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            QueryWrapper<Device> deviceQueryWrapper = new QueryWrapper<>();
            deviceQueryWrapper.eq("id", id);
            Device device = deviceService.getOne(deviceQueryWrapper);

            QueryWrapper<DeviceAssembly> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("device_code", device.getCode());

            deviceService.removeById(id);

        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    *//**
     * 批量删除
     *
     * @param ids
     * @return
     *//*
    @AutoLog(value = "设备-批量删除")
    @ApiOperation(value = "设备-批量删除", notes = "设备-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<Device> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<Device> result = new Result<Device>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            List<Device> list = this.deviceService.lambdaQuery().eq(Device::getId, Arrays.asList(ids.split(","))).select(Device::getCode).list();
            this.deviceService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }*//*
    *//**
     * 通过id删除
     *
     * @param id
     * @return
     *//*
    @AutoLog(value = "设备-通过id删除")
    @ApiOperation(value = "设备-通过id删除", notes = "设备-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            QueryWrapper<Device> deviceQueryWrapper = new QueryWrapper<>();
            deviceQueryWrapper.eq("id", id);
            Device device = deviceService.getOne(deviceQueryWrapper);

            QueryWrapper<DeviceAssembly> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("device_code", device.getCode());

            deviceService.removeById(id);

        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    *//**
     * 批量删除
     *
     * @param ids
     * @return
     *//*
    @AutoLog(value = "设备-批量删除")
    @ApiOperation(value = "设备-批量删除", notes = "设备-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<Device> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<Device> result = new Result<Device>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            List<Device> list = this.deviceService.lambdaQuery().eq(Device::getId, Arrays.asList(ids.split(","))).select(Device::getCode).list();
            this.deviceService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }*/
}
