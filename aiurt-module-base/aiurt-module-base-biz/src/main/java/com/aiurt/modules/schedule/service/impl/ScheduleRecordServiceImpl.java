package com.aiurt.modules.schedule.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.schedule.dto.*;
import com.aiurt.modules.schedule.entity.ScheduleRecord;
import com.aiurt.modules.schedule.mapper.ScheduleRecordMapper;
import com.aiurt.modules.schedule.model.ScheduleRecordModel;
import com.aiurt.modules.schedule.model.ScheduleUser;
import com.aiurt.modules.schedule.service.IScheduleRecordService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SiteModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

;

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
    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Override
    public List<ScheduleRecord> getScheduleRecordBySchedule(Integer scheduleId) {
        return this.baseMapper.getScheduleRecordBySchedule(scheduleId);
    }

    @Override
    public List<ScheduleUser> getScheduleUserByDate(String date, String username) {
        return this.baseMapper.getScheduleUserByDate(date, username);
    }

    @Override
    public List<ScheduleRecordModel> getRecordListByUserAndDate(String userId, String date) {
        return this.baseMapper.getRecordListByUserAndDate(userId, date);
    }

    @Override
    public List<ScheduleRecordModel> getAllScheduleRecordsByMonth(String date, String orgId) {
        return this.baseMapper.getAllScheduleRecordsByMonth(date, orgId);
    }

    @Override
    public List<LoginUser> getScheduleUserDataByDay(String day, String orgId) {
        return this.baseMapper.getScheduleUserDataByDay(day, orgId);
    }

    @Override
    public List<ScheduleRecordModel> getRecordListByDay(String date) {
        return this.baseMapper.getRecordListByDay(date);
    }

    @Override
    public List<ScheduleRecordModel> getRecordListByDayAndUserIds(String date, List<String> userIds) {
        return this.baseMapper.getRecordListByDayAndUserIds(date, userIds);
    }

    @Override
    public List<ScheduleRecord> getRecordListInDays(String userId, String startDate, String endDate) {
        return this.baseMapper.getRecordListInDays(userId, startDate, endDate);
    }

    @Override
    public List<ScheduleUser> getScheduleUserByDateAndOrgCode(String date, String username, String orgCode) {
        return this.baseMapper.getScheduleUserByDateAndOrgCode(date, username, orgCode);
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
                String[] emp = new String[banzuList.size()];
                int i = 0;
                for (String ce : banzuList) {
                    emp[i] = ce;
                    i++;
                }
                map.put("orgIds", emp);
            }
        }
        String today = DateUtil.format(new Date(), "yyyy-MM-dd");
        map.put("today", today);
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
        if (ObjectUtil.isEmpty(scheduleRecordDTO) || ObjectUtil.isEmpty(scheduleRecordDTO.getStartTime())) {
            return page;
        }
        // 根据日期条件查询班次情况
        List<SysUserScheduleDTO> result = baseMapper.getStaffOnDuty(page, scheduleRecordDTO);

        // 补充人员角色，工区，工区位置，工区负责人
        if (CollUtil.isNotEmpty(result)) {
            for (SysUserScheduleDTO sysUserScheduleDTO : result) {
                // 角色
                List<String> roleNamesByUsername = sysBaseAPI.getRoleNamesById(sysUserScheduleDTO.getUserId());
                if (CollUtil.isNotEmpty(roleNamesByUsername)) {
                    sysUserScheduleDTO.setRoleName(StrUtil.join("；", roleNamesByUsername));
                }
                LoginUser userById = sysBaseAPI.getUserById(sysUserScheduleDTO.getUserId());
                if (ObjectUtil.isNotEmpty(userById)) {
                    List<SiteModel> siteByOrgCode = sysBaseAPI.getSiteByOrgCode(userById.getOrgCode());
                    if (CollUtil.isNotEmpty(siteByOrgCode)) {
                        sysUserScheduleDTO.setSiteName(siteByOrgCode.stream().map(SiteModel::getSiteName).filter(site -> StrUtil.isNotEmpty(site)).collect(Collectors.joining(",")));
                        sysUserScheduleDTO.setSiteLocationName(siteByOrgCode.stream().map(SiteModel::getPosition).filter(site -> StrUtil.isNotEmpty(site)).collect(Collectors.joining(",")));
                        sysUserScheduleDTO.setSitPrincipalName(siteByOrgCode.stream().map(SiteModel::getRealName).filter(site -> StrUtil.isNotEmpty(site)).collect(Collectors.joining(",")));
                    }
                }
            }
        }
        return page.setRecords(result);
    }

    /**
     * 获取大屏的班组信息
     *
     * @param lineCode 线路code
     * @return
     */
    @Override
    public ScheduleBigScreenDTO getTeamData(String lineCode) {
        ScheduleBigScreenDTO result = new ScheduleBigScreenDTO();

        // 查询总班组数
        List<String> orgCodes = sysBaseAPI.getTeamBylineAndMajor(lineCode);

        // 查询班组总人员数
        List<LoginUser> userByDepIds = sysBaseAPI.getUserByDepIds(orgCodes);

        // 填充总班组数
        result.setTeamTotal(CollUtil.isNotEmpty(orgCodes) ? orgCodes.size() : 0);

        // 填充班组总人员数
        result.setUserTotal(CollUtil.isNotEmpty(userByDepIds) ? userByDepIds.size() : 0);
        result.setScheduleNum(0L);

        // 计算今日当班人数
        Page<SysUserTeamDTO> page = new Page<>(1, 10);
        List<String> userIdList = new ArrayList<>();
        if (CollUtil.isNotEmpty(userByDepIds)) {
            userIdList = userByDepIds.stream().map(LoginUser::getId).collect(Collectors.toList());
        }

        // 填充今日当班人数
        if (CollUtil.isNotEmpty(userIdList)) {
            List<SysUserTeamDTO> sysUserTeamDTOS = baseMapper.getTodayOndutyDetail(page, null, orgCodes, new Date());
            page.setRecords(sysUserTeamDTOS);
            result.setScheduleNum(page.getTotal());
        }

        return result;
    }


    /**
     * 获取大屏的班组信息-点击今日当班人数
     *
     * @param lineCode 线路code
     * @param page     分页参数
     * @param orgcode  班组code
     * @return
     */
    @Override
    public IPage<SysUserTeamDTO> getTodayOndutyDetail(String lineCode, String orgcode, Page<SysUserTeamDTO> page) {
        List<SysUserTeamDTO> result = new ArrayList<>();

        // 根据传入线路和自身管理专业获取班组信息
        List<String> orgCodes = sysBaseAPI.getTeamBylineAndMajor(lineCode);

        if (CollUtil.isNotEmpty(orgCodes)) {

            // 根据日期条件查询班次情况
            result = baseMapper.getTodayOndutyDetail(page, orgcode, orgCodes, new Date());

            // 填充角色名称
            for (SysUserTeamDTO sysUserTeamDTO : result) {
                List<String> roleNamesByUsername = sysBaseAPI.getRoleNamesById(sysUserTeamDTO.getUserId());
                if (CollUtil.isNotEmpty(roleNamesByUsername)) {
                    sysUserTeamDTO.setRoleName(StrUtil.join("；", roleNamesByUsername));
                }
            }
        }

        return page.setRecords(result);
    }

    /**
     * 获取大屏的班组信息-点击总人员数
     *
     * @param lineCode 线路code
     * @param page
     * @return
     */
    @Override
    public IPage<SysUserTeamDTO> getTotalPepoleDetail(String lineCode, String orgcode, Page<SysUserTeamDTO> page) {
        List<SysUserTeamDTO> result = new ArrayList<>();

        // 根据传入线路和自身管理专业获取班组信息
        List<String> orgCodes = sysBaseAPI.getTeamBylineAndMajor(lineCode);

        if (CollUtil.isNotEmpty(orgCodes)) {
            // 查询总人员列表
            result = baseMapper.getUserByDepIds(orgCodes, page, orgcode);

            // 填充角色名称
            for (SysUserTeamDTO sysUserTeamDTO : result) {
                List<String> roleNamesByUsername = sysBaseAPI.getRoleNamesById(sysUserTeamDTO.getUserId());
                if (CollUtil.isNotEmpty(roleNamesByUsername)) {
                    sysUserTeamDTO.setRoleName(StrUtil.join("；", roleNamesByUsername));
                }
            }
        }
        return page.setRecords(result);
    }

    /**
     * 获取大屏的班组信息-点击总班组数
     *
     * @param page     分页参数
     * @param lineCode 线路code
     * @return
     */
    @Override
    public IPage<SysTotalTeamDTO> getTotalTeamDetail(Page<SysTotalTeamDTO> page, String lineCode) {
        // 查询总班组数
        List<String> orgCodes = sysBaseAPI.getTeamBylineAndMajor(lineCode);
        if (CollUtil.isEmpty(orgCodes)) {
            return page;
        }

        // 获取总班组列表
        List<SysTotalTeamDTO> result = baseMapper.getTotalTeamDetail(page, orgCodes);
        page.setRecords(result);
        return page;
    }
}
