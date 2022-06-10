package com.aiurt.boot.modules.schedule.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.common.util.DateUtils;
import com.aiurt.boot.modules.schedule.entity.Schedule;
import com.aiurt.boot.modules.schedule.entity.ScheduleItem;
import com.aiurt.boot.modules.schedule.entity.ScheduleRecord;
import com.aiurt.boot.modules.schedule.mapper.ScheduleMapper;
import com.aiurt.boot.modules.schedule.model.ScheduleRecordModel;
import com.aiurt.boot.modules.schedule.model.ScheduleUser;
import com.aiurt.boot.modules.schedule.service.IScheduleItemService;
import com.aiurt.boot.modules.schedule.service.IScheduleRecordService;
import com.aiurt.boot.modules.schedule.service.IScheduleService;
import com.aiurt.boot.modules.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: schedule
 * @Author: swsc
 * @Date: 2021-09-23
 * @Version: V1.0
 */
@Slf4j
@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements IScheduleService {

    @Autowired
    private IScheduleRecordService recordService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private IScheduleItemService scheduleItemService;

    @Override
    public IPage<Schedule> getList(Schedule schedule, Page<Schedule> temp) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String orgCode = sysUser.getOrgCode();
        IPage page = new Page();
        page = temp;
        List<Schedule> scheduleList = new ArrayList<>();

        /**
         * 1、获取查询范围及当月有多少天
         */
        Date date = schedule.getDate();
        if (date == null) {
            date = DateUtils.parseDate(DateUtils.formatDate(), "yyyy-MM");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int maximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        /**
         * 2、从record表中获取有多少人在时间范围类安排了工作,从sys_user查询本班组的成员,根据班组查询
         */
        //List<ScheduleUser> scheduleUserList = recordService.getScheduleUserByDate(DateUtils.format(date, "yyyy-MM"),schedule.getUserName());
        //List<ScheduleUser> scheduleUserList = recordService.getScheduleUserByDateAndOrgCode(DateUtils.format(date, "yyyy-MM"),schedule.getUserName(),orgCode);
        List<ScheduleUser> scheduleUserList = recordService.getScheduleUserByDateAndOrgCodeAndOrgId(DateUtils.format(date, "yyyy-MM"), schedule.getUserName(), orgCode, schedule.getOrgId());
        /**
         * 3、获取记录数据
         */
        long start=System.currentTimeMillis();
        if (scheduleUserList != null && scheduleUserList.size() > 0) {
            for (ScheduleUser scheduleUser : scheduleUserList) {
                Schedule userSchedule = new Schedule();
                userSchedule.setUserId(scheduleUser.getUserId());
                userSchedule.setUserName(scheduleUser.getUserName());
                userSchedule.setDate(date);
                List<ScheduleRecordModel> scheduleRecordList = new ArrayList<ScheduleRecordModel>(maximum);
                for (int index = 0; index < maximum; index++) {
                    scheduleRecordList.add(new ScheduleRecordModel());
                }
                /**
                 * a、查询所有记录
                 */
                List<ScheduleRecordModel> recordModelList = recordService.getRecordListByUserAndDate(scheduleUser.getUserId(), DateUtils.format(date, "yyyy-MM"));
                if (recordModelList != null && recordModelList.size() > 0) {
                    for (ScheduleRecordModel recordModel : recordModelList) {
                        calendar.setTime(recordModel.getDate());
                        int index = calendar.get(Calendar.DAY_OF_MONTH) - 1;
                        scheduleRecordList.set(index, recordModel);
                    }
                }
                userSchedule.setItem(scheduleRecordList);
                scheduleList.add(userSchedule);
            }
        }
        long end=System.currentTimeMillis();
        log.info("time:{}",end-start);
        page.setRecords(scheduleList);
        return page;
    }


    @Override
    @Transactional(rollbackOn = Exception.class)
    public void importScheduleExcel(List<Map<Integer, String>> scheduleDate, HttpServletRequest request) {
        //从表头获取时间
        String date = scheduleDate.get(0).get(0).split("至")[0];
        SimpleDateFormat format = new SimpleDateFormat("yyyy年M月d日");
        Date startTime;
        try {
            startTime = format.parse(date);
        } catch (ParseException e) {
           throw new RuntimeException("第1行日期格式不正确，应为yyyy年MM月dd日，请检查。");
        }
        //获得所有已经排班的用户
        List<ScheduleUser> scheduleUserList = recordService.getScheduleUserByDate(DateUtils.format(startTime,"yyyy-MM"),null);
        List<String> userIds = scheduleUserList.stream().map(ScheduleUser::getUserId).collect(Collectors.toList());
        for (int i = 2; i < scheduleDate.size(); i++) {
            //获取一条排班记录
            Map<Integer, String> scheduleMap = scheduleDate.get(i);
            //构造ScheduleRecord对象
            String userId = userService.getUserIdByUsername(scheduleMap.get(2));
            if (StringUtils.isBlank(userId)){
                throw new RuntimeException("第" + (i + 1) + "行存在系统中未包含的工作证编号，请检查。");
            }
            //判断时间内是否有人已经排班
            if (userIds.contains(userId)){
                throw new RuntimeException("第" + (i + 1) + "行存用户此段时间内已排班，请检查。");
            }
            //生成排班月份中每天的排班记录
            //获取排班月份的第一天和最后一天
            Calendar start = Calendar.getInstance();
            //从表头获取时间
            //calendar的月份从0开始，1月是0
            start.setTime(startTime);

            Date end = DateUtil.endOfMonth(start.getTime());
            while (!start.getTime().after(end)) {
                ScheduleItem scheduleItem = scheduleItemService.getOne(new LambdaQueryWrapper<ScheduleItem>()
                        .eq(ScheduleItem::getName, scheduleMap.get(DateUtil.dayOfMonth(start.getTime())+3)).eq(ScheduleItem::getDelFlag,0));
                if (ObjectUtil.isEmpty(scheduleItem)){
                    throw new RuntimeException("第" + (i + 1) + "行存在系统中未包含的班次名称，请检查。");
                }
                ScheduleRecord record = ScheduleRecord.builder()
                        .scheduleId(null)
                        .userId(userId)
                        .date(start.getTime())
                        .itemId(scheduleItem.getId())
                        .itemName(scheduleItem.getName())
                        .startTime(scheduleItem.getStartTime())
                        .endTime(scheduleItem.getEndTime())
                        .color(scheduleItem.getColor())
                        .delFlag(0)
                        .build();
                recordService.save(record);
                start.add(Calendar.DAY_OF_YEAR, 1);
            }

        }

    }

}
