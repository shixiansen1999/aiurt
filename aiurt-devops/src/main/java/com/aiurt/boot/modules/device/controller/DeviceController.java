package com.aiurt.boot.modules.device.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.exception.SwscException;
import com.aiurt.boot.modules.device.entity.Device;
import com.aiurt.boot.modules.device.entity.DeviceAssembly;
import com.aiurt.boot.modules.device.entity.DeviceSmallType;
import com.aiurt.boot.modules.device.entity.DeviceType;
import com.aiurt.boot.modules.device.mapper.DeviceSmallTypeMapper;
import com.aiurt.boot.modules.device.mapper.DeviceTypeMapper;
import com.aiurt.boot.modules.device.service.IDeviceAssemblyService;
import com.aiurt.boot.modules.device.service.IDeviceService;
import com.aiurt.boot.modules.device.service.IDeviceTypeService;
import com.aiurt.boot.modules.manage.entity.Line;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.aiurt.boot.modules.manage.mapper.StationMapper;
import com.aiurt.boot.modules.manage.service.ILineService;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.manage.service.ISubsystemService;
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

import javax.annotation.Resource;
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
@Api(tags = "设备")
@RestController
@RequestMapping("/device/device")
public class DeviceController {
    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IDeviceTypeService deviceTypeService;

    @Autowired
    private IDeviceAssemblyService deviceAssemblyService;

    @Autowired
    private ISubsystemService subsystemService;

    @Autowired
    private ILineService lineService;

    @Autowired
    private IStationService stationService;

    @Resource
    private StationMapper stationMapper;

    @Resource
    private DeviceTypeMapper deviceTypeMapper;

    @Resource
    private DeviceSmallTypeMapper deviceSmallTypeMapper;


    /**
     * 分页列表查询
     *
     * @param device
     * @param pageNo
     * @param pageSize
     * @param organizationId
     * @return
     */
    @AutoLog(value = "设备-分页列表查询")
    @ApiOperation(value = "设备-分页列表查询", notes = "设备-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<Device>> queryPageList(Device device,
                                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                               @RequestParam(name = "organizationId", required = false) String organizationId,
                                               HttpServletRequest req) {
        Result<IPage<Device>> result = new Result<IPage<Device>>();
        Map<String, String[]> parameterMap = req.getParameterMap();
        String stationCode = device.getStationCode();
        Station station = stationService.lambdaQuery()
                .eq(StringUtils.isNotBlank(stationCode), Station::getId, stationCode)
                .select(Station::getTeamId).last("limit 1").one();

        QueryWrapper<Device> queryWrapper = QueryGenerator.initQueryWrapper(device, parameterMap);

        //根据班组查询班组下的站点
        if (StringUtils.isNotBlank(organizationId)) {
            List<Station> stations = stationMapper.selectList(new QueryWrapper<Station>().eq("team_id", organizationId));
            final List<String> list = stations.stream().map(Station::getStationCode).collect(Collectors.toList());
            queryWrapper.in("station_code", list);
        }
        queryWrapper.eq("del_flag", 0);
        queryWrapper.eq("scrap_flag", 0);
        Page<Device> page = new Page<Device>(pageNo, pageSize);
        IPage<Device> pageList = deviceService.page(page, queryWrapper);
        List<Device> list = pageList.getRecords();
		deviceService.addNeedInformation(list);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @ApiOperation(value = "设备详情查询", notes = "设备详情查询")
    @PostMapping(value = "/queryDetailById")
    public Result<Device> queryDetailById(@RequestParam(name = "deviceId", required = true) String deviceId) {
        return deviceService.queryDetailById(deviceId);
    }

    @ApiOperation(value = "根据ids查询设备详情", notes = "根据ids查询设备详情")
    @PostMapping(value = "/queryDetailByIds")
    public Result< List<Device>> queryDetailByIds(@RequestParam(name = "deviceIds", required = true)@NotBlank(message = "id不能为空") String deviceIds) {
        List<Device> list = new ArrayList<>();
        String[] split = deviceIds.split(",");
        for (String id : split) {
            Result<Device> result = deviceService.queryDetailById(id);
            if (result!=null){
                list.add(result.getResult());
            }
        }
        return Result.ok(list);
    }

    @ApiOperation(value = "根据codes查询设备详情", notes = "根据codes查询设备详情")
    @PostMapping(value = "/queryDetailByCodes")
    public Result< List<Device>> queryDetailByCodes(@RequestParam(name = "deviceCodes", required = true)@NotBlank(message = "codes不能为空") String deviceCodes) {
        List<Device> list = new ArrayList<>();
        String[] split = deviceCodes.split(",");
        for (String code : split) {
            Device one = deviceService.getOne(new QueryWrapper<Device>().eq("code", code), false);
            Long id = one.getId();
            String s = String.valueOf(id);
            Result<Device> deviceResult = deviceService.queryDetailById(s);
            if (deviceResult!=null) {
                list.add(deviceResult.getResult());
            }
        }
        return Result.ok(list);
    }

    /**
     * app列表查询
     *
     * @param device
     * @return
     */
    @AutoLog(value = "设备-app列表查询")
    @ApiOperation(value = "设备-app列表查询", notes = "设备-app列表查询")
    @GetMapping(value = "/appList")
    public Result<List<Device>> queryAppList(Device device) {
        Result<List<Device>> result = new Result<List<Device>>();
        if (device == null) {
            return result.error500("请求参数不能为空");
        }
        if (StrUtil.isEmpty(device.getSystemCode())){
            return result.error500("请求参数systemCode不能为空");
        }
        if (StrUtil.isEmpty(device.getTypeCode())){
            return result.error500("请求参数typeCode不能为空");
        }
        QueryWrapper<Device> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(device.getStationCode()!=null,Device::getStationCode,device.getStationCode());
        queryWrapper.eq("del_flag", 0);
        queryWrapper.eq("status",1);
        queryWrapper.eq("scrap_flag", 0);
        queryWrapper.eq("system_code", device.getSystemCode());
        queryWrapper.eq("type_code", device.getTypeCode());
        queryWrapper.select().orderByDesc("create_time");
        List<Device> list = deviceService.list(queryWrapper);
        deviceService.addNeedInformation(list);
        result.setSuccess(true);
        result.setResult(list);
        return result;
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
            Subsystem one = subsystemService.getOne(new LambdaQueryWrapper<Subsystem>().eq(Subsystem::getSystemCode, device.getSystemCode()));
            if (one != null) {
                device.setSystemName(one.getSystemName());
            }

            Line lineOne = lineService.getOne(new LambdaQueryWrapper<Line>().eq(Line::getLineCode, device.getLineCode()));
            if (lineOne != null) {
                device.setLineName(lineOne.getLineName());
            }
            deviceService.save(device);
            //添加设备组件信息
            final List<DeviceAssembly> deviceAssemblyList = device.getDeviceAssemblyList();
            if (deviceAssemblyList!= null){
                deviceAssemblyList.forEach(x->x.setDeviceCode(device.getCode()));
                deviceAssemblyService.saveBatch(device.getDeviceAssemblyList());
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
                //更新设备组件信息
                final List<DeviceAssembly> list = deviceAssemblyService.list(new LambdaQueryWrapper<DeviceAssembly>().eq(DeviceAssembly::getDeviceCode, device.getCode()));
                final List<Long> collect = list.stream().map(DeviceAssembly::getId).collect(Collectors.toList());
                if (collect != null && collect.size() >0 ){
                    deviceAssemblyService.removeByIds(collect);
                }
                final List<DeviceAssembly> deviceAssemblyList = device.getDeviceAssemblyList();
                if (deviceAssemblyList!= null){
                    deviceAssemblyList.forEach(x->x.setDeviceCode(device.getCode()));
                    deviceAssemblyService.saveBatch(device.getDeviceAssemblyList());
                }
            }catch (Exception e){
                throw new SwscException(e.getMessage());
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
            List<DeviceAssembly> deviceAssemblies = deviceAssemblyService.list(queryWrapper);
            deviceAssemblies.forEach(d -> {
                deviceAssemblyService.removeById(d.getId());
            });

            deviceService.removeById(id);

        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    /**
     * 通过id报废
     *
     * @param id
     * @return
     */
    @AutoLog(value = "设备-通过id报废")
    @ApiOperation(value = "设备-通过id报废", notes = "设备-通过id报废")
    @PostMapping(value = "/scrap")
    public Result<?> scrap(@RequestParam(name = "id", required = true) String id) {
        try {
            QueryWrapper<Device> deviceQueryWrapper = new QueryWrapper<>();
            deviceQueryWrapper.eq("id", id);
            Device device = deviceService.getOne(deviceQueryWrapper);
            device.setScrapFlag(1);
            device.setScrapTime(new Date());

            deviceService.updateById(device);

        } catch (Exception e) {
            log.error("报废失败", e.getMessage());
            return Result.error("报废失败!");
        }
        return Result.ok("报废成功!");
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
            if (CollectionUtils.isNotEmpty(list)) {
                deviceAssemblyService.lambdaUpdate().in(DeviceAssembly::getDeviceCode, list.stream().map(Device::getCode).collect(Collectors.toList())).remove();
            }
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
    @AutoLog(value = "设备-通过id查询")
    @ApiOperation(value = "设备-通过id查询", notes = "设备-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<Device> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<Device> result = new Result<Device>();
        Device device = deviceService.getById(id);
        if (device == null) {
            result.onnull("未找到对应实体");
        } else {
            result.setResult(device);
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
    @AutoLog(value = "导出excel")
    @ApiOperation(value = "导出excel", notes = "导出excel")
    @RequestMapping(value = "/exportXls")
    public Result<?> exportXls(HttpServletRequest request, HttpServletResponse response) {
//        String userName = TokenUtils.getUserName(request, iSysBaseAPI);
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        QueryWrapper<Device> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", 0);
        List<Device> list = deviceService.list(queryWrapper);


        if (CollectionUtils.isNotEmpty(list)) {
            Map<String, List<Device>> deviceMap = list.stream().collect(Collectors.groupingBy(Device::getTypeCode));
            List<DeviceType> types = deviceTypeMapper.selectList(new LambdaQueryWrapper<DeviceType>().in(DeviceType::getCode, deviceMap.keySet()).eq(DeviceType::getDelFlag, CommonConstant.DEL_FLAG_0));

            Set<String> codeSet = null;
            Set<String> lineSet = null;
            Set<String> stationSet = null;
            Set<String> systemCodeSet = null;

            for (Device device : list) {
                if (device.getTypeCode() != null) {
                    if (codeSet == null) {
                        codeSet = new HashSet<>();
                    }
                    codeSet.add(device.getTypeCode());
                }
                if (device.getStationCode() != null) {
                    if (stationSet == null) {
                        stationSet = new HashSet<>();
                    }
                    stationSet.add(device.getStationCode());
                }

                if (device.getLineCode() != null) {
                    if (lineSet == null) {
                        lineSet = new HashSet<>();
                    }
                    lineSet.add(device.getLineCode());
                }
                if (device.getSystemCode() != null) {
                    if (systemCodeSet == null) {
                        systemCodeSet = new HashSet<>();
                    }
                    systemCodeSet.add(device.getSystemCode());
                }
            }
            Map<String, String> codeNameMap = types.stream().collect(Collectors.toMap(DeviceType::getCode, DeviceType::getName));
            List<Station> stationList = stationService.lambdaQuery().in(Station::getStationCode, stationSet).eq(Station::getDelFlag, CommonConstant.DEL_FLAG_0).list();
            List<Subsystem> subsystemList = subsystemService.lambdaQuery().in(Subsystem::getSystemCode, systemCodeSet).eq(Subsystem::getDelFlag, CommonConstant.DEL_FLAG_0).list();
            List<Line> lineList = lineService.lambdaQuery().in(Line::getLineCode, lineSet).eq(Line::getDelFlag, 0).list();

            Map<String, Station> stationMap = null;
            Map<String, Subsystem> systemMap = null;
            Map<String, Line> lineMap = null;

            if (CollectionUtils.isNotEmpty(stationList)) {
                stationMap = stationList.stream().collect(Collectors.toMap(Station::getStationCode, s -> s));
            }
            if (CollectionUtils.isNotEmpty(subsystemList)) {
                systemMap = subsystemList.stream().collect(Collectors.toMap(Subsystem::getSystemCode, s -> s));
            }
            if (CollectionUtils.isNotEmpty(lineList)) {
                lineMap = lineList.stream().collect(Collectors.toMap(Line::getLineCode, l -> l));
            }


            Map<String, Line> finalLineMap = lineMap;
            Map<String, Station> finalStationMap = stationMap;
            Map<String, Subsystem> finalSystemMap = systemMap;

            list.forEach(l -> {
                if (l.getTypeCode() != null) {
                    l.setTypeName(codeNameMap.get(l.getTypeCode()));
                }
                if (finalLineMap != null) {
                    Line line = finalLineMap.get(l.getLineCode());
                    if (line != null) {
                        l.setLineName(line.getLineName());
                    }
                }
                if (finalStationMap != null) {
                    Station station = finalStationMap.get(l.getStationCode());
                    if (station != null) {
                        l.setStationName(station.getStationName());
                    }
                }
                if (finalSystemMap != null) {
                    Subsystem one = finalSystemMap.get(l.getSystemCode());
                    if (one != null) {
                        l.setSystemName(one.getSystemName());
                    }
                }
                if (StrUtil.isNotEmpty(l.getSmallTypeCode())) {
                    final DeviceSmallType smallType = deviceSmallTypeMapper.selectOne(Wrappers.<DeviceSmallType>query().lambda().eq(DeviceSmallType::getCode, l.getSmallTypeCode()).last("limit 1"));
                    if (smallType != null) {
                        l.setSmallTypeName(smallType.getName());
                    }
                }

            });
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ArrayList<Map<String, Object>> listMap = new ArrayList<>();
        //设备
        Map<String, Object> map = new HashMap<>();
        map.put("title",getExportParams(null));
        map.put("entity",Device.class);//表格对应实体

        List<Device> ls=new ArrayList<> ();
        for(int j=0;j<list.size();j++){
            Device device = list.get(j);
            ls.add(device);
        }
        map.put("data", ls);
        listMap.add(map);

        //设置组件
        final List<String> collect = list.stream().filter(x -> StrUtil.isNotEmpty(x.getMaterials())).map(Device::getCode).collect(Collectors.toList());
        final List<DeviceAssembly> deviceAssemblyList = deviceAssemblyService.list(new LambdaQueryWrapper<DeviceAssembly>().in(DeviceAssembly::getDeviceCode, collect));

        Map<String, Object> map2 = new HashMap<>();
        map2.put("title",getExportParams(null));//表格title
        map2.put("entity",DeviceAssembly.class);//表格对应实体

        List<DeviceAssembly> ls2=new ArrayList<> ();
        for(int j=0;j<deviceAssemblyList.size();j++){
            final DeviceAssembly deviceAssembly = deviceAssemblyList.get(j);
            ls2.add(deviceAssembly);
        }
        map2.put("data", ls2);
        listMap.add(map2);


        Workbook workbook = ExcelExportUtil.exportExcel(listMap, ExcelType.XSSF);
        try {
            workbook.write(response.getOutputStream());
            if (null != os) {
                os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    //导出参数
    private ExportParams getExportParams(String name) {
        //表格名称,sheet名称,导出版本
        return  new ExportParams(name,name,ExcelType.XSSF);
    }

    /**
     * 下载设备信息模板
     *
     * @param response
     * @param request
     *  @throws IOException
     */
    @AutoLog(value = "下载设备信息模板")
    @ApiOperation(value="下载设备信息模板", notes="下载设备信息模板")
    @RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
    public void downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
        ClassPathResource classPathResource =  new ClassPathResource("template/Device.xlsx");
        InputStream bis = classPathResource.getInputStream();
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        int len = 0;
        while ((len = bis.read()) != -1) {
            out.write(len);
            out.flush();
        }
        out.close();
    }

    /**
     * 通过excel导入设备信息数据
     *
     * @param request
     * @return
     */
	@Transactional(rollbackFor = Exception.class)
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request) {
        final List<Subsystem> subsystems = subsystemService.lambdaQuery().eq(Subsystem::getDelFlag,CommonConstant.DEL_FLAG_0).list();
        final Map<String, String> subMap = subsystems.stream().collect(Collectors.toMap(Subsystem::getSystemName, Subsystem::getSystemCode));
        final List<Line> lines = lineService.lambdaQuery().eq(Line::getDelFlag,CommonConstant.DEL_FLAG_0).list();
        final Map<String, String> lineMap = lines.stream().collect(Collectors.toMap(Line::getLineName, Line::getLineCode));
        final List<Station> stations = stationService.lambdaQuery().eq(Station::getDelFlag, CommonConstant.DEL_FLAG_0).list();
        final Map<String, String> stationMap = stations.stream().collect(Collectors.toMap(Station::getStationName, Station::getStationCode));
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            try {
                ImportParams params = new ImportParams();
                params.setTitleRows(0);
                params.setHeadRows(1);
                params.setSheetNum(2);
                params.setNeedSave(true);
                List<DeviceAssembly> deviceAssemblyList = ExcelImportUtil.importExcel(file.getInputStream(), DeviceAssembly.class, params);
                final List<DeviceAssembly> list = deviceAssemblyList.stream().filter(x -> StrUtil.isNotEmpty(x.getDeviceCode())).collect(Collectors.toList());
                //过滤 同一个device_code只有一个code
                final Map<String, List<DeviceAssembly>> listMap = list.stream().collect(Collectors.groupingBy(d->d.getDeviceCode().concat(d.getCode())));
                List<DeviceAssembly> saveList = new ArrayList<>();
                for (String mm : listMap.keySet()) {
                    final List<DeviceAssembly> assemblies = listMap.get(mm);
                    if (assemblies!=null){
                        final DeviceAssembly assembly = assemblies.get(0);
                        saveList.add(assembly);
                    }
                }
                deviceAssemblyService.saveBatch(saveList);

                final Map<String, List<DeviceAssembly>> map = list.stream().collect(Collectors.groupingBy(DeviceAssembly::getDeviceCode));


                List<Device> listDevices = ExcelImportUtil.importExcel(file.getInputStream(), Device.class, params);
                listDevices = listDevices.stream().filter(x -> StrUtil.isNotEmpty(x.getCode())).collect(Collectors.toList());
                listDevices.forEach(x->{
                    final LambdaQueryWrapper<DeviceType> wrapper = new LambdaQueryWrapper<DeviceType>()
                            .eq(DeviceType::getSystemCode, subMap.get(x.getSystemCode())).eq(DeviceType::getName,x.getTypeCode());
                    final DeviceType deviceType = deviceTypeMapper.selectOne(wrapper);
                    if (deviceType == null){
                        return;
                    }
	                if (StringUtils.isBlank(x.getSystemCode())){
		                throw new SwscException("系统不能为空");
	                }
	                if (StringUtils.isBlank(x.getLineCode())){
		                throw new SwscException("线路不能为空");
	                }
	                if (StringUtils.isBlank(x.getStationCode())){
		                throw new SwscException("站点不能为空");
	                }
	                if (StringUtils.isBlank(subMap.get(x.getSystemCode()))){
		                throw new SwscException(x.getSystemCode()+" 系统未查询到");
	                }
	                if (StringUtils.isBlank(lineMap.get(x.getLineCode()))){
		                throw new SwscException(x.getLineCode()+" 线路未找到");
	                }
	                if (StringUtils.isBlank(stationMap.get(x.getStationCode()))){
		                throw new SwscException(stationMap.get(x.getStationCode())+" 站点未查询到");
	                }

                    x.setTypeCode(deviceType.getCode());
                    x.setSystemCode(subMap.get(x.getSystemCode()));
                    x.setLineCode(lineMap.get(x.getLineCode()));
                    x.setStationCode(stationMap.get(x.getStationCode()));

                    final List<DeviceSmallType> smallTypeList = deviceSmallTypeMapper.selectList(Wrappers.<DeviceSmallType>query().lambda()
                            .eq(DeviceSmallType::getName, x.getSmallTypeName()));
                    for (DeviceSmallType smallType : smallTypeList) {
                        final DeviceType device = deviceTypeService.getById(smallType.getDeviceTypeId());
                        if (StrUtil.equals(device.getSystemCode(),x.getSystemCode()) && StrUtil.equals(device.getCode(),x.getTypeCode())){
                            x.setSmallTypeCode(smallType.getCode());
                        }
                    }
                    if (x.getSmallTypeCode()==null){
                        throw new SwscException("未找到 \""+x.getSmallTypeName()+"\" 设备小类");
                    }
                    final List<DeviceAssembly> deviceAssemblies = map.get(x.getCode());
                    if (deviceAssemblies != null && deviceAssemblies.size() > 0){
                        final List<String> collect = deviceAssemblies.stream().map(DeviceAssembly::getCode).collect(Collectors.toList());
                        x.setMaterials(String.join(",",collect));
                    }
                });

                //filter typecode isnull
                final List<Device> deviceList = listDevices.stream()
                        .filter(x -> StrUtil.isNotBlank(x.getTypeCode()))
                        .filter(x -> StrUtil.isNotBlank(x.getCode()))
                        .filter(x -> StrUtil.isNotBlank(x.getSmallTypeCode()))
                        .collect(Collectors.toList());

                //过滤只有一个code
                final ArrayList<Device> collect = deviceList.stream().collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() ->
                                new TreeSet<>(Comparator.comparing(Device::getCode))),
                        ArrayList::new
                ));
                List<String> codes = collect.stream().map(Device::getCode).collect(Collectors.toList());

                List<Device> devices = deviceService.lambdaQuery().in(Device::getCode, codes).eq(Device::getDelFlag, CommonConstant.DEL_FLAG_0).list();
                if (CollectionUtils.isNotEmpty(devices)){
                    throw new SwscException(StringUtils.join(devices.stream().map(Device::getCode).collect(Collectors.toList()),",")+" 编号已存在,不可重复导入");
                }

                deviceService.saveBatch(collect);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new SwscException("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return Result.ok("文件导入成功！");
    }


    /**
     * 通过code查询
     *
     * @param code
     * @return
     */
    @AutoLog(value = "设备-通过code查询")
    @ApiOperation(value = "设备-通过code查询", notes = "设备-通过code查询")
    @GetMapping(value = "/queryByCode")
    public Result<List<Device>> queryByCode(@RequestParam(name = "code", required = true) String code) {
        Result<List<Device>> result = new Result<>();
        final List<?> codeList = Convert.toList(code);
        final List<Device> list = deviceService.list(new LambdaQueryWrapper<Device>().in(Device::getCode, codeList).eq(Device::getDelFlag, 0));
        result.setResult(list);
        result.setSuccess(true);
        return result;
    }
}
