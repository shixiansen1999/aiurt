package com.aiurt.modules.common;

import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.dto.DeviceDTO;
import com.aiurt.modules.common.entity.SelectTable;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.entity.CsStationPosition;
import com.aiurt.modules.position.service.ICsLineService;
import com.aiurt.modules.position.service.ICsStationPositionService;
import com.aiurt.modules.position.service.ICsStationService;
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

import java.util.*;
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

    @Autowired
    private ICsLineService lineService;

    @Autowired
    private ICsStationService stationService;

    @Autowired
    private ICsStationPositionService stationPositionService;


    public Result<List<Device>> query() {
        return Result.OK();
    }

    /**
     * 查询当前人员所管辖的专业
     *
     * @return
     */
    @GetMapping("/major/queryMajorByAuth")
    @ApiOperation("查询当前人员所管辖的专业")
    public Result<List<SelectTable>> queryMajorByAuth() {
        LambdaQueryWrapper<CsMajor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CsMajor::getDelFlag, 0);

        //todo 查询当前人员所管辖的专业。
        List<CsMajor> csMajorList = csMajorService.getBaseMapper().selectList(queryWrapper);

        List<SelectTable> list = csMajorList.stream().map(csMajor -> {
            SelectTable table = new SelectTable();
            table.setLabel(csMajor.getMajorName());
            table.setKey(csMajor.getId());
            table.setValue(csMajor.getMajorCode());
            return table;
        }).collect(Collectors.toList());

        return Result.OK(list);
    }


    /**
     * 查询当前人员所管理的专业子系统
     *
     * @return
     */
    @GetMapping("/subsystem/querySubSystemByAuth")
    @ApiOperation("查询当前人员所管理的专业子系统")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "majorCode", value = "专业编码", required = false, paramType = "query"),
    })
    public Result<List<SelectTable>> querySubSystemByAuth(@RequestParam(value = "majorCode", required = false) String majorCode) {
        LambdaQueryWrapper<CsSubsystem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CsSubsystem::getDelFlag, 0);

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
     *
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

        if (StrUtil.isNotBlank(deviceDTO.getDeviceTypeCode())) {
            queryWrapper.eq(Device::getDeviceTypeCode, deviceDTO.getDeviceTypeCode());
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

        if (StrUtil.isNotBlank(deviceDTO.getName())) {
            queryWrapper.like(Device::getName, deviceDTO.getName());
        }

        queryWrapper.eq(Device::getDelFlag, 0);
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
     * 根据个人权限获取位置树
     *
     * @return
     */
    @GetMapping("/position/queryTreeByAuth")
    @ApiOperation("根据个人权限获取位置树")
    public Result<List<SelectTable>> queryPositionTree() {
        List<CsLine> lineList = lineService.getBaseMapper().selectList(null);

        Map<String, String> lineMap = lineList.stream().collect(Collectors.toMap(CsLine::getLineCode, CsLine::getLineName, (t1, t2) -> t2));

        LambdaQueryWrapper<CsStation> stationWrapper = new LambdaQueryWrapper<>();

        List<CsStation> stationList = stationService.getBaseMapper().selectList(stationWrapper);

        Map<String, List<CsStation>> stationMap = stationList.stream().collect(Collectors.groupingBy(CsStation::getLineCode));

        LambdaQueryWrapper<CsStationPosition> positionWrapper = new LambdaQueryWrapper<>();

        List<CsStationPosition> positionList = stationPositionService.getBaseMapper().selectList(positionWrapper);

        Map<String, List<CsStationPosition>> positionMap = positionList.stream().collect(Collectors.groupingBy(CsStationPosition::getStaionCode));

        List<SelectTable> list = new ArrayList<>();
        stationMap.keySet().stream().forEach(lineCode -> {
            SelectTable table = new SelectTable();
            table.setLabel(lineMap.get(lineCode));
            table.setValue(lineCode);
            table.setLevel(1);
            table.setLineCode(lineCode);
            //
            List<CsStation> csStationList = stationMap.getOrDefault(lineCode, Collections.emptyList());

            List<SelectTable> lv2List = csStationList.stream().map(csStation -> {
                SelectTable selectTable = new SelectTable();
                selectTable.setValue(csStation.getStationCode());
                selectTable.setLabel(csStation.getStationName());
                selectTable.setLevel(2);
                selectTable.setLineCode(lineCode);
                selectTable.setStationCode(csStation.getStationCode());

                List<CsStationPosition> stationPositionList = positionMap.getOrDefault(csStation.getStationCode(), Collections.emptyList());

                List<SelectTable> tableList = stationPositionList.stream().map(csStationPosition -> {
                    SelectTable tableV = new SelectTable();
                    tableV.setLabel(csStationPosition.getPositionName());
                    tableV.setValue(csStationPosition.getPositionCode());
                    tableV.setLevel(3);
                    tableV.setLineCode(lineCode);
                    tableV.setStationCode(csStation.getStationCode());
                    tableV.setPositionCode(csStationPosition.getPositionCode());
                    return tableV;
                }).collect(Collectors.toList());
                selectTable.setChildren(tableList);
                return selectTable;
            }).collect(Collectors.toList());

            table.setChildren(lv2List);
            list.add(table);
        });
        return Result.OK(list);
    }


    /**
     * 根据个人权限获取位置树
     *
     * @return
     */
    @GetMapping("/position/queryStationTree")
    @ApiOperation("根据个人权限获取站点树")
    public Result<List<SelectTable>> queryStationTree() {
        List<CsLine> lineList = lineService.getBaseMapper().selectList(null);

        Map<String, String> lineMap = lineList.stream().collect(Collectors.toMap(CsLine::getLineCode, CsLine::getLineName, (t1, t2) -> t2));

        LambdaQueryWrapper<CsStation> stationWrapper = new LambdaQueryWrapper<>();

        List<CsStation> stationList = stationService.getBaseMapper().selectList(stationWrapper);

        Map<String, List<CsStation>> stationMap = stationList.stream().collect(Collectors.groupingBy(CsStation::getLineCode));

        List<SelectTable> list = new ArrayList<>();
        stationMap.keySet().stream().forEach(lineCode -> {
            SelectTable table = new SelectTable();
            table.setLabel(lineMap.get(lineCode));
            table.setValue(lineCode);
            table.setLevel(1);
            table.setLineCode(lineCode);
            //
            List<CsStation> csStationList = stationMap.getOrDefault(lineCode, Collections.emptyList());

            List<SelectTable> lv2List = csStationList.stream().map(csStation -> {
                SelectTable selectTable = new SelectTable();
                selectTable.setValue(csStation.getStationCode());
                selectTable.setLabel(csStation.getStationName());
                selectTable.setLevel(2);
                selectTable.setLineCode(lineCode);
                selectTable.setStationCode(csStation.getStationCode());
                return selectTable;
            }).collect(Collectors.toList());

            table.setChildren(lv2List);
            list.add(table);
        });
        return Result.OK(list);
    }

    @GetMapping("/system/queryMajorAndSystemTree")
    @ApiOperation("查询专业系统树")
    public Result<List<SelectTable>> queryMajorAndSystemTree() {
        LambdaQueryWrapper<CsMajor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CsMajor::getDelFlag, 0);

        //todo 查询当前人员所管辖的专业。
        List<CsMajor> csMajorList = csMajorService.getBaseMapper().selectList(queryWrapper);

        List<CsSubsystem> csSubsystemList = csSubsystemService.getBaseMapper().selectList(null);
        Map<String, List<CsSubsystem>> map = csSubsystemList.stream().collect(Collectors.groupingBy(CsSubsystem::getMajorCode));

        List<SelectTable> tableList = csMajorList.stream().map(csMajor -> {
            SelectTable table = new SelectTable();
            table.setLabel(csMajor.getMajorName());
            table.setValue(csMajor.getMajorCode());
            table.setKey(csMajor.getId());

            List<CsSubsystem> csList = map.getOrDefault(csMajor.getMajorCode(), Collections.emptyList());
            List<SelectTable> list = csList.stream().map(subsystem -> {
                SelectTable selectTable = new SelectTable();
                selectTable.setLabel(subsystem.getSystemName());
                selectTable.setKey(subsystem.getId());
                selectTable.setValue(subsystem.getSystemCode());
                return selectTable;
            }).collect(Collectors.toList());
            table.setChildren(list);
            return table;
        }).collect(Collectors.toList());
        return Result.OK(tableList);
    }

}
