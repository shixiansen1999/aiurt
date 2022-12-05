package com.aiurt.modules.common.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.common.dto.DeviceDTO;
import com.aiurt.modules.common.entity.SelectTable;
import com.aiurt.modules.common.service.ICommonService;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.entity.CsStationPosition;
import com.aiurt.modules.position.service.ICsLineService;
import com.aiurt.modules.position.service.ICsStationPositionService;
import com.aiurt.modules.position.service.ICsStationService;
import com.aiurt.modules.system.service.ICsUserMajorService;
import com.aiurt.modules.system.service.ICsUserStaionService;
import com.aiurt.modules.system.service.ICsUserSubsystemService;
import com.aiurt.modules.system.service.impl.CsUserDepartServiceImpl;
import com.aiurt.modules.workarea.entity.WorkArea;
import com.aiurt.modules.workarea.service.IWorkAreaService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fgw
 */

@Slf4j
@Api(tags = "共用模块")
@RestController
@RequestMapping("/common")
public class CommonCtroller {

    /**
     * 系统管理员角色编码
     */
    private static final String ADMIN = "admin";

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private ICsLineService lineService;

    @Autowired
    private ICsStationService stationService;

    @Autowired
    private ICsStationPositionService stationPositionService;

    @Autowired
    private ICsUserMajorService csUserMajorService;

    @Autowired
    private ICsUserSubsystemService csUserSubsystemService;

    @Autowired
    private ICommonService commonService;

    @Autowired
    private ISysBaseAPI sysBaseApi;

    @Autowired
    private ICsUserStaionService userStationService;

    @Autowired
    private CsUserDepartServiceImpl csUserDepartService;

    @Autowired
    private ICsStationPositionService csStationPositionService;

    @Autowired
    private IWorkAreaService workAreaService;

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

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        String userId = loginUser.getId();
        String roleCodes = loginUser.getRoleCodes();

        List<CsUserMajorModel> majorModelList = Collections.emptyList();
        // 系统管理员不做权限过滤
        if (StrUtil.isNotBlank(roleCodes) && roleCodes.indexOf(ADMIN)>-1) {
            majorModelList = csUserMajorService.queryAllMojor();
        }else {
            majorModelList =  csUserMajorService.getMajorByUserId(userId);
        }

        Map<String, CsUserMajorModel> modelMap = majorModelList.stream().collect(Collectors.toMap(CsUserMajorModel::getMajorCode, t -> t, (t1, t2) -> t1));
        List<SelectTable> list = modelMap.keySet().stream().map(key -> {
            SelectTable table = new SelectTable();
            CsUserMajorModel csMajor = modelMap.getOrDefault(key, new CsUserMajorModel());
            table.setLabel(csMajor.getMajorName());
            table.setKey(csMajor.getMajorId());
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
            @ApiImplicitParam(name = "majorIds", value = "专业编码", required = false, paramType = "query"),
    })
    public Result<List<SelectTable>> querySubSystemByAuth(@RequestParam(value = "majorCode", required = false) String majorCode,
                                                          @RequestParam(value ="majorIds",required = false) List<String> majorIds) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = loginUser.getId();
        String roleCodes = loginUser.getRoleCodes();

        List<CsUserSubsystemModel> csMajorList = Collections.emptyList();
        // 系统管理员不做权限过滤
        if (StrUtil.isNotBlank(roleCodes) && roleCodes.indexOf(ADMIN)>-1) {
            csMajorList = csUserSubsystemService.queryAllSubsystem();
        }else {
            csMajorList = csUserSubsystemService.getSubsystemByUserId(userId);
        }

        List<SelectTable> list = new ArrayList<>();
        if (StrUtil.isNotBlank(majorCode)) {
            list = csMajorList.stream().filter(entity -> StrUtil.equalsIgnoreCase(majorCode, entity.getMajorCode())).map(subsystem -> {
                SelectTable table = new SelectTable();
                table.setLabel(subsystem.getSystemName());
                table.setValue(subsystem.getSystemCode());
                return table;
            }).collect(Collectors.toList());
        }else if(StrUtil.isBlank(majorCode) && CollectionUtils.isEmpty(majorIds)) {
            list = csMajorList.stream().map(subsystem -> {
                SelectTable table = new SelectTable();
                table.setLabel(subsystem.getSystemName());
                table.setValue(subsystem.getSystemCode());
                return table;
            }).collect(Collectors.toList());
        }
        if (CollectionUtil.isNotEmpty(majorIds)) {
            list = csMajorList.stream().filter(entity -> majorIds.contains(entity.getMajorId())).map(subsystem -> {
                SelectTable table = new SelectTable();
                table.setLabel(subsystem.getSystemName());
                table.setValue(subsystem.getSystemCode());
                return table;
            }).collect(Collectors.toList());
        }
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
        List<SelectTable> list = commonService.queryDevice(deviceDTO);
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
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = loginUser.getId();
        String roleCodes = loginUser.getRoleCodes();
        // 系统管理员不做权限过滤


        List<CsLine> lineList = lineService.getBaseMapper().selectList(new LambdaQueryWrapper<CsLine>().eq(CsLine::getDelFlag,0));

        Map<String, String> lineMap = lineList.stream().collect(Collectors.toMap(CsLine::getLineCode, CsLine::getLineName, (t1, t2) -> t2));

        List<CsUserStationModel> stationModelList = Collections.emptyList();
        // 根据个人管理的站点
        if (StrUtil.isNotBlank(roleCodes) && roleCodes.indexOf(ADMIN)>-1) {
            stationModelList = userStationService.queryAllStation();
        }else {
            stationModelList = sysBaseApi.getStationByUserId(userId);
        }


        Map<String, List<CsUserStationModel>> stationMap = stationModelList.stream().collect(Collectors.groupingBy(CsUserStationModel::getLineCode));

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
            List<CsUserStationModel> csStationList = stationMap.getOrDefault(lineCode, Collections.emptyList());

            List<SelectTable> lv2List = csStationList.stream().map(csStation -> {
                SelectTable selectTable = new SelectTable();
                selectTable.setValue(csStation.getStationCode());
                selectTable.setLabel(csStation.getStationName());
                selectTable.setLevel(2);
                selectTable.setId(csStation.getStationId());
                selectTable.setLineCode(lineCode);
                selectTable.setStationCode(csStation.getStationCode());

                List<CsStationPosition> stationPositionList = positionMap.getOrDefault(csStation.getStationCode(), Collections.emptyList());

                List<SelectTable> tableList = stationPositionList.stream().map(csStationPosition -> {
                    SelectTable tableV = new SelectTable();
                    tableV.setLabel(csStationPosition.getPositionName());
                    tableV.setValue(csStationPosition.getPositionCode());
                    tableV.setId(csStationPosition.getId());
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
                selectTable.setKey(csStation.getId());
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
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        String userId = loginUser.getId();
        String roleCodes = loginUser.getRoleCodes();
        List<CsUserMajorModel> majorModelList = Collections.emptyList();

        List<CsUserSubsystemModel> subsystemModelList = Collections.emptyList();
        // 系统管理员不做权限过滤
        if (StrUtil.isNotBlank(roleCodes) && roleCodes.indexOf(ADMIN)>-1) {
            majorModelList = csUserMajorService.queryAllMojor();

            subsystemModelList = csUserSubsystemService.queryAllSubsystem();
        }else {
            majorModelList = csUserMajorService.getMajorByUserId(userId);

            subsystemModelList =  csUserSubsystemService.getSubsystemByUserId(userId);
        }

        Map<String, List<CsUserSubsystemModel>> map = subsystemModelList.stream().collect(Collectors.groupingBy(CsUserSubsystemModel::getMajorCode));

        List<SelectTable> tableList = majorModelList.stream().map(csMajor -> {
            SelectTable table = new SelectTable();
            table.setLabel(csMajor.getMajorName());
            table.setValue(csMajor.getMajorCode());
            table.setKey(csMajor.getMajorId());
            List<CsUserSubsystemModel> csList = map.getOrDefault(csMajor.getMajorCode(), Collections.emptyList());
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

    @GetMapping("/line/queryLines")
    @ApiOperation("查询线路数据")
    public Result<List<CsLine>> queryLines() {
        LambdaQueryWrapper<CsLine> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CsLine::getDelFlag, 0);
        List<CsLine> csLines = lineService.getBaseMapper().selectList(wrapper);
        return Result.OK(csLines);
    }

    @GetMapping("/station/queryStationByLineCode")
    @ApiOperation("根据线路编码查询站所数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "lineCode", value = "线路编码", required = true, paramType = "query")
    })
    public Result<List<CsStation>> queryStationByLineCode(@RequestParam(value = "lineCode") String lineCode) {
        LambdaQueryWrapper<CsStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CsStation::getLineCode, lineCode).eq(CsStation::getDelFlag, 0);
        List<CsStation> stationList = stationService.getBaseMapper().selectList(wrapper);
        return Result.OK(stationList);
    }



    @GetMapping("/position/queryPositionByStationCode")
    @ApiOperation("根据站所编码查询站所数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stationCode", value = "站所编码", required = true, paramType = "query")
    })
    public Result<List<CsStationPosition>> queryPositionByStationCode(@RequestParam(value = "stationCode") String stationCode) {
        LambdaQueryWrapper<CsStationPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CsStationPosition::getStaionCode, stationCode);
        List<CsStationPosition> stationList = stationPositionService.getBaseMapper().selectList(wrapper);
        return Result.OK(stationList);
    }

    @GetMapping("/sysuser/queryDepartUserTree")
    @ApiOperation("根据机构人员树")
    public Result<List<SelectTable>> queryDepartUserTree() {
        List<SelectTable> tables = commonService.queryDepartUserTree(null, null);
        return Result.OK(tables);
    }

    /**
     * 位置列表查询
     * @param
     * @return
     */
    @ApiOperation(value="位置列表查询", notes="站所列表查询")
    @GetMapping(value = "/stationPosition/selectList")
    public Result<?> selectList(@RequestParam(name = "stationCode",required = false) String stationCode) {
        LambdaQueryWrapper<CsStationPosition> queryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(stationCode)) {
            queryWrapper.eq(CsStationPosition::getStaionCode, stationCode);
        }
        List<CsStationPosition> list = csStationPositionService.list(queryWrapper.eq(CsStationPosition::getDelFlag, CommonConstant.DEL_FLAG_0));
        return Result.OK(list);
    }

    /**
     * 根据站点位置列表查询
     * @param
     * @return
     */
    @ApiOperation(value="位置列表查询", notes="站所列表查询")
    @GetMapping(value = "/workArea/selectWorkAreaList")
    public Result<?> selectWorkAreaList(@RequestParam(name = "stationCode",required = false) String stationCode) {
        LambdaQueryWrapper<WorkArea> queryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(stationCode)) {
            queryWrapper.eq(WorkArea::getPosition, stationCode);
        }
        List<WorkArea> list = workAreaService.list(queryWrapper.eq(WorkArea::getDelFlag, CommonConstant.DEL_FLAG_0));
        return Result.OK(list);
    }



    @GetMapping("/system/queryDepartTree")
    @ApiOperation("查询用户拥有部门权限树")
    public Result<List<CsUserDepartModel>> queryDepartTree() {
        List<CsUserDepartModel> list = csUserDepartService.queryDepartTree();
        return Result.OK(list);
    }
}
