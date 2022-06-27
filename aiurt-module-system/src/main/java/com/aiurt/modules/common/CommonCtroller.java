package com.aiurt.modules.common;

import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.dto.DeviceDTO;
import com.aiurt.modules.common.entity.SelectTable;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Api(tags = "共用模块")
@RestController
@RequestMapping("/common")
public class CommonCtroller {

    @Autowired
    private ICsMajorService csMajorService;

    @Autowired
    private ICsSubsystemService csSubsystemService;

    @Autowired
    private IDeviceService deviceService;




    public Result<List<Device>> query() {
        return Result.OK();
    }

    /**
     * 查询当前人员所管辖的专业
     * @return
     */
    @GetMapping("/major/queryMajorByAuth")
    @ApiOperation("查询当前人员所管辖的专业")
    public Result<List<SelectTable>> queryMajorByAuth() {
        LambdaQueryWrapper<CsMajor> queryWrapper = new LambdaQueryWrapper<>();

        //todo 查询当前人员所管辖的专业。
        List<CsMajor> csMajorList = csMajorService.getBaseMapper().selectList(queryWrapper);

        List<SelectTable> list = csMajorList.stream().map(csMajor -> {
            SelectTable table = new SelectTable();
            table.setLabel(csMajor.getMajorName());
            table.setValue(csMajor.getMajorCode());
            return table;
        }).collect(Collectors.toList());

        return Result.OK(list);
    }



    /**
     * 查询当前人员所管理的专业子系统
     * @return
     */
    @GetMapping("/subsystem/querySubSystemByAuth")
    @ApiOperation("查询当前人员所管理的专业子系统")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "majorCode", value = "专业编码", required = false, paramType = "query"),
    })
    public Result<List<SelectTable>> querySubSystemByAuth(@RequestParam(value = "majorCode", required = false) String majorCode) {
        LambdaQueryWrapper<CsSubsystem> queryWrapper = new LambdaQueryWrapper<>();

        //todo 查询当前人员所管辖的子系统
        if (StrUtil.isNotBlank(majorCode)) {
            queryWrapper.eq(CsSubsystem::getMajorCode, majorCode);
        }

        List<CsSubsystem> csMajorList = csSubsystemService.getBaseMapper().selectList(queryWrapper);
        List<SelectTable> list = csMajorList.stream().map(subsystem -> {
            SelectTable table = new SelectTable();
            table.setLabel(subsystem.getSystemName());
            table.setValue(subsystem.getSystemCode());
            return table;
        }).collect(Collectors.toList());

        return Result.OK(list);
    }

    /**
     * 查询设备
     * @return
     */
    @GetMapping("/device/queryDevice")
    @ApiOperation("查询设备")
    public Result<List<SelectTable>> queryDevice(DeviceDTO deviceDTO) {
        LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
        //todo 查询当前人员所管辖的站所

        if (StrUtil.isNotBlank(deviceDTO.getLineCode())) {
            queryWrapper.eq(Device::getLineCode, deviceDTO.getLineCode());
        }

        if (StrUtil.isNotBlank(deviceDTO.getMajorCode())) {
            queryWrapper.eq(Device::getMajorCode, deviceDTO.getMajorCode());
        }

        if (StrUtil.isNotBlank(deviceDTO.getSystemCode())) {
            queryWrapper.eq(Device::getSystemCode, deviceDTO.getSystemCode());
        }

        if (StrUtil.isNotBlank(deviceDTO.getStationCode())) {
            queryWrapper.eq(Device::getStationCode, deviceDTO.getStationCode());
        }

        if (StrUtil.isNotBlank(deviceDTO.getPositionCode())) {
            queryWrapper.eq(Device::getPositionCode, deviceDTO.getPositionCode());
        }

        //

        List<Device> csMajorList = deviceService.getBaseMapper().selectList(queryWrapper);

        List<SelectTable> list = csMajorList.stream().map(device -> {
            SelectTable table = new SelectTable();
            table.setLabel(device.getName());
            table.setValue(device.getCode());
            return table;
        }).collect(Collectors.toList());

        return Result.OK(list);
    }


    /**
     * 查询设备
     * @return
     */
    @GetMapping("/position/queryTree")
    @ApiOperation("位置下拉数据")
    public Result<List<SelectTable>> queryPositionTree() {
        return Result.OK(null);
    }

}
