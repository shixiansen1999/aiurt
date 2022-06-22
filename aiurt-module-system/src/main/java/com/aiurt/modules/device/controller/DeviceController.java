package com.aiurt.modules.device.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.entity.DeviceAssembly;
import com.aiurt.modules.device.service.IDeviceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

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
                                               HttpServletRequest req) {
        Result<IPage<Device>> result = new Result<IPage<Device>>();
        Map<String, String[]> parameterMap = req.getParameterMap();

        QueryWrapper<Device> queryWrapper = QueryGenerator.initQueryWrapper(device, parameterMap);
        queryWrapper.eq("del_flag", 0);
        Page<Device> page = new Page<Device>(pageNo, pageSize);
        IPage<Device> pageList = deviceService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @ApiOperation(value = "设备详情查询", notes = "设备详情查询")
    @PostMapping(value = "/queryDetailById")
    public Result<Device> queryDetailById(@RequestParam(name = "id", required = true) String deviceId) {
        return deviceService.queryDetailById(deviceId);
    }

    /**
     * 添加
     *
     * @param device
     * @return
     */
    @AutoLog(value = "设备-添加")
    @ApiOperation(value = "设备-添加", notes = "设备-添加")
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

            QueryWrapper<DeviceAssembly> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("device_code", device.getCode());

            deviceService.removeById(id);

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
    }
}
