package com.aiurt.modules.syntheticalpanel.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.device.service.IDeviceTypeService;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.service.ICsStationService;
import com.aiurt.modules.syntheticalpanel.model.PositionPanelModel;
import com.aiurt.modules.syntheticalpanel.service.PositionPanelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 综合看板线路信息
 * @Author: lkj
 * @Date:   2022-09-13
 */
@Api(tags="综合看板线路信息")
@RestController
@RequestMapping("/syntheticalpanel/positionPanel")
@Slf4j
public class PositionPanelController {

    @Autowired
    private PositionPanelService positionPanelService;

    @Autowired
    private IDeviceTypeService deviceTypeService;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private ISysBaseAPI iSysBaseAPI;

    @Autowired
    private ICsStationService iCsStationService;


    /**
     * 综合大屏线路工区查询
     *
     * @param positionPanel
     * @return
     */
    @AutoLog(value = "综合大屏线路工区-查询", operateType =  1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value="综合大屏线路工区查询", notes="综合大屏线路工区查询")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "")
    public Result<List<CsStation>> queryPageList(PositionPanelModel positionPanel) {
        List<CsStation> positionPanels = positionPanelService.readAll(positionPanel);
        return Result.OK(positionPanels);
    }

    /**
     * 通过名称查询
     *
     * @param stationName
     * @return
     */
    @AutoLog(value = "综合看板线路站点信息-通过名称查询", operateType =  1, operateTypeAlias = "查询-通过名称查询", permissionUrl = "")
    @ApiOperation(value="综合看板线路站点信息-通过名称查询", notes="综合看板线路站点信息-通过名称查询")
    @PostMapping(value = "/queryById")
    public Result<List<PositionPanelModel>> queryById(@RequestParam(name="stationName",required=true)  String stationName) {
        List<PositionPanelModel> positionPanels = positionPanelService.queryById(stationName);
        return Result.OK(positionPanels);
    }

    /**
     *  根据线路站点编辑
     *
     * @param positionPanel
     * @return
     */
    @AutoLog(value = "综合看板线路站点信息-编辑", operateType =  3, operateTypeAlias = "编辑", permissionUrl = "")
    @ApiOperation(value="综合看板线路站点信息-编辑", notes="综合看板线路站点信息-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
    public Result<String> edit(@RequestBody PositionPanelModel positionPanel) {
        positionPanelService.edit(positionPanel);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过线路站点查询
     *
     * @param lineName
     * @param stationName
     * @return
     */
    @AutoLog(value = "综合看板监控设备信息-通过线路站点查询", operateType =  1, operateTypeAlias = "查询-通过线路站点查询", permissionUrl = "")
    @ApiOperation(value="综合看板监控设备信息-通过线路站点查询", notes="综合看板监控设备信息-通过线路站点查询")
    @PostMapping(value = "/getMonitorDevice")
    public Result<IPage<Device>> getMonitorDevice(@RequestParam(name="lineName",required=true)  String lineName,
                                                  @RequestParam(name="stationName",required=true)  String stationName,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
                                                  ){
        String string = "摄像机";
        Result<IPage<Device>> result = new Result<>();
        LambdaQueryWrapper<DeviceType> deviceTypeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deviceTypeLambdaQueryWrapper.eq(DeviceType::getDelFlag,CommonConstant.DEL_FLAG_0)
                  .eq(DeviceType::getName,string);
        List<DeviceType> deviceTypeList = deviceTypeService.list(deviceTypeLambdaQueryWrapper);

        //根据线路名称和站点名称查询线路编码和站点编码
        List<CsStation> csStation = new ArrayList<>();
        LambdaQueryWrapper<CsStation> csStationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(lineName)){
            csStationLambdaQueryWrapper.eq(CsStation::getLineName,lineName);
        }
        if (StrUtil.isNotBlank(stationName)){
            csStationLambdaQueryWrapper.eq(CsStation::getStationName,stationName);
        }
        csStationLambdaQueryWrapper.eq(CsStation::getDelFlag,CommonConstant.DEL_FLAG_0);
        csStation = iCsStationService.list(csStationLambdaQueryWrapper);

        Page<Device> devicePage = new Page<>();
        if (CollUtil.isNotEmpty(deviceTypeList)){
            List<String> collect = deviceTypeList.stream().map(DeviceType::getCode).collect(Collectors.toList());
            LambdaQueryWrapper<Device> deviceLambdaQueryWrapper = new LambdaQueryWrapper<>();
            deviceLambdaQueryWrapper.eq(Device::getDelFlag,CommonConstant.DEL_FLAG_0)
                    .in(Device::getDeviceTypeCode,collect);
            if(CollUtil.isNotEmpty(csStation)){
                List<String> collect1 = csStation.stream().map(CsStation::getLineCode).collect(Collectors.toList());
                deviceLambdaQueryWrapper.in(Device::getLineCode,collect1);
            }
            if (CollUtil.isNotEmpty(csStation)){
                List<String> collect2 = csStation.stream().map(CsStation::getStationCode).collect(Collectors.toList());
                deviceLambdaQueryWrapper.in(Device::getStationCode,collect2);
            }

           devicePage = deviceService.page(new Page<>(pageNo, pageSize), deviceLambdaQueryWrapper);
        }
        if (CollUtil.isNotEmpty(devicePage.getRecords())){
            devicePage.getRecords().forEach(e->{
                if(StrUtil.isNotBlank(e.getStationCode())){
                    String position = iSysBaseAPI.getPosition(e.getStationCode());
                    e.setStationCodeName(position);
                }
            });
        }
        result.setSuccess(true);
        result.setResult(devicePage);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }
}
