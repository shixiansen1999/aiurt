package com.aiurt.modules.personnelportrait.controller;

import com.aiurt.modules.fault.dto.FaultDeviceDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.personnelportrait.dto.*;
import com.aiurt.modules.personnelportrait.service.PersonnelPortraitService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * @author
 * @description 统计报表人员画像
 */
@Slf4j
@Api(tags = "统计报表人员画像")
@RestController
@RequestMapping("/personnel")
public class PersonnelPortraitController {

    @Autowired
    private PersonnelPortraitService personnelPortraitService;

    /**
     * 人员画像
     */
    @ApiOperation(value = "人员画像", notes = "人员画像")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = PersonnelPortraitResDTO.class)
    })
    @GetMapping(value = "/portrait")
    public Result<PersonnelPortraitResDTO> portrait(@RequestParam @ApiParam(name = "orgCode", value = "部门编号") String orgCode) {
        PersonnelPortraitResDTO personnelPortrait = personnelPortraitService.portrait(orgCode);
        return Result.OK(personnelPortrait);
    }

    /**
     * 用户详细信息
     */
    @ApiOperation(value = "用户详细信息", notes = "用户详细信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = UserDetailResDTO.class)
    })
    @GetMapping(value = "/detail")
    public Result<UserDetailResDTO> userDetail(@RequestParam @ApiParam(name = "userId", value = "用户ID") String userId) {
        UserDetailResDTO userDetail = personnelPortraitService.userDetail(userId);
        return Result.OK(userDetail);
    }

    /**
     * 人员综合表现
     */
    @ApiOperation(value = "人员综合表现", notes = "人员综合表现")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = RadarResDTO.class)
    })
    @GetMapping(value = "/radar")
    public Result<RadarResDTO> radarMap(@RequestParam @ApiParam(name = "userId", value = "用户ID") String userId) {
        RadarResDTO radar = personnelPortraitService.radarMap(userId);
        return Result.OK(radar);
    }

    /**
     * 综合表现评分
     */
    @ApiOperation(value = "综合表现评分", notes = "综合表现评分")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = DashboardResDTO.class)
    })
    @GetMapping(value = "/dashboard")
    public Result<DashboardResDTO> dashboard(@RequestParam @ApiParam(name = "userId", value = "用户ID") String userId) {
        DashboardResDTO dashboard = personnelPortraitService.dashboard(userId);
        return Result.OK(dashboard);
    }

    /**
     * 培训经历
     */
    @ApiOperation(value = "培训经历", notes = "培训经历")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = List.class)
    })
    @GetMapping(value = "/experience")
    public Result<List<ExperienceResDTO>> experience(@RequestParam @ApiParam(name = "userId", value = "用户ID") String userId) {
        List<ExperienceResDTO> experiences = personnelPortraitService.experience(userId);
        return Result.OK(experiences);
    }

    /**
     * 任务次数
     */
    @ApiOperation(value = "任务次数", notes = "任务次数")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = WaveResDTO.class)
    })
    @GetMapping(value = "/wave")
    public Result<List<WaveResDTO>> waveRose(@RequestParam @ApiParam(name = "userId", value = "用户ID") String userId) {
        List<WaveResDTO> waveRes = personnelPortraitService.waveRose(userId);
        return Result.OK(waveRes);
    }

    /**
     * 历史维修记录
     */
    @ApiOperation(value = "历史维修记录", notes = "历史维修记录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = HistoryResDTO.class)
    })
    @GetMapping(value = "/history")
    public Result<List<HistoryResDTO>> history(@RequestParam @ApiParam(name = "userId", value = "用户ID") String userId) {
        List<HistoryResDTO> history = personnelPortraitService.history(userId);
        return Result.OK(history);
    }

    /**
     * 历史维修记录-设备故障信息列表
     */
    @ApiOperation(value = "历史维修记录-设备故障信息列表", notes = "历史维修记录-设备故障信息列表")
    @GetMapping(value = "/device/info")
    public Result<List<FaultDeviceDTO>> deviceInfo(@RequestParam @ApiParam(name = "userId", value = "用户ID") String userId) {
        List<FaultDeviceDTO> history = personnelPortraitService.deviceInfo(userId);
        return Result.OK(history);
    }

    /**
     * 历史维修记录列表(更多)
     */
    @ApiOperation(value = "历史维修记录列表(更多)", notes = "历史维修记录列表(更多)")
    @GetMapping(value = "/history/record")
    public Result<IPage<Fault>> historyRecord(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                              @RequestParam @ApiParam(name = "userId", value = "用户ID") String userId,
                                              HttpServletRequest request) {
        IPage<Fault> pageList = personnelPortraitService.historyRecord(pageNo, pageSize, userId, request);
        return Result.OK(pageList);
    }
}
