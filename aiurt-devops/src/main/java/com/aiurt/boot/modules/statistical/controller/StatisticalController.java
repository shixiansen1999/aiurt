package com.aiurt.boot.modules.statistical.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.modules.device.entity.Device;
import com.aiurt.boot.modules.device.service.IDeviceService;
import com.aiurt.boot.modules.manage.entity.Line;
import com.aiurt.boot.modules.manage.entity.SpecialSituation;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.aiurt.boot.modules.manage.mapper.SubsystemMapper;
import com.aiurt.boot.modules.manage.model.StationWarning;
import com.aiurt.boot.modules.manage.service.ILineService;
import com.aiurt.boot.modules.manage.service.ISpecialSituationService;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.patrol.param.PatrolTaskDetailParam;
import com.aiurt.boot.modules.patrol.param.UrlParam;
import com.aiurt.boot.modules.patrol.service.IPatrolTaskReportService;
import com.aiurt.boot.modules.patrol.service.IPatrolTaskService;
import com.aiurt.boot.modules.patrol.vo.PatrolTaskVO;
import com.aiurt.boot.modules.repairManage.service.IRepairTaskService;
import com.aiurt.boot.modules.repairManage.vo.ReTaskDetailVO;
import com.aiurt.boot.modules.statistical.service.StatisticalService;
import com.aiurt.boot.modules.statistical.vo.*;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.util.oConvertUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartScheduleVo;
import org.jeecg.common.system.vo.SysDepartTreeModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author qian
 * @version 1.0
 * @date 2021/11/20 11:21
 */
@Slf4j
@Api(tags = "统计分析")
@RestController
@RequestMapping("/statistical")
@RequiredArgsConstructor
public class StatisticalController {

    @Autowired
    private final StatisticalService statisticalService;
    @Autowired
    private final IStationService stationService;
    @Autowired
    private final ISpecialSituationService specialSituationService;
    //    @Autowired
//    private final ISysDepartService sysDepartService;
//    @Autowired
//    private final ISysUserService sysUserService;
    @Autowired
    private final IRepairTaskService repairTaskService;
    @Autowired
    private final IPatrolTaskService patrolTaskService;
    @Autowired
    private final IPatrolTaskReportService patrolTaskReportService;
    @Autowired
    private final IDeviceService deviceService;
    @Resource
    private final SubsystemMapper subsystemMapper;

    @ApiOperation(value = "维修人员数据统计", notes = "维修人员数据统计")
    @PostMapping("/getCount")
    public Result<List<StatisticsResultVO>> getCount(@RequestBody @Validated StatisticsVO statisticsVO) {
        return statisticalService.getCount(statisticsVO);
    }

    /**
     * @param lineCode
     * @return
     * @Description: 各班组巡检数统计
     * @author: renanfeng
     * date: 2022/1/26
     */
    @ApiOperation(value = "各班组巡检数统计", notes = "各班组巡检数统计")
    @PostMapping("/getPatrolCountGroupByOrg")
    public Result<List<StatisticsPatrolVO>> getPatrolCountGroupByOrg(String lineCode) {
        return statisticalService.getPatrolCountGroupByOrg(lineCode);
    }

    /**
     * @param lineCode
     * @return
     * @Description: 各班组检修数统计
     * @author: renanfeng
     * date: 2022/1/26
     */
    @ApiOperation(value = "各班组检修数统计", notes = "各班组检修数统计")
    @PostMapping("/getRepairCountGroupByOrg")
    public Result<List<StatisticsRepairVO>> getRepairCountGroupByOrg(String lineCode) {
        return statisticalService.getRepairCountGroupByOrg(lineCode);
    }

    /**
     * @param lineCode
     * @return
     * @Description: 故障报修方式对比
     * @author: renanfeng
     * date: 2022/1/26
     */
    @ApiOperation(value = "故障报修方式对比", notes = "故障报修方式对比")
    @PostMapping("/getFaultCountGroupByWay")
    public Result<List<StatisticsFaultWayVO>> getFaultCountGroupByWay(String lineCode) {
        return statisticalService.getFaultCountGroupByWay(lineCode);
    }

    /**
     * @param lineCode
     * @return
     * @Description: 故障完成情况对比
     * @author: renanfeng
     * date: 2022/1/26
     */
    @ApiOperation(value = "故障完成情况对比", notes = "故障完成情况对比")
    @PostMapping("/getFaultCountGroupByStatus")
    public Result<List<StatisticsFaultStatusVO>> getFaultCountGroupByStatus(String lineCode) {
        return statisticalService.getFaultCountGroupByStatus(lineCode);
    }

    /**
     * @param lineCode
     * @return
     * @Description: 故障一级、二级、三级统计
     * @author: renanfeng
     * date: 2022/1/26
     */
    @ApiOperation(value = "故障一级、二级、三级统计", notes = "故障一级、二级、三级统计")
    @PostMapping("/getFaultGroupByLevel")
    public Result<Map<String, List<StatisticsFaultLevelVO>>> getFaultGroupByLevel(String lineCode) {
        return statisticalService.getFaultGroupByLevel(lineCode);
    }

    /**
     * @param lineCode
     * @return
     * @Description: 年度维修情况统计
     * @author: renanfeng
     * date: 2022/1/26
     */
    @ApiOperation(value = "年度维修情况统计", notes = "年度维修情况统计 ")
    @PostMapping("/getFaultCountGroupByMonth")
    public Result<List<StatisticsFaultMonthVO>> getFaultCountGroupByMonth(String lineCode) {
        return statisticalService.getFaultCountGroupByMonth(lineCode);
    }

    /**
     * @param lineCode
     * @return
     * @Description: 各子系统故障数据统计
     * @author: renanfeng
     * date: 2022/1/26
     */
    @ApiOperation(value = "各子系统故障数据统计", notes = "各子系统故障数据统计 ")
    @PostMapping("/getFaultCountGroupBySystem")
    public Result<List<StatisticsFaultSystemVO>> getFaultCountGroupBySystem(String lineCode, Integer month) {
        return statisticalService.getFaultCountGroupBySystem(lineCode, month);
    }


    /**
     * @Description: 维修数据统计-故障信息统计
     * @author: niuzeyu
     * @date: 2022/1/26 9:58
     * @Return: com.swsc.copsms.common.api.vo.Result
     */
    @AutoLog(value = "维修数据统计-故障信息统计")
    @GetMapping(value = "/getWeeklyFaultStatistics")
    public Result getWeeklyFaultStatistics(@RequestParam(value = "lineCode", required = false) String lineCode,
                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                           @RequestParam(name = "pageSize", defaultValue = "5000") Integer pageSize,
                                           @RequestParam(value = "week", required = false) boolean week) {
        DateTime now = DateTime.now();
        if (week) {
            now = DateUtil.lastWeek();
        }
        DateTime startTime = DateUtil.beginOfWeek(now);
        DateTime endTime = DateUtil.endOfWeek(now);
        PageVo page = statisticalService.getWeeklyFaultStatistics(lineCode, pageNo, pageSize, startTime, endTime);
        return Result.ok(page);
    }


    @ApiOperation("本周检修统计")
    @GetMapping("/getWeeklyRepairStatistic")
    public Result<PageVo> getWeeklyRepairStatistic(@RequestParam(value = "lineCode", required = false) String lineCode,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "5000") Integer pageSize,
                                                   @RequestParam(value = "week", required = false) boolean week) {
        DateTime now = DateUtil.date();
        if (week) {
            now = DateUtil.lastWeek();
        }
        DateTime startTime = DateUtil.beginOfWeek(now);
        DateTime endTime = DateUtil.endOfWeek(now);
        PageVo vo = statisticalService.getRepairStatisticByLineCodeAndTime(lineCode, pageNo, pageSize, startTime, endTime);
        return Result.ok(vo);
    }

    /**
     * @Description: 检修数据统计
     * @author: niuzeyu
     * @date: 2022/1/24 19:27
     * @Return: com.swsc.copsms.common.api.vo.Result<com.swsc.copsms.modules.statistical.vo.RepairPageVo>
     */
    @ApiOperation("检修数据统计")
    @GetMapping("/getRepairStatistic")
    public Result<PageVo> getRepairStatistic(@RequestParam(value = "lineCode", required = false) String lineCode,
                                             @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                             @RequestParam(name = "pageSize", defaultValue = "5000") Integer pageSize,
                                             @RequestParam(value = "week", required = false) boolean week) {
        DateTime now = DateUtil.date();
        if (week) {
            now = DateUtil.lastWeek();
        }
        PageVo vo = statisticalService.getRepairStatistic(lineCode, pageNo, pageSize, now);
        return Result.ok(vo);
    }

    /**
     * @Description: 班组信息展示
     * @author: niuzeyu
     * @date: 2022/1/24 19:29
     * @Return: com.swsc.copsms.common.api.vo.Result<com.swsc.copsms.modules.system.vo.SysDepartSchedulePageVo>
     */
    @ApiOperation(value = "班组信息展示")
    @GetMapping("/sysDepartScheduleStatistics")
    public Result<SysDepartScheduleVo> queryDepartScheduleByOrgId(@RequestParam(value = "lineCode", required = false) String lineCode,
                                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                  @RequestParam(name = "pageSize", defaultValue = "5000") Integer pageSize,
                                                                  @RequestParam(value = "week", required = false) boolean week) {
        DateTime now = DateUtil.date();
        if (week) {
            now = DateUtil.lastWeek();
        }
        SysDepartScheduleVo vo = statisticalService.getSysDepartSchedulePageVo(lineCode, pageNo, pageSize, now);
        return Result.ok(vo);
    }

    /**
     * @Description: 本周巡视统计
     * @author: niuzeyu
     * @date: 2022/1/24 17:43
     * @Return: com.swsc.copsms.common.api.vo.Result<com.swsc.copsms.modules.patrol.vo.PageVo>
     */
    @AutoLog(value = "本周巡视统计")
    @ApiOperation(value = "本周巡视统计", notes = "本周巡视统计")
    @GetMapping(value = "/weeklyPatrolStatistics")
    public Result<PageVo> weeklyPatrolStatistics(@RequestParam(value = "lineCode", required = false) String lineCode,
                                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "pageSize", defaultValue = "5000") Integer pageSize,
                                                 @RequestParam(value = "week", required = false) boolean week) {
        DateTime now = DateUtil.date();
        if (week) {
            now = DateUtil.lastWeek();
        }
        PageVo page = new PageVo();
        List<Integer> lineIds = stationService.getIdsByLineCode(lineCode);
        if (ObjectUtil.isEmpty(lineIds)) {
            return Result.ok(page);
        }
        page = statisticalService.getWeeklyPatrolStatisticPageVo(pageNo, pageSize, lineIds, lineCode, now);

        return Result.ok(page);
    }

    /**
     * @Description: 巡视数据统计
     * @author: niuzeyu
     * @date: 2022/1/24 17:43
     * @Return: com.swsc.copsms.common.api.vo.Result<com.swsc.copsms.modules.patrol.vo.PageVo>
     */
    @AutoLog("巡视数据统计")
    @ApiOperation(value = "巡视数据统计", notes = "巡视数据统计")
    @GetMapping(value = "/patrolStatistics")
    public Result<PageVo> patrolStatistics(@RequestParam(value = "lineCode", required = false) String lineCode,
                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                           @RequestParam(name = "pageSize", defaultValue = "5000") Integer pageSize,
                                           @RequestParam(value = "week", required = false) boolean week) {
        DateTime now = DateUtil.date();
        if (week) {
            now = DateUtil.lastWeek();
        }
        PageVo page = new PageVo();

        page = statisticalService.getPatrolStatisticPageVo(pageNo, pageSize, lineCode, now);

        return Result.ok(page);
    }

    /**
     * @param lineCode
     * @return
     * @Description: 故障数据统计
     * @author: renanfeng
     * date: 2022/1/27
     */
    @ApiOperation(value = "故障数据统计", notes = "故障数据统计 ")
    @PostMapping("/getFaultCountAndDetails")
    public Result<StatisticsFaultCountVO> getFaultCount(String lineCode) {
        return statisticalService.getFaultCountAndDetails(lineCode);
    }

    @ApiOperation(value = "综合看板特情信息", notes = "综合看板特情信息 ")
    @RequestMapping(value = "/StatisticsSituation", method = RequestMethod.GET)
    public Result<List<SpecialSituation>> statisticsSituation() {
        DateTime now = DateUtil.date();
        DateTime startTime = DateUtil.beginOfMonth(now);
        DateTime endTime = DateUtil.endOfWeek(now);
        List<SpecialSituation> specialSituations = new ArrayList<>();
        if (oConvertUtils.isNotEmpty(startTime) && oConvertUtils.isNotEmpty(endTime)) {
            specialSituations = specialSituationService.list(new QueryWrapper<SpecialSituation>()
                    .gt("end_time", now)
                    .between("publish_time", startTime, endTime)
                    .orderByDesc("create_time")
                    .last("limit 0,10")
            );
        } else {
            specialSituations = specialSituationService.list(new QueryWrapper<SpecialSituation>()
                    .orderByDesc("create_time").last("limit 0,10")
            );
        }
        return Result.ok(specialSituations);
    }


    //--------------------------公共接口开始-----------------------------
    @Autowired
    private ILineService lineService;


    @ApiOperation(value = "查询线路接口", notes = "查询线路接口 ")
    @GetMapping("lineSelect")
    public Result<List<Line>> lineSelect() {
        Result<List<Line>> result = new Result<List<Line>>();
        List<Line> lineList = lineService.list(new LambdaQueryWrapper<Line>().eq(Line::getDelFlag, 0));
        result.setResult(lineList);
        return result;
    }

    @ApiOperation(value = "查询班组接口", notes = "查询班组接口 ")
    @GetMapping("orgSelect")
    public Result<List<SysDepartTreeModel>> queryTreeList() {
        Result<List<SysDepartTreeModel>> result = new Result<>();
        try {
            // todo 后期修改
            List<SysDepartTreeModel> list = new ArrayList<>();
//            List<SysDepartTreeModel> list = sysDepartService.queryTreeList();
            result.setResult(list);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    @AutoLog("获取站点信息")
    @ApiOperation(value = "获取站点信息", notes = "获取站点信息")
    @GetMapping(value = "/getStations")
    public Result<List<Station>> getStations() {
        Result<List<Station>> result = new Result<List<Station>>();
        List<Station> stations = stationService.getStationsInOrdered();
        if (ObjectUtil.isNotEmpty(stations)) {
            Station yongchun = stations.stream().filter(item -> item.getStationCode().equals("01101")).findFirst().get();
            Station xihu = stations.stream().filter(item -> item.getStationCode().equals("02100")).findFirst().get();
            stations.add(yongchun);
            stations.add(xihu);
            result.setSuccess(true);
            result.setResult(stations);
            return result;
        }
        return Result.error("未获取到站点信息");
    }
    //--------------------------公共接口结束-----------------------------

    /**
     * 通过stationName查询
     *
     * @param stationName
     * @return
     */
    @AutoLog(value = "站点信息-通过stationName查询")
    @ApiOperation(value = "站点信息-通过stationName查询", notes = "站点信息-通过stationName查询")
    @GetMapping(value = "/queryByStationName")
    public Result<StationVo> queryByStationName(@RequestParam(name = "stationName", required = true) String stationName) {
        Result<StationVo> result = new Result<StationVo>();
        Station station = stationService.getOne(new LambdaQueryWrapper<Station>().eq(Station::getStationName, stationName));
        if (ObjectUtil.isEmpty(station)) {
            result.onnull("未找到对应实体");
        } else {
            StationVo stationVo = new StationVo();
            BeanUtils.copyProperties(station, stationVo);
            // todo 后期修改
//            SysDepart depart = sysDepartService.getOne(new LambdaQueryWrapper<SysDepart>().eq(SysDepart::getId, stationVo.getTeamId()));
//            stationVo.setTeamPhone(depart.getPhoneNum());
//            List<LoginUser> userList = sysUserService.list(new LambdaQueryWrapper<SysUser>().eq(SysUser::getOrgId, stationVo.getTeamId()));
            List<LoginUser> userList = new ArrayList<>();
            stationVo.setUserList(userList);

            result.setResult(stationVo);
            result.setSuccess(true);
        }
        return result;
    }

    @ApiOperation(value = "根据站点名称修改预警状态和开站状态", notes = "根据站点名称修改预警状态和开站状态")
    @PutMapping(value = "/editWarningStatus")
    public Result<Station> editWarningStatus(@RequestBody StationWarning stationWarning) {
        Result<Station> result = new Result<Station>();
        Station stationEntity = stationService.getOne(new LambdaQueryWrapper<Station>().eq(Station::getStationName, stationWarning.getStationName()));

        if (stationEntity == null) {
            result.onnull("未找到对应实体");
        } else {
            stationEntity.setWarningStatus(stationWarning.getStatus());
            stationEntity.setOpenStatus(stationWarning.getOpenStatus());
            boolean ok = stationService.updateById(stationEntity);

            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    @AutoLog(value = "站点信息-查询设备数据")
    @ApiOperation(value = "站点信息-查询设备数据", notes = "站点信息-查询设备数据")
    @GetMapping(value = "/getDevice")
    public Result<List<Device>> getDevice(@RequestParam(name = "stationName", required = true) String stationName,
                                          @RequestParam(name = "systemCode", required = false) String systemCode) {
        Result<List<Device>> result = new Result<List<Device>>();
        LambdaQueryWrapper<Station> wrapper = new LambdaQueryWrapper<Station>().eq(Station::getStationName, stationName);
        Station station = stationService.getOne(wrapper);
        List<Device> list = deviceService.queryDeviceByStationCodeAndSystemCode(station.getStationCode(), systemCode);
        result.setResult(list);
        return result;
    }

    /**
     * @Description: 班组信息展示-总人员
     * @author niuzeyu
     */
    @ApiOperation(value = "班组信息展示-总人员", notes = "总人员 ")
    @GetMapping(value = "/getSysUsers")
    public Result<List<LoginUser>> getSysUsers(@RequestParam(value = "lineCode", required = false) String lineCode,
                                             @RequestParam(value = "orgId", required = false) String orgId) {
        Result<List<LoginUser>> result = new Result<>();
        // todo 后期修改
//        List<LoginUser> userList = sysUserService.getSysUsersByLineCodeAndOrgId(lineCode, orgId);
        List<LoginUser> userList = new ArrayList<>();
        result.setResult(userList);
        return result;
    }

    /**
     * @Description: 班组信息展示-今日当班人
     * @author niuzeyu
     */
    @ApiOperation(value = "班组信息展示-今日当班人", notes = "今日当班人 ")
    @GetMapping(value = "/getDutyUsers")
    public Result<List<LoginUser>> getDutyUsers(@RequestParam(value = "lineCode", required = false) String lineCode,
                                              @RequestParam(value = "orgId", required = false) String orgId,
                                              @RequestParam(value = "week", required = false) boolean week) {
        Result<List<LoginUser>> result = new Result<>();
        DateTime now = DateTime.now();
        if (week) {
            now = DateUtil.lastWeek();
        }
        // todo 后期修改
//        List<LoginUser> userList = statisticalService.getDutyUsers(lineCode, orgId, now);
        List<LoginUser> userList = new ArrayList<>();
        result.setResult(userList);
        return result;
    }

    /**
     * @Description: 故障信息统计-未解决故障
     * @author: niuzeyu
     */
    @ApiOperation(value = "故障信息统计-未解决故障", notes = "未解决故障 ")
    @GetMapping(value = "/getUncompletedFault")
    public Result<FaultStatisticsModal> getUncompletedFault(@RequestParam(value = "lineCode", required = false) String lineCode,
                                                            @RequestParam(value = "week", required = false) boolean week) {
        Result result = new Result();
        DateTime now = DateTime.now();
        if (week) {
            now = DateUtil.lastWeek();
        }
        DateTime startTime = DateUtil.beginOfWeek(now);
        DateTime endTime = DateUtil.endOfWeek(now);
        List<FaultStatisticsModal> list = statisticalService.getUncompletedFault(lineCode, startTime, endTime);
        result.setResult(list);
        return result;
    }

    /**
     * @Description: 本周检修统计-检修完成
     * @author niuzeyu
     */
    @ApiOperation(value = "本周检修统计-检修完成", notes = "检修完成 ")
    @GetMapping(value = "/getWeeklyCompletedRepair")
    public Result<List<RepairTaskVo>> getWeeklyCompletedRepair(@RequestParam(value = "lineCode", required = false) String lineCode,
                                                               @RequestParam(value = "week", required = false) boolean week) {
        Result<List<RepairTaskVo>> result = new Result<>();
        DateTime now = DateTime.now();
        if (week) {
            now = DateUtil.lastWeek();
        }
        DateTime startTime = DateUtil.beginOfWeek(now);
        DateTime endTime = DateUtil.endOfWeek(now);
        List<RepairTaskVo> list = statisticalService.getCompletedRepair(lineCode, startTime, endTime);
        result.setResult(list);
        return result;
    }

    /**
     * @Description: 检修数据统计-检修完成
     * @author niuzeyu
     */
    @ApiOperation(value = "检修数据统计-本周漏检", notes = "本周漏检")
    @GetMapping(value = "/getWeeklyIgnoreRepair")
    public Result<List<RepairTaskVo>> getWeeklyIgnoreRepair(@RequestParam(value = "lineCode", required = false) String lineCode,
                                                            @RequestParam(value = "week", required = false) boolean week) {
        Result<List<RepairTaskVo>> result = new Result<>();
        DateTime now = DateTime.now();
        if (week) {
            now = DateUtil.lastWeek();
        }
        List<RepairTaskVo> list = statisticalService.getWeeklyIgnoreRepair(lineCode, now);
        result.setResult(list);
        return result;
    }

    /**
     * @Description: 检修数据统计-检修完成
     * @author niuzeyu
     */
    @ApiOperation(value = "检修数据统计-今日检修", notes = "今日检修数")
    @GetMapping(value = "/getTodayRepair")
    public Result<List<RepairTaskVo>> getTodayRepair(@RequestParam(value = "lineCode", required = false) String lineCode,
                                                     @RequestParam(value = "week", required = false) boolean week) {
        Result<List<RepairTaskVo>> result = new Result<>();
        DateTime now = DateTime.now();
        if (week) {
            now = DateUtil.lastWeek();
        }
        List<RepairTaskVo> list = statisticalService.getTodayRepair(lineCode, now);
        result.setResult(list);
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "检修数据统计-通过id查询检修信息")
    @ApiOperation(value = "检修数据统计-通过id查询检修信息", notes = "检修数据统计-通过id查询检修信息")
    @GetMapping(value = "/queryDetailById")
    public Result<ReTaskDetailVO> queryDetailById(@RequestParam(name = "id", required = true) String id) {
        return repairTaskService.queryDetailById(id);
    }

    /**
     * 综合看板-本周巡检统计
     */
    @ApiOperation(value = "本周巡视统计", notes = "本周巡视统计")
    @GetMapping(value = "/getWeeklyPatrolStatistics")
    public Result<PatrolStatisticVo> getWeeklyPatrolStatistics(@RequestParam(value = "lineCode", required = false) String lineCode,
                                                               @RequestParam(value = "week", required = false) boolean week) {
        DateTime now = DateUtil.date();
        if (week) {
            now = DateUtil.lastWeek();
        }
        PatrolStatisticVo vo = statisticalService.getWeeklyPatrolStatistic(lineCode, now);
        return Result.ok(vo);
    }

    @AutoLog(value = "本周巡视统计-详情")
    @ApiOperation(value = "本周巡视统计-详情", notes = "本周巡视统计-详情")
    @PostMapping(value = "/detail")
    public Result<?> detail(HttpServletRequest req, @RequestBody PatrolTaskDetailParam param) {
        return statisticalService.detail(req, param);
    }

    @AutoLog(value = "本周巡视统计-查看结果-获取url")
    @ApiOperation(value = "本周巡视统计-查看结果-获取url", notes = "本周巡视统计-查看结果-获取url")
    @GetMapping(value = "/getUrl")
    public Result<?> getUrl(HttpServletRequest req, UrlParam param) {
        return patrolTaskReportService.getUrl(req, param);
    }

    @AutoLog(value = "本周巡视统计-巡视完成")
    @ApiOperation(value = "本周巡视统计-巡视完成", notes = "本周巡视统计-巡视完成")
    @GetMapping(value = "/getCompletedPatrol")
    public Result<List<PatrolTaskVO>> getCompletedPatrol(@RequestParam(value = "lineCode", required = false) String lineCode,
                                                         @RequestParam(value = "week", required = false) boolean week,
                                                         @RequestParam(value = "departId", required = false) String departId) {
        DateTime now = DateUtil.date();
        if (week) {
            now = DateUtil.lastWeek();
        }
        List<PatrolTaskVO> vo = statisticalService.getCompletedPatrol(lineCode, departId, now);
        return Result.ok(vo);
    }

    @AutoLog(value = "本周巡视统计-今日巡视完成")
    @ApiOperation(value = "本周巡视统计-今日巡视完成", notes = "本周巡视统计-今日巡视完成")
    @GetMapping(value = "/getTodayCompletedPatrol")
    public Result<List<PatrolTaskVO>> getTodayCompletedPatrol(@RequestParam(value = "lineCode", required = false) String lineCode,
                                                              @RequestParam(value = "week", required = false) boolean week) {
        DateTime now = DateUtil.date();
        if (week) {
            now = DateUtil.lastWeek();
        }
        List<PatrolTaskVO> vo = statisticalService.getTodayCompletedPatrol(lineCode, now);
        return Result.ok(vo);
    }

    @AutoLog(value = "本周巡视统计-本周漏检")
    @ApiOperation(value = "本周巡视统计-本周漏检", notes = "本周巡视统计-本周漏检")
    @GetMapping(value = "/getIgnoredPatrol")
    public Result<List<PatrolTaskVO>> getIgnoredPatrol(@RequestParam(value = "lineCode", required = false) String lineCode,
                                                       @RequestParam(value = "week", required = false) boolean week,
                                                       @RequestParam(value = "departId", required = false) String departId) {
        DateTime now = DateUtil.date();
        if (week) {
            now = DateUtil.lastWeek();
        }
        List<PatrolTaskVO> vo = statisticalService.getIgnoredPatrol(lineCode, departId, now);
        return Result.ok(vo);
    }

    @AutoLog(value = "本周巡视统计-巡视异常")
    @ApiOperation(value = "本周巡视统计-巡视异常", notes = "本周巡视统计-巡视异常")
    @GetMapping(value = "/getExceptionPatrol")
    public Result<List<PatrolTaskVO>> getExceptionPatrol(@RequestParam(value = "lineCode", required = false) String lineCode,
                                                         @RequestParam(value = "week", required = false) boolean week) {
        DateTime now = DateUtil.date();
        if (week) {
            now = DateUtil.lastWeek();
        }
        List<PatrolTaskVO> vo = statisticalService.getExceptionPatrol(lineCode, now);
        return Result.ok(vo);
    }


    @AutoLog(value = "本周巡视统计-今日巡检")
    @ApiOperation(value = "本周巡视统计-今日巡检", notes = "本周巡视统计-今日巡检")
    @GetMapping(value = "/getTodayPatrol")
    public Result<List<PatrolTaskVO>> getTodayPatrol(@RequestParam(value = "lineCode", required = false) String lineCode,
                                                     @RequestParam(value = "week", required = false) boolean week) {
        DateTime now = DateUtil.date();
        if (week) {
            now = DateUtil.lastWeek();
        }
        List<PatrolTaskVO> vo = statisticalService.getTodayPatrol(lineCode, now);
        return Result.ok(vo);
    }

    @AutoLog(value = "本周巡视统计-计划巡视")
    @ApiOperation(value = "本周巡视统计-计划巡视", notes = "本周巡视统计-计划巡视")
    @GetMapping(value = "/getPlanPatrol")
    public Result<List<PatrolTaskVO>> getPlanPatrol(@RequestParam(value = "lineCode", required = false) String lineCode,
                                                    @RequestParam(value = "week", required = false) boolean week,
                                                    @RequestParam(value = "departId", required = false) String departId) {
        DateTime now = DateUtil.date();
        if (week) {
            now = DateUtil.lastWeek();
        }
        if (StringUtils.isNotBlank(departId)) {
            System.out.println();
        }
        List<PatrolTaskVO> vo = statisticalService.getPlanPatrol(lineCode, departId, now);
        return Result.ok(vo);
    }

    @AutoLog(value = "站点信息-系统")
    @ApiOperation(value = "站点信息-系统", notes = "站点信息-系统")
    @GetMapping(value = "/getSubSystem")
    public Result<List<Subsystem>> getSubSystem(@RequestParam(name = "stationName", required = true) String stationName) {
        List<Subsystem> list = subsystemMapper.getSubSystemByStationName(stationName);
        return Result.ok(list);
    }

    /**
     * @Description: 检修数据统计-本周计划
     * @author niuzeyu
     */
    @ApiOperation(value = "检修数据统计-本周计划", notes = "本周计划 ")
    @GetMapping(value = "/getWeeklyPlanRepair")
    public Result<List<RepairTaskVo>> getWeeklyPlanRepair(@RequestParam(value = "lineCode", required = false) String lineCode,
                                                          @RequestParam(value = "week", required = false) boolean week) {
        Result<List<RepairTaskVo>> result = new Result<>();
        DateTime now = DateTime.now();
        if (week) {
            now = DateUtil.lastWeek();
        }
        List<RepairTaskVo> list = statisticalService.getWeeklyPlanRepair(lineCode, now);
        result.setResult(list);
        return result;
    }

    /**
     * 故障数据统计-总故障数
     */
    @ApiOperation(value = "故障数据统计-总故障数", notes = "故障数据统计-总故障数")
    @GetMapping(value = "/getFaultTotalDetail")
    public Result<IPage> getFaultTotalDetail(@RequestParam(value = "lineCode", required = false) String lineCode,
                                             @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                             @RequestParam(name = "pageSize", defaultValue = "50") Integer pageSize) {
        Result result = new Result();
        DateTime now = DateTime.now();
        Date startTime = DateUtil.beginOfYear(now);
        Date endTime = DateUtil.endOfYear(now);
        IPage page = statisticalService.getFaultTotalDetail(lineCode, pageNo, pageSize, startTime, endTime);

        return result.ok(page);
    }

    /**
     * 故障数据统计-未修复故障
     */
    @ApiOperation(value = "故障数据统计-未修复故障", notes = "故障数据统计-未修复故障")
    @GetMapping(value = "/getFaultUnCompletedDetail")
    public Result getFaultTotalDetail(@RequestParam(value = "lineCode", required = false) String lineCode) {
        Result result = new Result();
        DateTime now = DateTime.now();
        DateTime startTime = DateUtil.beginOfYear(now);
        DateTime endTime = DateUtil.endOfYear(now);
        List<FaultStatisticsModal> list = statisticalService.getUncompletedFault(lineCode, startTime, endTime);
        return result.ok(list);
    }

    /**
     * 故障数据统计-未修复故障
     */
    @ApiOperation(value = "故障数据统计-本周修复", notes = "故障数据统计-本周修复")
    @GetMapping(value = "/getFaultCompletedDetail")
    public Result getFaultCompletedDetail(@RequestParam(value = "lineCode", required = false) String lineCode) {
        Result result = new Result();
        DateTime now = DateTime.now();
        DateTime startTime = DateUtil.beginOfWeek(now);
        DateTime endTime = DateUtil.endOfWeek(now);
        List<FaultStatisticsModal> list = statisticalService.getCompletedFault(lineCode, startTime, endTime);
        return result.ok(list);
    }

}
