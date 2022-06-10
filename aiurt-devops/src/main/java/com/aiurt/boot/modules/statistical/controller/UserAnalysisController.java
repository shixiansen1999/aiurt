package com.aiurt.boot.modules.statistical.controller;

import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.util.DateUtils;
import com.aiurt.boot.modules.device.service.IDeviceService;
import com.aiurt.boot.modules.schedule.entity.ScheduleHolidays;
import com.aiurt.boot.modules.schedule.model.DayScheduleModel;
import com.aiurt.boot.modules.schedule.model.ScheduleRecordModel;
import com.aiurt.boot.modules.schedule.service.IScheduleHolidaysService;
import com.aiurt.boot.modules.schedule.service.IScheduleRecordService;
import com.aiurt.boot.modules.schedule.vo.ScheduleCalendarVo;
import com.aiurt.boot.modules.statistical.vo.DepartDataVo;
import com.aiurt.boot.modules.statistical.vo.StaffDataVo;
import com.aiurt.boot.modules.statistical.vo.UserAnalysisDataVo;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.service.ISysDepartService;
import com.aiurt.boot.modules.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * @author stephen
 * @version 1.0
 * @date 2022/01/24
 */

@Slf4j
@Api(tags = "大屏-人员管理分析")
@RestController
@RequestMapping("/userAnalysis")
public class UserAnalysisController {

    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private ISysDepartService departService;
    @Autowired
    private IScheduleRecordService scheduleRecordService;
    @Autowired
    private IScheduleHolidaysService holidaysService;


    @ApiOperation(value = "总人数|今日值班人数|班组数量", notes = "总人数|今日值班人数|班组数量")
    @PostMapping("/getCount")
    public Result<UserAnalysisDataVo> getCount(@RequestParam(name = "lineId", required = false) String lineId) {
        Result result = new Result();
        Map map = new HashMap();
        map.put("lineId", lineId);
        Integer totalNum = userService.getTotalNum(map);
        Integer zhiBanNum = scheduleRecordService.getZhiBanNum(map);
        Integer banZuNum = departService.getBanZuNum(map);
        UserAnalysisDataVo userAnalysisDataVo = new UserAnalysisDataVo();
        userAnalysisDataVo.setNum1(totalNum);
        userAnalysisDataVo.setNum2(zhiBanNum);
        userAnalysisDataVo.setNum3(banZuNum);
        result.setResult(userAnalysisDataVo);
        return result;
    }

    @ApiOperation(value = "班组数据统计", notes = "班组数据统计")
    @PostMapping("/getDepartData")
    public Result getDepartData(@RequestParam(name = "lineId", required = false) String lineId) {
        Result result = new Result();
        Map map = new HashMap();
        map.put("lineId", lineId);
        List<DepartDataVo> list = departService.getDepartData(map);
        result.setResult(list);
        return result;
    }

    @ApiOperation(value = "工班日历", notes = "工班日历")
    @PostMapping(value = "/getUserSchedule")
    public Result<List<DayScheduleModel>> getUserSchedule(@RequestParam(name = "orgId", required = false) String orgId) {
        Result<List<DayScheduleModel>> result = new Result<List<DayScheduleModel>>();

        String date = DateUtils.format(new Date(), "yyyy-MM");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.parseDate(date, "yyyy-MM"));
        int maximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        List<DayScheduleModel> list = new ArrayList<>(maximum);
        for (int index = 0; index < maximum; index++) {
            DayScheduleModel model = new DayScheduleModel();
            model.setHolidays(new ArrayList<String>());
            model.setVoList(new ArrayList<ScheduleCalendarVo>());
            list.add(model);
        }
        List<ScheduleRecordModel> allRecordList = scheduleRecordService.getAllScheduleRecordsByMonth(date, orgId);
        if (allRecordList != null && allRecordList.size() > 0) {
            for (ScheduleRecordModel recordModel : allRecordList) {
                calendar.setTime(recordModel.getDate());
                int index = calendar.get(Calendar.DAY_OF_MONTH) - 1;
                ScheduleCalendarVo scheduleCalendarVo = new ScheduleCalendarVo();
                if ("白".equals(recordModel.getItemName())) {
                    scheduleCalendarVo.setType("success");
                } else if ("夜".equals(recordModel.getItemName())) {
                    scheduleCalendarVo.setType("warning");
                } else {
                    scheduleCalendarVo.setType("error");
                }
                scheduleCalendarVo.setColor(recordModel.getColor());
                scheduleCalendarVo.setContent(recordModel.getItemName() + "-" + recordModel.getUserName());
                list.get(index).getVoList().add(scheduleCalendarVo);
            }
        }
        List<ScheduleHolidays> holidaysList = holidaysService.getListByMonth(date);
        if (holidaysList != null && holidaysList.size() > 0) {
            for (ScheduleHolidays holidays : holidaysList) {
                calendar.setTime(holidays.getDate());
                int index = calendar.get(Calendar.DAY_OF_MONTH) - 1;
                list.get(index).getHolidays().add(holidays.getName());
            }
        }
        result.setResult(list);
        result.setSuccess(true);
        return result;
    }

    @ApiOperation(value = "维修人员数据统计", notes = "维修人员数据统计")
    @PostMapping("/getStaffData")
    public Result<StaffDataVo> getStaffData(@RequestParam(name = "lineId", required = false) String lineId) {
        Result result = new Result();
        Map map = new HashMap();
        map.put("lineId", lineId);
        List<StaffDataVo> list = userService.getStaffData(map);
        result.setResult(list);
        return result;
    }

    @ApiOperation(value = "根据日期查询值班人员", notes = "根据日期查询值班人员")
    @PostMapping("/getScheduleUserDataByDay")
    public Result<StaffDataVo> getScheduleUserDataByDay(@RequestParam(name = "day") String day, @RequestParam(name = "orgId", required = false) String orgId) {
        Result result = new Result();
        List<SysUser> userList = scheduleRecordService.getScheduleUserDataByDay(day, orgId);
        result.setResult(userList);
        return result;
    }


}
