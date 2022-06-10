package com.aiurt.boot.modules.device.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.modules.device.entity.Device;
import com.aiurt.boot.modules.device.entity.DeviceType;
import com.aiurt.boot.modules.device.service.IDeviceService;
import com.aiurt.boot.modules.device.service.IDeviceTypeService;
import com.aiurt.boot.modules.manage.entity.Line;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.aiurt.boot.modules.manage.service.ILineService;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.manage.service.ISubsystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * create By lhz on 2021/10/9 11:29.
 */
@Slf4j
@Api(tags="设备报废管理")
@RestController
@RequestMapping("/device/deviceScrap")
public class DeviceScrapController {

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IDeviceTypeService deviceTypeService;

    @Autowired
    private ILineService lineService;

    @Autowired
    private IStationService stationService;

    @Autowired
    private ISubsystemService subsystemService;

    /**
     * 分页列表查询
     * @param device
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "设备报废-分页列表查询")
    @ApiOperation(value="设备报废-分页列表查询", notes="设备报废-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<Device>> queryPageList(Device device,
                                               @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                               @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        Result<IPage<Device>> result = new Result<IPage<Device>>();
        QueryWrapper<Device> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", 0);
        queryWrapper.eq("scrap_flag",1);
        if(device.getName()!=null){
            queryWrapper.like("name",device.getName());
        }
        if(device.getStatus()!=null){
            queryWrapper.eq("status",device.getStatus());
        }
        if(device.getStationCode()!=null){
            queryWrapper.eq("station_code",device.getStationCode());
        }
        if(device.getLocation()!=null){
            queryWrapper.eq("location",device.getLocation());
        }
        if(device.getSystemCode()!=null && device.getTypeCode()==null){
            queryWrapper.eq("system_code",device.getSystemCode());
        }
        if(device.getSystemCode()!=null && device.getTypeCode()!=null){
            queryWrapper.eq("system_code",device.getSystemCode());
            queryWrapper.eq("type_code",device.getTypeCode());
        }
        queryWrapper.select().orderByDesc("create_time");
        Page<Device> page = new Page<Device>(pageNo, pageSize);
        IPage<Device> pageList = deviceService.page(page, queryWrapper);
        List<Device> list=pageList.getRecords();
        deviceService.addNeedInformation(list);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     * @param response
     */
    @AutoLog(value = "导出excel")
    @ApiOperation(value="导出excel", notes="导出excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        QueryWrapper<Device> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("del_flag",0);
        queryWrapper.eq("scrap_flag",1);
        List<Device> list = deviceService.list(queryWrapper);
        list.forEach(l->{
            QueryWrapper<DeviceType> deviceTypeQueryWrapper=new QueryWrapper<>();
            deviceTypeQueryWrapper.eq("code",l.getTypeCode());
            DeviceType deviceType=deviceTypeService.getOne(deviceTypeQueryWrapper,false);
            l.setTypeName(deviceType.getName());

            Line lineOne = lineService.getOne(new LambdaQueryWrapper<Line>().eq(Line::getLineCode, l.getLineCode()));
            l.setLineName(lineOne.getLineName());

            Station station=stationService.getOne(new LambdaQueryWrapper<Station>().eq(Station::getStationCode, l.getStationCode()));
            l.setStationName(station.getStationName());

            Subsystem one = subsystemService.getOne(new LambdaQueryWrapper<Subsystem>().eq(Subsystem::getSystemCode, l.getSystemCode()));
            l.setSystemName(one.getSystemName());
        });
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "设备报废列表");
        mv.addObject(NormalExcelConstants.CLASS, Device.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("设备报废列表数据", "导出人:Jeecg", "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }
}
