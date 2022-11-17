package com.aiurt.modules.maplocation.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.weeklyplan.entity.BdLine;
import com.aiurt.boot.weeklyplan.entity.BdStation;
import com.aiurt.boot.weeklyplan.mapper.BdLineMapper;
import com.aiurt.boot.weeklyplan.mapper.BdStationMapper;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.maplocation.constant.BdMapConstant;
import com.aiurt.modules.maplocation.dto.*;
import com.aiurt.modules.maplocation.service.IBdMapListService;
import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.mapper.CsLineMapper;
import com.aiurt.modules.position.mapper.CsStationMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2021/4/3010:19
 */
@Api(tags = "地图位置管理")
@RestController
@RequestMapping("/maplocaltion/baMapList")
@Slf4j
public class BdMapController {
    @Autowired
    private IBdMapListService bdMapListService;
    @Autowired
    private CsStationMapper bdStationMapper;
    @Autowired
    private CsLineMapper bdLineMapper;

    /**
     * 查询人员的位置信息
     *
     * @param
     * @return
     */
    @AutoLog(value = " 查询人员的位置信息")
    @ApiOperation(value = " 查询人员的位置信息", notes = " 查询人员的位置信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = CurrentTeamPosition.class),
    })
    @GetMapping(value = "/queryById")
    public Result<?> queryPositionById(@RequestParam(name = "userInfoList", required = false) @ApiParam(value = "用户ID，查全部传all字符串，不按id查传空") String userInfoList,
                                       @RequestParam(name = "stationId", required = false) @ApiParam(value = "站点ID") String stationId,
                                       @RequestParam(value = "stateId", required = false) @ApiParam(value = "状态ID") String stateId,
                                       @RequestParam(value = "teamId", required = false) @ApiParam(value = "班组ID") String teamId) {
        List<CurrentTeamPosition> currentTeamPositions = bdMapListService.queryPositionById(teamId, userInfoList, stationId, stateId);
        return Result.OK(currentTeamPositions);
    }

    /**
     * 根据机构查询人员
     *
     * @param
     * @return
     */
    @AutoLog(value = " 根据机构查询人员")
    @ApiOperation(value = " 根据机构查询人员", notes = " 根据机构查询人员")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = UserInfo.class),
    })
    @GetMapping(value = "/getUserByTeamIdList")
    public Result<?> getUserByTeamIdList(@RequestParam(required = false) @ApiParam(name = "teamId", value = "班组id") String teamId) {
        List<UserInfo> userInfoList = bdMapListService.getUserByTeamIdList(teamId);
        if (userInfoList == null) {
            return Result.OK("未找到数据");
        }
        return Result.OK(userInfoList);
    }

    /**
     * 根据人员id查询人员信息
     *
     * @param
     * @return
     */
    @AutoLog(value = " 根据人员id查询人员信息")
    @ApiOperation(value = " 根据人员id查询人员信息", notes = " 根据人员id查询人员信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = UserInfo.class),
    })
    @GetMapping(value = "/getUserById")
    public Result<?> getUserById(@RequestParam @ApiParam(name = "id", required = true, value = "用户id") String id) {
        List<UserInfo> userInfoList = bdMapListService.getUserById(id);
        if (userInfoList == null) {
            return Result.error("未找到数据");
        }
        return Result.OK(userInfoList);
    }

    /**
     * 根据人员id查询附近设备分页
     *
     * @param
     * @return
     */
    @AutoLog(value = "根据人员id或者站点id查询附近设备分页")
    @ApiOperation(value = "根据人员id或者站点id查询附近设备分页", notes = "根据人员id或者站点id查询附近设备分页")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = EquipmentHistoryDTO.class),
    })
    @GetMapping(value = "/getEquipmentByUserId")
    public Result<?> getEquipmentByUserId(@RequestParam(required = false) @ApiParam(name = "id", value = "用户id") String id,
                                          @RequestParam(required = false) @ApiParam(name = "stationId", value = "站点id") String stationId,
                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<EquipmentHistoryDTO> pageList = new Page<EquipmentHistoryDTO>(pageNo, pageSize);
        pageList = bdMapListService.getEquipmentByUserId(id, stationId, pageList);
        return Result.OK(pageList);
    }

    /**
     * 查询3、4、8号线下面的所有站点的位置信息
     *
     * @param
     * @return
     */
    @AutoLog(value = "查询3、4、8号线下面的所有站点的位置信息")
    @ApiOperation(value = "查询3、4、8号线下面的所有站点的位置信息", notes = "查询3、4、8号线下面的所有站点的位置信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = StationDTO.class),
    })
    @GetMapping(value = "/getStationPosition")
    public Result<?> getStationPosition() {
        List<StationDTO> stationList = new ArrayList<>();
        LambdaQueryWrapper<CsLine> lineLambdaQueryWrapper = new LambdaQueryWrapper<CsLine>();
        lineLambdaQueryWrapper.in(CsLine::getLineName, Arrays.asList(BdMapConstant.THREELINE, BdMapConstant.FOURLINE, BdMapConstant.EIGHTLINE));
        lineLambdaQueryWrapper.eq(CsLine::getDelFlag,0);
        List<CsLine> bdLineList = bdLineMapper.selectList(lineLambdaQueryWrapper);
        if (CollUtil.isNotEmpty(bdLineList)) {
            List<String> lineList = bdLineList.stream().map(bdLine -> bdLine.getLineCode()).collect(Collectors.toList());
            LambdaQueryWrapper<CsStation> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(CsStation::getLineCode, lineList);
            List<CsStation> bdStationList = bdStationMapper.selectList(queryWrapper);
            if (CollUtil.isNotEmpty(bdStationList)) {
                bdStationList.forEach(bdStation -> {
                    if (ObjectUtil.isNotEmpty(bdStation) && bdStation.getLongitude() != null && bdStation.getLatitude() != null) {
                        StationDTO stationDTO = new StationDTO();
                        stationDTO.setStationId(bdStation.getId());
                        stationDTO.setPositionX(bdStation.getLongitude().toString());
                        stationDTO.setPositionY(bdStation.getLatitude().toString());
                        stationDTO.setStationName(bdStation.getStationName());
                        stationList.add(stationDTO);
                    }
                });
            }
        }
        return Result.OK(stationList);
    }

    /**
     * 根据机构获取机构下的人员状态
     *
     * @return
     */
    @ApiOperation(value = "根据机构获取机构下的人员状态", notes = "根据机构获取机构下的人员状态")
    @GetMapping(value = "/getUserStateByTeamId")
    public Result<List<AssignUserDTO>> getUserStateByTeamId(@ApiParam(required = true, value = "班组id", name = "teamId") String teamId,
                                                            @ApiParam(required = false, value = "人员id", name = "userId") String userId,
                                                            @ApiParam(required = false, value = "状态", name = "stateId") Integer stateId) {
        List<AssignUserDTO> list = bdMapListService.getUserStateByTeamId(teamId,userId,stateId);
        return Result.OK(list);
    }
    /**
     * 根据站点id查询站点信息
     *
     * @return
     */
    @ApiOperation(value = "根据站点id查询站点信息", notes = "根据站点id查询站点信息")
    @GetMapping(value = "/getStationById")
    public Result<List<StationDTO>> getStationById(@ApiParam(required = false, value = "线路Id", name = "lineCode") String lineCode,
                                                  @RequestParam @ApiParam(name = "stationId", required = false, value = "站点Id") String stationId) {
        List<StationDTO> stationList = new ArrayList<>();
        LambdaQueryWrapper<CsStation> queryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotEmpty(lineCode) && !StrUtil.isNotEmpty(stationId)){
            queryWrapper.eq(CsStation::getLineCode, lineCode);
        }
        if (StrUtil.isNotEmpty(stationId)){
            queryWrapper.eq(CsStation::getId, stationId);
        }
            queryWrapper.eq(CsStation::getDelFlag,0);
        List<CsStation> list = bdStationMapper.selectList(queryWrapper);
        if (CollUtil.isNotEmpty(list)) {
            list.forEach(bdStation -> {
                if (ObjectUtil.isNotEmpty(bdStation) && bdStation.getLongitude() != null && bdStation.getLatitude() != null) {
                    StationDTO stationDTO = new StationDTO();
                    stationDTO.setStationId(bdStation.getId());
                    stationDTO.setPositionX(bdStation.getLongitude().toString());
                    stationDTO.setPositionY(bdStation.getLatitude().toString());
                    stationDTO.setStationName(bdStation.getStationName());
                    stationList.add(stationDTO);
                }
            });
        }
        return Result.OK(stationList);
    }
    /**
     * 站点下拉框
     *
     * @return
     */
    @ApiOperation(value = "站点下拉框", notes = "站点下拉框")
    @GetMapping(value = "/getStation")
    public Result<List<LineDTO>> getStation() {
        List<LineDTO> list = bdMapListService.getStation();
        return Result.OK(list);
    }
    /**
     * 发送消息给对应的用户
     *
     * @param username
     * @param msg
     * @return
     */
    @ApiOperation(value = "发送消息给对应的用户", notes = "发送消息给对应的用户")
    @GetMapping(value = "/sendSysAnnouncement")
    public Result<?> sendSysAnnouncement(@ApiParam(required = true, value = "用户id", name = "username") String username,
                                         @ApiParam(required = true, value = "消息", name = "msg") String msg) {
        bdMapListService.sendSysAnnouncement(username, msg);
        return Result.OK();
    }

}
