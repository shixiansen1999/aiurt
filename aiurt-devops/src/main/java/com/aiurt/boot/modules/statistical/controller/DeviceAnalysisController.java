package com.aiurt.boot.modules.statistical.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.aiurt.boot.common.result.SpareConsumeNum;
import com.aiurt.boot.modules.device.entity.Device;
import com.aiurt.boot.modules.device.service.IDeviceService;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.mapper.StationMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartScrapService;
import com.aiurt.boot.modules.statistical.vo.DeviceDataVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author stephen
 * @version 1.0
 * @date 2022/01/24
 */

@Slf4j
@Api(tags = "大屏-设备分析")
@RestController
@RequestMapping("/deviceAnalysis")
public class DeviceAnalysisController {

    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private ISparePartScrapService sparePartScrapService;
    @Resource
    private StationMapper stationMapper;


    @ApiOperation(value = "设备总数&备件消耗数", notes = "设备总数&备件消耗数")
    @PostMapping("/getDeviceAndPartsCount")
    public Result<DeviceDataVo> getDeviceAndPartsCount(@RequestParam(name = "lineId", required = false) String lineId,
                                                       @RequestParam(name = "organizationId", required = false) String organizationId) {
        Result result = new Result();
        Map map = new HashMap();
        map.put("lineId", lineId);

        //根据班组查询班组下的站点
        if (StringUtils.isNotBlank(organizationId)) {
            List<Station> stations = stationMapper.selectList(new QueryWrapper<Station>().eq("team_id", organizationId));
            final List<String> list = stations.stream().map(Station::getStationCode).collect(Collectors.toList());
            String[] strings = new String[list.size()];
            list.toArray(strings);
            map.put("station_code", strings);
        }
        Integer dNum = deviceService.getDeviceNum(map);

        Integer cNum = sparePartScrapService.getConsumeNum(map);
        DeviceDataVo deviceDataVo = new DeviceDataVo();
        deviceDataVo.setValue1(dNum);
        deviceDataVo.setValue2(cNum);
        result.setResult(deviceDataVo);
        return result;
    }

    @ApiOperation(value = "系统设备数", notes = "系统设备数")
    @PostMapping("/getSystemDeviceData")
    public Result getSystemDeviceData(@RequestParam(name = "lineId", required = false) String lineId,
                                      @RequestParam(name = "organizationId", required = false) String organizationId) {
        Result result = new Result();
        Map map = new HashMap();
        map.put("lineId", lineId);
        //根据班组查询班组下的站点
        if (StringUtils.isNotBlank(organizationId)) {
            List<Station> stations = stationMapper.selectList(new QueryWrapper<Station>().eq("team_id", organizationId));
            final List<String> list = stations.stream().map(Station::getStationCode).collect(Collectors.toList());
            String[] strings = new String[list.size()];
            list.toArray(strings);
            map.put("station_code", strings);
        }
        List<DeviceDataVo> list = deviceService.getSystemDeviceData(map);
        result.setResult(list);
        return result;
    }

    @ApiOperation(value = "系统/站点设备数弹窗详情", notes = "系统/站点设备数弹窗详情")
    @PostMapping("/getSystemDeviceDetailData")
    public Result getSystemDeviceData(@RequestParam(name = "lineId", required = false) String lineId,
                                      @RequestParam(name = "organizationId", required = false) String organizationId,
                                      @RequestParam(name = "systemCode", required = false) String systemCode,
                                      @RequestParam(name = "stationCode", required = false) String stationCode
                                      ) {
        Result result = new Result();
        QueryWrapper<Device> queryWrapper = new QueryWrapper<Device>();
        queryWrapper.eq("del_flag",0);
        if (StringUtils.isNotBlank(systemCode)) {
            queryWrapper.eq("system_code",systemCode);
        }
        if (StringUtils.isNotBlank(lineId)) {
            queryWrapper.eq("line_code",lineId);
        }
        //根据班组查询班组下的站点
        if (StringUtils.isNotBlank(organizationId)) {
            List<Station> stations = stationMapper.selectList(new QueryWrapper<Station>().eq("team_id", organizationId));
            final List<String> list = stations.stream().map(Station::getStationCode).collect(Collectors.toList());
            queryWrapper.in("station_code", list);
        }
        if (StringUtils.isNotBlank(stationCode)) {
            queryWrapper.eq("station_code",stationCode);
        }
        List<Device> list = deviceService.list(queryWrapper);
        deviceService.addNeedInformation(list);
        result.setResult(list);
        return result;
    }

    @ApiOperation(value = "备件年消耗数", notes = "备件年消耗数")
    @PostMapping("/getPartsNumByYear")
    public Result getPartsConsumeByYear(@RequestParam(name = "lineId", required = false) String lineId,
                                        @RequestParam(name = "organizationId", required = false) String organizationId) {
        Result result = new Result();
        Map map = new HashMap();
        map.put("lineId", lineId);
        //根据班组查询班组下的站点
        if (StringUtils.isNotBlank(organizationId)) {
            List<Station> stations = stationMapper.selectList(new QueryWrapper<Station>().eq("team_id", organizationId));
            final List<String> list = stations.stream().map(Station::getStationCode).collect(Collectors.toList());
            String[] strings = new String[list.size()];
            list.toArray(strings);
            map.put("station_code", strings);
        }
        List<SpareConsumeNum> list = sparePartScrapService.getSpareConsumeNumByTime(map);
        result.setResult(list);
        return result;
    }

    @ApiOperation(value = "站点设备数", notes = "站点设备数")
    @PostMapping("/getDeviceNumByStation")
    public Result getDeviceNumByStation(@RequestParam(name = "lineId", required = false) String lineId,
                                        @RequestParam(name = "organizationId", required = false) String organizationId) {
        Result result = new Result();
        Map map = new HashMap();
        map.put("lineId", lineId);
        //根据班组查询班组下的站点
        if (StringUtils.isNotBlank(organizationId)) {
            List<Station> stations = stationMapper.selectList(new QueryWrapper<Station>().eq("team_id", organizationId));
            final List<String> list = stations.stream().map(Station::getStationCode).collect(Collectors.toList());
            String[] strings = new String[list.size()];
            list.toArray(strings);
            map.put("station_code", strings);
        }
        List<DeviceDataVo> list = deviceService.getDeviceNumByStation(map);
        result.setResult(list);
        return result;
    }


}
