package com.aiurt.modules.schedule.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.schedule.dto.*;
import com.aiurt.modules.schedule.entity.ScheduleItem;
import com.aiurt.modules.schedule.entity.ScheduleLog;
import com.aiurt.modules.schedule.entity.ScheduleRecord;
import com.aiurt.modules.schedule.entity.ScheduleRuleItem;
import com.aiurt.modules.schedule.mapper.ScheduleRecordMapper;
import com.aiurt.modules.schedule.model.ScheduleRecordModel;
import com.aiurt.modules.schedule.model.ScheduleUser;
import com.aiurt.modules.schedule.service.IScheduleItemService;
import com.aiurt.modules.schedule.service.IScheduleLogService;
import com.aiurt.modules.schedule.service.IScheduleRecordService;
import com.aiurt.modules.schedule.service.IScheduleRuleItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SiteModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private IScheduleItemService itemService;
    @Autowired
    private ISysBaseAPI userService;

    @Autowired
    private IScheduleLogService logService;

    @Autowired
    private IScheduleRuleItemService ruleItemService;
    @Autowired
    private IScheduleItemService ItemService;

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
    public List<ScheduleRecordModel> getAllScheduleRecordsByMonth(String date, String orgId,String text,List<String> orgList) {
        return this.baseMapper.getAllScheduleRecordsByMonth(date, orgId,text,orgList);
    }

    @Override
    public List<ScheduleRecordModel> getMySchedule(String date, String userId) {
        return this.baseMapper.getMySchedule(date, userId);
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
    public List<ScheduleUser> getScheduleUserByDateAndOrgCodeAndOrgId(String date, List<String> orgList , String orgId,String text) {
        return this.baseMapper.getScheduleUserByDateAndOrgCodeAndOrgId(date, orgList, orgId,text);
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

    /**
     * 查询今日当班人员信息
     *
     * @param orgCodes
     * @param date
     * @return
     */
    @Override
    public List<SysUserTeamDTO> getTodayOndutyDetailNoPage(List<String> orgCodes, Date date) {
        return baseMapper.getTodayOndutyDetailNoPage(orgCodes,date);
    }

    @Override
    @Transactional
    public Result<ScheduleRecord> editRecord(List<ScheduleRecordREditDTO> scheduleRecordREditDTOList) {
        Result<ScheduleRecord> result = new Result<ScheduleRecord>();
        for (ScheduleRecordREditDTO scheduleRecordREditDTO : scheduleRecordREditDTOList) {
            String item = "0";
            if (item.equals(scheduleRecordREditDTO.getSchedulingMethod())) {
                ScheduleRecord scheduleRecordEntity = this.getById(scheduleRecordREditDTO.getScheduleRecordId());
                if (scheduleRecordEntity == null) {
                    result.onnull("未找到对应实体");
                } else {
                    ScheduleItem oldItem = itemService.getById(scheduleRecordEntity.getItemId());
                    ScheduleItem newItem = itemService.getById(scheduleRecordREditDTO.getScheduleItemId());
                    scheduleRecordEntity.setItemId(newItem.getId());
                    scheduleRecordEntity.setColor(newItem.getColor());
                    scheduleRecordEntity.setItemName(newItem.getName());
                    scheduleRecordEntity.setStartTime(newItem.getStartTime());
                    scheduleRecordEntity.setEndTime(newItem.getEndTime());

                    this.updateById(scheduleRecordEntity);

                    ScheduleLog log = new ScheduleLog();
                    log.setDate(scheduleRecordEntity.getDate());
                    log.setRecordId(scheduleRecordEntity.getId());
                    log.setDelFlag(0);
                    log.setSourceItemId(oldItem.getId());
                    log.setSourceItemName(oldItem.getName());
                    log.setTargetItemId(newItem.getId());
                    log.setTargetItemName(newItem.getName());
                    log.setUserId(scheduleRecordEntity.getUserId());
//            LoginUser user = new LoginUser();
                    LoginUser user = userService.getUserById(log.getUserId());
                    log.setUserName(user.getRealname());
                    logService.save(log);
                }
            } else {
                Calendar start = Calendar.getInstance();
                start.setTime(scheduleRecordREditDTO.getStartTime());
                QueryWrapper wrapper = new QueryWrapper();
                wrapper.eq("rule_id", scheduleRecordREditDTO.getScheduleRuleId());
                List<ScheduleRuleItem> itemList = ruleItemService.list(wrapper);
                int itemSize = itemList.size();
                Map<Integer, Integer> scheduleRuleItemMap = new HashMap<>(itemSize);
                for (ScheduleRuleItem scheduleRuleItem : itemList) {
                    scheduleRuleItemMap.put(scheduleRuleItem.getSort(), scheduleRuleItem.getItemId());
                }
                int i = 0;
                while (!scheduleRecordREditDTO.getStartTime().after(scheduleRecordREditDTO.getEndTime())) {
                    LambdaQueryWrapper<ScheduleRecord> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(ScheduleRecord::getDelFlag, "0");
                    queryWrapper.eq(ScheduleRecord::getUserId, scheduleRecordREditDTO.getUserId());
                    queryWrapper.eq(ScheduleRecord::getDate, scheduleRecordREditDTO.getStartTime());
                    ScheduleRecord one = this.getOne(queryWrapper);

                    int index = (i % itemSize == 0 ? itemSize : i % itemSize);
                    Integer ruleItemId = scheduleRuleItemMap.get(index);
                    ScheduleItem scheduleItem = ItemService.getById(ruleItemId);
                    if (ObjectUtil.isNotEmpty(one)) {
                        ScheduleItem oldItem = itemService.getById(one.getItemId());
                        ScheduleItem newItem = scheduleItem;
                        one.setItemId(newItem.getId());
                        one.setColor(newItem.getColor());
                        one.setItemName(newItem.getName());
                        one.setStartTime(newItem.getStartTime());
                        one.setEndTime(newItem.getEndTime());

                        this.updateById(one);

                        ScheduleLog log = new ScheduleLog();
                        log.setDate(one.getDate());
                        log.setRecordId(one.getId());
                        log.setDelFlag(0);
                        log.setSourceItemId(oldItem.getId());
                        log.setSourceItemName(oldItem.getName());
                        log.setTargetItemId(newItem.getId());
                        log.setTargetItemName(newItem.getName());
                        log.setUserId(one.getUserId());
//            LoginUser user = new LoginUser();
                        LoginUser user = userService.getUserById(log.getUserId());
                        log.setUserName(user.getRealname());
                        logService.save(log);

                    } else {
                        ScheduleRecord record = ScheduleRecord.builder()
                                .userId(scheduleRecordREditDTO.getUserId())
                                .date(start.getTime())
                                .itemId(scheduleItem.getId())
                                .itemName(scheduleItem.getName())
                                .startTime(scheduleItem.getStartTime())
                                .endTime(scheduleItem.getEndTime())
                                .color(scheduleItem.getColor())
                                .delFlag(0)
                                .build();
                        this.save(record);

                        ScheduleLog log = new ScheduleLog();
                        log.setDate(record.getDate());
                        log.setRecordId(record.getId());
                        log.setDelFlag(0);
                        log.setTargetItemId(scheduleItem.getId());
                        log.setTargetItemName(scheduleItem.getName());
                        log.setUserId(record.getUserId());
//            LoginUser user = new LoginUser();
                        LoginUser user = userService.getUserById(log.getUserId());
                        log.setUserName(user.getRealname());
                        logService.save(log);
                    }
                    start.add(Calendar.DAY_OF_YEAR, 1);
                    i++;
                }
            }
        }
        result.success("修改成功!");
        return result;
    }
}
