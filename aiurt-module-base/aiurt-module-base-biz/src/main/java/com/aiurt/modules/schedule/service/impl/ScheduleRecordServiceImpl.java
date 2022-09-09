package com.aiurt.modules.schedule.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;

import com.aiurt.modules.schedule.dto.ScheduleRecordDTO;
import com.aiurt.modules.schedule.dto.SysUserScheduleDTO;
import com.aiurt.modules.schedule.mapper.ScheduleRecordMapper;
;
import com.aiurt.modules.schedule.service.IScheduleRecordService;
import com.aiurt.modules.schedule.entity.ScheduleRecord;
import com.aiurt.modules.schedule.model.ScheduleRecordModel;
import com.aiurt.modules.schedule.model.ScheduleUser;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: schedule_record
 * @Author: swsc
 * @Date: 2021-09-23
 * @Version: V1.0
 */
@Service
public class ScheduleRecordServiceImpl extends ServiceImpl<ScheduleRecordMapper, ScheduleRecord> implements IScheduleRecordService {

//    @Autowired
//    private ISysUserService sysUserService;
//    @Autowired
//    private ILineService lineService;

    @Override
    public List<ScheduleRecord> getScheduleRecordBySchedule(Integer scheduleId) {
        return this.baseMapper.getScheduleRecordBySchedule(scheduleId);
    }

    @Override
    public List<ScheduleUser> getScheduleUserByDate(String date, String username) {
        return this.baseMapper.getScheduleUserByDate(date,username);
    }

    @Override
    public List<ScheduleRecordModel> getRecordListByUserAndDate(String userId, String date) {
        return this.baseMapper.getRecordListByUserAndDate(userId, date);
    }

    @Override
    public List<ScheduleRecordModel> getAllScheduleRecordsByMonth(String date,String orgId) {
        return this.baseMapper.getAllScheduleRecordsByMonth(date,orgId);
    }

    @Override
    public List<LoginUser> getScheduleUserDataByDay(String day, String orgId) {
        return this.baseMapper.getScheduleUserDataByDay(day,orgId);
    }

    @Override
    public List<ScheduleRecordModel> getRecordListByDay(String date) {
        return this.baseMapper.getRecordListByDay(date);
    }

    @Override
    public List<ScheduleRecordModel>getRecordListByDayAndUserIds(String date,List<String>userIds) {
        return this.baseMapper.getRecordListByDayAndUserIds(date,userIds);
    }

    @Override
    public List<ScheduleRecord> getRecordListInDays(String userId,String startDate, String endDate) {
        return this.baseMapper.getRecordListInDays(userId,startDate,endDate);
    }
    @Override
    public List<ScheduleUser> getScheduleUserByDateAndOrgCode(String date,String username, String orgCode) {
        return this.baseMapper.getScheduleUserByDateAndOrgCode(date,username,orgCode);
    }

    @Override
    public List<ScheduleUser> getScheduleUserByDateAndOrgCodeAndOrgId(String date, String username, String orgCode, String orgId) {
        return this.baseMapper.getScheduleUserByDateAndOrgCodeAndOrgId(date, username, orgCode, orgId);
    }
    @Override
    public Integer getZhiBanNum(Map map) {
        if (ObjectUtil.isNotEmpty(map.get("lineId"))) {
            String lineCode = map.get("lineId").toString();
         // CsLine line=lineService.getOne(new QueryWrapper<CsLine>().eq("line_code",lineCode));
            // todo 后期修改
            List<String> banzuList = new ArrayList<>();
           //List<String> banzuList = sysUserService.getBanzuListByLine(line.getId());
            if (banzuList != null && banzuList.size() > 0) {
                String[] emp=new String[banzuList.size()];
                int i=0;
                for(String ce:banzuList){
                    emp[i]=ce;
                    i++;
                }
                map.put("orgIds", emp);
            }
        }
        String today= DateUtil.format(new Date(),"yyyy-MM-dd");
        map.put("today",today);
        return this.baseMapper.getZhiBanNum(map);
    }

    /**
     * 根据日期查询班次情况
     *
     * @param page
     * @param scheduleRecordDTO
     * @return
     */
    @Override
    public IPage<SysUserScheduleDTO> getStaffOnDuty(Page<SysUserScheduleDTO> page, ScheduleRecordDTO scheduleRecordDTO) {
        return page;
    }

}
