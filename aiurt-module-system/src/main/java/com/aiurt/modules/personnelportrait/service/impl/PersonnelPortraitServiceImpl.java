package com.aiurt.modules.personnelportrait.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.personnelportrait.dto.*;
import com.aiurt.modules.personnelportrait.service.PersonnelPortraitService;
import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.mapper.CsStationPositionMapper;
import com.aiurt.modules.position.service.ICsLineService;
import com.aiurt.modules.position.service.ICsStationService;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.entity.SysRole;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.system.entity.SysUserRole;
import com.aiurt.modules.system.service.ISysDepartService;
import com.aiurt.modules.system.service.ISysRoleService;
import com.aiurt.modules.system.service.ISysUserRoleService;
import com.aiurt.modules.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PersonnelPortraitServiceImpl implements PersonnelPortraitService {

    private final ISysBaseAPI iSysBaseApi;
    private final ISysUserService sysUserService;
    private final ISysDepartService sysDepartService;
    private final ISysUserRoleService sysUserRoleService;
    private final ISysRoleService sysRoleService;
    private final ICsLineService csLineService;
    private final ICsStationService csStationService;
    private final CsStationPositionMapper csStationPositionMapper;


    @Override
    public PersonnelPortraitResDTO portrait(String orgCode) {
        PersonnelPortraitResDTO personnelPortrait = new PersonnelPortraitResDTO();
        SysDepart depart = sysDepartService.lambdaQuery()
                .eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(SysDepart::getOrgCode, orgCode)
                .last("limit 1")
                .one();
        if (ObjectUtil.isEmpty(depart)) {
            return personnelPortrait;
        }

        String departName = depart.getDepartName();
        final String title = departName + "人员画像";
        String leader = null;
        String line = this.selectLine(orgCode);
        Long number = this.countStation(orgCode);
        String position = this.selectPosition(orgCode);

        List<UserInfoResDTO> userInfos = Collections.emptyList();
        List<SysUser> users = sysUserService.lambdaQuery()
                .eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(SysUser::getOrgCode, orgCode)
                .list();
        if (CollUtil.isNotEmpty(users)) {
            SysRole sysRole = sysRoleService.lambdaQuery()
                    .eq(SysRole::getRoleCode, RoleConstant.FOREMAN)
                    .last("limit 1")
                    .one();
            List<String> userIds = users.stream().map(SysUser::getId).collect(Collectors.toList());
            Map<String, List<SysRole>> userRoleMap = new HashMap<>(16);
            List<SysUserRole> userRoles = sysUserRoleService.lambdaQuery()
                    .in(SysUserRole::getUserId, userIds)
                    .select(SysUserRole::getUserId, SysUserRole::getRoleId)
                    .list();
            if (CollUtil.isNotEmpty(userRoles)) {
                if (ObjectUtil.isNotEmpty(sysRole)) {
                    // 找到工班长角色的用户
                    List<String> foremanUserIds = userRoles.stream().filter(l -> sysRole.getId().equals(l.getRoleId()))
                            .map(SysUserRole::getUserId).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(foremanUserIds)) {
                        leader = users.stream().filter(user -> foremanUserIds.contains(user.getId()))
                                .map(SysUser::getRealname)
                                .collect(Collectors.joining(","));
                    }
                }

                List<String> roleIds = userRoles.stream()
                        .filter(l -> ObjectUtil.isNotEmpty(l.getRoleId()))
                        .map(SysUserRole::getRoleId)
                        .distinct()
                        .collect(Collectors.toList());
                List<SysRole> roles = sysRoleService.lambdaQuery()
                        .in(SysRole::getId, roleIds)
                        .select(SysRole::getId, SysRole::getRoleCode, SysRole::getRoleName)
                        .list();
                Map<String, List<SysRole>> roleMap = roles.stream().collect(Collectors.groupingBy(SysRole::getId));
                for (SysUserRole userRole : userRoles) {
                    String userId = userRole.getUserId();
                    List<SysRole> sysRoles = userRoleMap.get(userId);
                    if (CollUtil.isEmpty(sysRoles)) {
                        userRoleMap.put(userId, roleMap.get(userRole.getRoleId()));
                        continue;
                    }
                    sysRoles.addAll(roleMap.get(userRole.getRoleId()));
                    userRoleMap.put(userId, sysRoles);
                }
            }
            userInfos = this.setUserInfo(users, userRoleMap);
        }

        personnelPortrait.setTitle(title);
        // 在这个班组下为工班长的人
        personnelPortrait.setLeader(leader);
        personnelPortrait.setLine(line);
        personnelPortrait.setNumber(number);
        personnelPortrait.setPosition(position);
        personnelPortrait.setUserInfos(userInfos);
        return personnelPortrait;
    }

    /**
     * 查询班组下的工区位置
     *
     * @param orgCode
     * @return
     */
    private String selectPosition(String orgCode) {
        if (StrUtil.isEmpty(orgCode)) {
            return null;
        }
        List<String> positions = csStationPositionMapper.selectPosition(orgCode);
        if (CollUtil.isEmpty(positions)) {
            return null;
        }
        String position = positions.stream().collect(Collectors.joining(","));
        return position;
    }

    /**
     * 统计班组下的站点个数
     *
     * @param orgCode
     * @return
     */
    private Long countStation(String orgCode) {
        Long count = csStationService.lambdaQuery()
                .eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(CsStation::getSysOrgCode, orgCode)
                .count();
        return count;
    }

    /**
     * 查询班组下的线路
     *
     * @param orgCode
     * @return
     */
    private String selectLine(String orgCode) {
        List<CsLine> lines = csLineService.lambdaQuery()
                .eq(CsLine::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(CsLine::getSysOrgCode, orgCode)
                .list();
        if (CollUtil.isEmpty(lines)) {
            return null;
        }
        String lineName = lines.stream().map(CsLine::getLineName).collect(Collectors.joining(","));
        return lineName;
    }

    /**
     * 查询人员列表信息
     *
     * @param users
     * @param userRoleMap
     * @return
     */
    private List<UserInfoResDTO> setUserInfo(List<SysUser> users, Map<String, List<SysRole>> userRoleMap) {
        List<UserInfoResDTO> userInfos = new ArrayList<>();
        UserInfoResDTO userInfo = null;
        Date nowDate = new Date();
        Map<String, String> jobGradeMap = iSysBaseApi.getDictItems("job_grade")
                .stream()
                .collect(Collectors.toMap(k -> k.getValue(), v -> v.getText()));
        for (SysUser user : users) {
            String seniority = null;
            String role = null;
            String level = null;
            Date workingTime = user.getWorkingTime();
            if (ObjectUtil.isNotEmpty(workingTime)) {
                long year = DateUtil.betweenYear(workingTime, nowDate, false);
                seniority = String.valueOf(year);
            }
            if (ObjectUtil.isNotEmpty(user.getJobGrade())) {
                level = jobGradeMap.get(String.valueOf(user.getJobGrade()));
            }
            String id = user.getId();
            if (CollUtil.isNotEmpty(userRoleMap.get(id))) {
                role = userRoleMap.get(id).stream().map(SysRole::getRoleName)
                        .collect(Collectors.joining(";"));
            }
            userInfo = new UserInfoResDTO();
            userInfo.setUserId(id);
            userInfo.setPicurl(user.getAvatar());
            // todo 根据排班判定。当天有排班就值班，排班班次基础数据那有个字段：是否为休息日。
            userInfo.setDutyStatus("值班中");
            userInfo.setUsername(user.getRealname());
            userInfo.setRole(role);
            userInfo.setLevel(level);
            // todo 判断工龄显示方式
            userInfo.setSeniority(seniority);
            // todo 历史设备维修类型维修数>8次，则显示：擅长$设备类型$维修
            userInfo.setSpeciality("");
            userInfos.add(userInfo);
        }
        return userInfos;
    }

    @Override
    public UserDetailResDTO userDetail(String userId) {
        return new UserDetailResDTO();
    }

    @Override
    public RadarResDTO radarMap(String userId) {
        RadarResDTO data = new RadarResDTO();
        data.setHandle(BigDecimal.valueOf(60));
        data.setPerformance(BigDecimal.valueOf(60));
        data.setAptitude(BigDecimal.valueOf(60));
        data.setSeniority(BigDecimal.valueOf(60));
        data.setEfficiency(BigDecimal.valueOf(60));
        return data;
    }

    @Override
    public DashboardResDTO dashboard(String userId) {
        DashboardResDTO data = new DashboardResDTO();
        data.setScore(BigDecimal.valueOf(90));
        data.setOrgRank(1);
        data.setOrgTotal(16);
        data.setMajorRank(1);
        data.setMajorTotal(32);
        data.setPerformances(new ArrayList<>());
        return data;
    }

    @Override
    public List<ExperienceResDTO> experience(String userId) {
        return new ArrayList<>();
    }

    @Override
    public List<WaveResDTO> waveRose(String userId) {
        return new ArrayList<>();
    }

    @Override
    public HistoryResDTO history(String userId) {
        return new HistoryResDTO();
    }
}
