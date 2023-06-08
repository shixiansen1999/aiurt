package com.aiurt.modules.personnelportrait.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.api.PersonnelPortraitInspectionApi;
import com.aiurt.boot.api.PersonnelPortraitPatrolApi;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.common.api.IBaseApi;
import com.aiurt.modules.common.api.PersonnelPortraitFaultApi;
import com.aiurt.modules.fault.constants.FaultConstant;
import com.aiurt.modules.fault.dto.FaultDeviceDTO;
import com.aiurt.modules.fault.dto.FaultHistoryDTO;
import com.aiurt.modules.fault.dto.FaultMaintenanceDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.personnelportrait.dto.*;
import com.aiurt.modules.personnelportrait.service.PersonnelPortraitService;
import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.mapper.CsStationPositionMapper;
import com.aiurt.modules.position.service.ICsLineService;
import com.aiurt.modules.position.service.ICsStationService;
import com.aiurt.modules.schedule.dto.ScheduleUserWorkDTO;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.entity.SysRole;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.system.entity.SysUserRole;
import com.aiurt.modules.system.mapper.SysUserAptitudesMapper;
import com.aiurt.modules.system.mapper.SysUserMapper;
import com.aiurt.modules.system.mapper.SysUserPerfMapper;
import com.aiurt.modules.system.service.*;
import com.aiurt.modules.train.task.dto.TrainExperienceDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.*;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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
    private final IBaseApi iBaseApi;
    private final PersonnelPortraitFaultApi personnelPortraitFaultApi;
    private final PersonnelPortraitInspectionApi personnelPortraitInspectionApi;
    private final PersonnelPortraitPatrolApi personnelPortraitPatrolApi;

    private final ISysUserService sysUserService;
    private final ISysDepartService sysDepartService;
    private final ISysUserRoleService sysUserRoleService;
    private final ISysRoleService sysRoleService;
    private final ICsLineService csLineService;
    private final ICsStationService csStationService;
    private final ICsUserMajorService csUserMajorService;

    private final CsStationPositionMapper csStationPositionMapper;
    private final SysUserMapper sysUserMapper;
    private final SysUserPerfMapper sysUserPerfMapper;
    private final SysUserAptitudesMapper sysUserAptitudesMapper;

    // 用户职级字典编码
    private final String JOB_GRADE = "job_grade";


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
//        String line = this.selectLine(orgCode);
        List<String> stations = this.selectStation(orgCode);
        String station = stations.stream().collect(Collectors.joining(","));
        long number = stations.size();
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
//        personnelPortrait.setLine(line);
        personnelPortrait.setStation(station);
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
    private List<String> selectStation(String orgCode) {
        List<CsStation> stations = csStationService.lambdaQuery()
                .eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(CsStation::getSysOrgCode, orgCode)
                .list();
        if (CollUtil.isEmpty(stations)) {
            Collections.emptyList();
        }
        List<String> stationNames = stations.stream().map(CsStation::getStationName).collect(Collectors.toList());
        return stationNames;
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
        Map<String, String> jobGradeMap = iSysBaseApi.getDictItems(JOB_GRADE)
                .stream()
                .collect(Collectors.toMap(k -> k.getValue(), v -> v.getText()));
        List<String> userIds = users.stream().map(SysUser::getId).distinct().collect(Collectors.toList());
        List<String> usernames = users.stream().map(SysUser::getUsername).distinct().collect(Collectors.toList());
        List<ScheduleUserWorkDTO> todayUserWork = iBaseApi.getTodayUserWork(userIds);
        List<FaultMaintenanceDTO> faultMaintenances = personnelPortraitFaultApi.personnelPortraitStatic(usernames);
        for (SysUser user : users) {
            String seniority = null;
            String role = null;
            String level = null;
            String dutyStatus = "休息";
            Date workingTime = user.getWorkingTime();
            // 判断工龄显示方式
            String speciality = this.getSeniority(workingTime);

            if (ObjectUtil.isNotEmpty(user.getJobGrade())) {
                level = jobGradeMap.get(String.valueOf(user.getJobGrade()));
            }
            String id = user.getId();
            if (CollUtil.isNotEmpty(userRoleMap.get(id))) {
                role = userRoleMap.get(id).stream().map(SysRole::getRoleName)
                        .collect(Collectors.joining(";"));
            }
            ScheduleUserWorkDTO scheduleUserWork = todayUserWork.stream()
                    .filter(l -> user.getId().equals(l.getUserId()))
                    .filter(l -> DateUtil.isIn(new Date(), l.getStartTime(), l.getEndTime())
                            && "1".equals(l.getWork()))
                    .findFirst().orElseGet(ScheduleUserWorkDTO::new);
            if (StrUtil.isNotEmpty(scheduleUserWork.getWork())) {
                dutyStatus = "值班中";
            }
            FaultMaintenanceDTO faultMaintenance = faultMaintenances.stream()
                    .filter(l -> user.getUsername().equals(l.getUsername())).findFirst()
                    .orElseGet(FaultMaintenanceDTO::new);
            if (ObjectUtil.isNotEmpty(faultMaintenance.getNum()) && 8 < faultMaintenance.getNum()
                    && ObjectUtil.isNotEmpty(faultMaintenance.getDeviceTypeName())) {
                speciality = "擅长" + faultMaintenance.getDeviceTypeName() + "维修";
            }
            userInfo = new UserInfoResDTO();
            userInfo.setUserId(id);
            userInfo.setPicurl(user.getAvatar());
            // 根据排班判定。当天有排班就值班，排班班次基础数据那有个字段：是否为休息日。
            userInfo.setDutyStatus(dutyStatus);
            userInfo.setUsername(user.getRealname());
            userInfo.setRole(role);
            userInfo.setLevel(level);
            userInfo.setSeniority(seniority);
            // 历史设备维修类型维修数>8次，则显示：擅长$设备类型$维修
            userInfo.setSpeciality(speciality);
            userInfos.add(userInfo);
        }
        return userInfos;
    }

    /**
     * 工龄判断
     *
     * @param workingTime
     * @return
     */
    private String getSeniority(Date workingTime) {
        if (ObjectUtil.isNotEmpty(workingTime)) {
            long year = DateUtil.betweenYear(workingTime, new Date(), false);
            if (3 <= year && 5 > year) {
                return "工龄3年+";
            } else if (5 <= year && 7 > year) {
                return "工龄5年+";
            } else if (7 <= year && 10 > year) {
                return "工龄7年+";
            } else if (10 >= year) {
                return "工龄10年+";
            }
        }
        return null;
    }

    @Override
    public UserDetailResDTO userDetail(String userId) {
        SysUser sysUser = sysUserService.lambdaQuery()
                .eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(SysUser::getId, userId)
                .one();

        UserDetailResDTO userDetail = new UserDetailResDTO();
        if (ObjectUtil.isEmpty(sysUser)) {
            return userDetail;
        }
        Map<String, String> sexMap = iSysBaseApi.getDictItems("sex")
                .stream()
                .collect(Collectors.toMap(k -> k.getValue(), v -> v.getText()));
        Map<String, String> jobGradeMap = iSysBaseApi.getDictItems(JOB_GRADE)
                .stream()
                .collect(Collectors.toMap(k -> k.getValue(), v -> v.getText()));
        String sex = null;
        Long year = null;
        String seniority = this.getSeniority(sysUser.getWorkingTime());
        String speciality = null;
        String level = null;
        String roleCode = null;
        String roleName = null;
        String majorCode = null;
        String majorName = null;
        if (ObjectUtil.isNotEmpty(sysUser.getSex())) {
            sex = sexMap.get(String.valueOf(sysUser.getSex()));
        }
        if (ObjectUtil.isNotEmpty(sysUser.getEntryDate())) {
            year = DateUtil.betweenYear(sysUser.getEntryDate(), new Date(), false);
        }
        if (ObjectUtil.isNotEmpty(sysUser.getJobGrade())) {
            level = jobGradeMap.get(String.valueOf(sysUser.getJobGrade()));
        }
        List<FaultMaintenanceDTO> faultMaintenances = personnelPortraitFaultApi.personnelPortraitStatic(Arrays.asList(sysUser.getUsername()));
        FaultMaintenanceDTO faultMaintenance = faultMaintenances.stream()
                .filter(l -> sysUser.getUsername().equals(l.getUsername())).findFirst()
                .orElseGet(FaultMaintenanceDTO::new);
        if (ObjectUtil.isNotEmpty(faultMaintenance.getNum()) && 8 < faultMaintenance.getNum()
                && ObjectUtil.isNotEmpty(faultMaintenance.getDeviceTypeName())) {
            speciality = "擅长" + faultMaintenance.getDeviceTypeName() + "维修";
        }
        List<SysUserRole> userRoles = sysUserRoleService.lambdaQuery()
                .in(SysUserRole::getUserId, userId)
                .select(SysUserRole::getUserId, SysUserRole::getRoleId)
                .list();
        if (CollUtil.isNotEmpty(userRoles)) {
            List<String> roleIds = userRoles.stream().map(SysUserRole::getRoleId).distinct().collect(Collectors.toList());
            List<SysRole> roles = sysRoleService.lambdaQuery()
                    .in(SysRole::getId, roleIds)
                    .select(SysRole::getId, SysRole::getRoleCode, SysRole::getRoleName)
                    .list();
            if (CollUtil.isNotEmpty(roles)) {
                roleCode = roles.stream().map(SysRole::getRoleCode).collect(Collectors.joining(","));
                roleName = roles.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
            }
        }
        List<CsUserMajorModel> majors = csUserMajorService.getMajorByUserId(userId);
        if (CollUtil.isNotEmpty(majors)) {
            majorCode = majors.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.joining(","));
            majorName = majors.stream().map(CsUserMajorModel::getMajorName).collect(Collectors.joining(","));
        }

        userDetail.setUserId(sysUser.getId());
        userDetail.setPicurl(sysUser.getAvatar());
        userDetail.setUsername(sysUser.getRealname());
        userDetail.setGender(sex);
        userDetail.setYear(year);
        userDetail.setSeniority(seniority);
        userDetail.setSpeciality(speciality);
        // 确认过此处使用专业权限
        userDetail.setMajorCode(majorCode);
        userDetail.setMajorName(majorName);
        userDetail.setOrgCode(sysUser.getOrgCode());
        userDetail.setOrgName(sysUser.getOrgName());
        userDetail.setRoleCode(roleCode);
        userDetail.setRoleName(roleName);
        userDetail.setLevel(level);
        userDetail.setJobNumber(sysUser.getWorkNo());
        userDetail.setCertificateNumber(sysUser.getPermitCode());
        userDetail.setPhone(sysUser.getPhone());
        return userDetail;
    }

    @Override
    public RadarResDTO radarMap(String userId) {
        SysUser sysUser = sysUserService.getById(userId);
        if (ObjectUtil.isEmpty(sysUser)) {
            throw new AiurtBootException("未找到对应的用户信息！");
        }
        // 同一班组用户
        List<LoginUser> loginUsers = iSysBaseApi.getUserByDeptCode(sysUser.getOrgCode());
        List<String> usernames = loginUsers.stream().map(LoginUser::getUsername).collect(Collectors.toList());
        List<String> userIds = loginUsers.stream().map(LoginUser::getId).collect(Collectors.toList());

        String orgCode = sysUser.getOrgCode();
        RadarModel handleRadar = personnelPortraitFaultApi.getHandleNumber(userId, usernames);
        RadarModel performanceRadar = this.getPerformance(userId, userIds);
        RadarModel aptitudeRadar = this.getAptitude(userId, orgCode);
        // 用户工龄可以直接根据所属组织机构查询过滤
        RadarModel seniorityRadar = this.getUserSeniority(userId, orgCode);
        RadarModel efficiencyRadar = personnelPortraitFaultApi.getEfficiency(sysUser.getUsername(), usernames);

        // 故障处理总次数
        double handle = calculateScore(handleRadar.getCurrentValue(), handleRadar.getMaxValue(), handleRadar.getMinValue());
        // 绩效
        double performance = calculateScore(performanceRadar.getCurrentValue(), performanceRadar.getMaxValue(), performanceRadar.getMinValue());
        // 资质
        double aptitude = calculateScore(aptitudeRadar.getCurrentValue(), aptitudeRadar.getMaxValue(), aptitudeRadar.getMinValue());
        // 工龄
        double seniority = calculateScore(seniorityRadar.getCurrentValue(), seniorityRadar.getMaxValue(), seniorityRadar.getMinValue());
        // 解决效率
        double efficiency = calculateScore(efficiencyRadar.getCurrentValue(), efficiencyRadar.getMaxValue(), efficiencyRadar.getMinValue());

        RadarResDTO data = new RadarResDTO();
        data.setHandle(handle);
        data.setPerformance(performance);
        data.setAptitude(aptitude);
        data.setSeniority(seniority);
        data.setEfficiency(efficiency);
        return data;
    }

    /**
     * 雷达图-获取用户的资质信息
     *
     * @param userId
     * @param orgCode
     * @return
     */
    private RadarModel getAptitude(String userId, String orgCode) {
        List<RadarAptitudeModel> aptitudes = sysUserAptitudesMapper.getAptitude(orgCode);
        List<Integer> numbers = aptitudes.stream().map(RadarAptitudeModel::getNumber).collect(Collectors.toList());

        RadarModel radarModel = new RadarModel();
        RadarAptitudeModel radarAptitudeModel = aptitudes.stream()
                .filter(l -> userId.equals(l.getUserId()))
                .findFirst()
                .orElse(null);
        // 班组其他成员有数据，但是查询的用户不一定有数据
        if (ObjectUtil.isEmpty(radarAptitudeModel)) {
            return radarModel;
        }
        Integer currentValue = radarAptitudeModel.getNumber();
        radarModel.setCurrentValue(Double.valueOf(currentValue));
        if (CollUtil.isNotEmpty(numbers)) {
            Integer maxValue = Collections.max(numbers);
            Integer minValue = Collections.min(numbers);
            radarModel.setMaxValue(Double.valueOf(maxValue));
            radarModel.setMinValue(Double.valueOf(minValue));
        }
        return radarModel;
    }

    /**
     * 雷达图-获取用户的绩效信息
     *
     * @param userId
     * @param userIds
     */
    private RadarModel getPerformance(String userId, List<String> userIds) {
        // 近一年
        Date date = DateUtil.offsetMonth(new Date(), -12);
        List<RadarPerformanceModel> performances = sysUserPerfMapper.getPerformance(date);
        if (CollUtil.isNotEmpty(userIds) && CollUtil.isNotEmpty(performances)) {
            performances = performances.stream().filter(l -> userIds.contains(l.getUserId())).collect(Collectors.toList());
        }

        RadarModel radarModel = new RadarModel();
        RadarPerformanceModel radarPerformanceModel = performances.stream().filter(l -> userId.equals(l.getUserId()))
                .findFirst()
                .orElse(null);
        // 班组其他成员有数据，但是查询的用户不一定有数据
        if (ObjectUtil.isEmpty(radarPerformanceModel)) {
            return radarModel;
        }
        double currentValue = Double.parseDouble(radarPerformanceModel.getScore());
        radarModel.setCurrentValue(currentValue);

        List<Double> scores = performances.stream()
                .map(performance -> Double.parseDouble(performance.getScore()))
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(scores)) {
            Double maxValue = Collections.max(scores);
            Double minValue = Collections.min(scores);
            radarModel.setMaxValue(maxValue);
            radarModel.setMinValue(minValue);
        }
        return radarModel;
    }

    /**
     * 雷达图-获取班组用户的工龄
     *
     * @param userId
     * @param orgCode
     */
    private RadarModel getUserSeniority(String userId, String orgCode) {
        List<LoginUser> seniorityNumber = sysUserMapper.getSeniorityNumber(orgCode);
        RadarModel radarModel = new RadarModel();
        LoginUser maxDateUser = seniorityNumber.stream()
                .max(Comparator.comparing(LoginUser::getEntryDate))
                .orElse(null);
        LoginUser minDateUser = seniorityNumber.stream()
                .min(Comparator.comparing(LoginUser::getEntryDate))
                .orElse(null);
        LoginUser currentUser = seniorityNumber.stream()
                .filter(l -> userId.equals(l.getId()))
                .findFirst()
                .orElse(null);
        if (ObjectUtil.isEmpty(maxDateUser) || ObjectUtil.isEmpty(minDateUser)
                || ObjectUtil.isEmpty(currentUser)) {
            return radarModel;
        }
        Date date = new Date();
        long minValue = DateUtil.between(maxDateUser.getEntryDate(), date, DateUnit.DAY);
        long maxValue = DateUtil.between(minDateUser.getEntryDate(), date, DateUnit.DAY);
        long currentValue = DateUtil.between(currentUser.getEntryDate(), date, DateUnit.DAY);
        radarModel.setCurrentValue((double) currentValue);
        radarModel.setMaxValue((double) maxValue);
        radarModel.setMinValue((double) minValue);
        return radarModel;
    }

    /**
     * 雷达图分数转化计算
     *
     * @param currentValue
     * @param minValue
     * @param maxValue
     * @return
     */
    private static double calculateScore(double currentValue, double maxValue, double minValue) {
        // 最高分为
        final int topScore = 100;
        final int lowestScore = 60;

        if (maxValue == minValue) {
            if (0 != currentValue && currentValue == maxValue) {
                return topScore;
            } else {
                return lowestScore;
            }
        }
        // 计算当前值相对于最小值和最大值的百分比
        double percentage = 1.0 * (currentValue - minValue) / (maxValue - minValue);
        // 将百分比映射到分数范围
        double score = percentage * (topScore - lowestScore) + lowestScore;
        // 如果小数较多则保留3位小数
        score = Double.parseDouble(String.format("%.3f", score));
        return score;
    }

    @Override
    public DashboardResDTO dashboard(String userId) {
        DashboardResDTO data = new DashboardResDTO();
        int score = 90;
        final int sixty = 60;
        final int seventy = 70;
        final int eighty = 80;
        final int ninety = 90;
        final int oneHundred = 100;

        String grade = null;
        // 分值60-69，显示一般； 70-79，显示中等； 80-89，显示良好； 90-100显示优秀
        if (sixty <= score && seventy > score) {
            grade = "一般";
        } else if (seventy <= score && eighty > score) {
            grade = "中等";
        } else if (eighty <= score && ninety > score) {
            grade = "良好";
        } else if (ninety <= score && oneHundred >= score) {
            grade = "优秀";
        }
        data.setScore(BigDecimal.valueOf(score));
        data.setOrgRank(1);
        data.setOrgTotal(16);
        data.setMajorRank(1);
        data.setMajorTotal(32);
        data.setGrade(grade);
        return data;
    }

    @Override
    public List<ExperienceResDTO> experience(String userId) {
        List<TrainExperienceDTO> trainExperience = iBaseApi.getTrainExperience(userId);
        List<ExperienceResDTO> experiences = new ArrayList<>();
        ExperienceResDTO experienceResDTO = null;
        for (TrainExperienceDTO experience : trainExperience) {
            experienceResDTO = new ExperienceResDTO();
            if (!userId.equals(experience.getUserId())) {
                continue;
            }
            String startTime = DateUtil.format(experience.getStartTime(), "YYYY-MM-dd HH:mm:ss");
            String endTime = DateUtil.format(experience.getEndTime(), "YYYY-MM-dd HH:mm:ss");
            String taskName = "参与培训：" + experience.getTaskName();
            experienceResDTO.setStarDate(startTime);
            experienceResDTO.setEndDate(endTime);
            experienceResDTO.setDescription(taskName);
            experiences.add(experienceResDTO);
        }
        return experiences;
    }

    @Override
    public WaveResDTO waveRose(String userId) {
        final int year = DateUtil.thisYear();
        // 近几年
        final int flag = 5;
        final int flagYearAgo = year - flag;
        // 获取近五年的巡视任务数据
        Map<Integer, Long> patrolMap = personnelPortraitPatrolApi.getPatrolTaskNumber(userId, flagYearAgo, year);
        // 获取近五年的检修任务数据
        Map<Integer, Long> inspectionMap = personnelPortraitInspectionApi.getInspectionNumber(userId, flagYearAgo, year);
        // 获取近五年的故障任务数据
        Map<Integer, Long> faultMap = personnelPortraitFaultApi.getFaultTaskNumber(userId, flagYearAgo, year);
        WaveResDTO waveRes = new WaveResDTO();
        List<Integer> years = new LinkedList<>();
        List<Long> patrols = new LinkedList<>();
        List<Long> inspections = new LinkedList<>();
        List<Long> faults = new LinkedList<>();
        for (int i = year - flag + 1; i <= year; i++) {
            years.add(i);
            patrols.add(ObjectUtil.isEmpty(patrolMap.get(i)) ? 0 : patrolMap.get(i));
            inspections.add(ObjectUtil.isEmpty(inspectionMap.get(i)) ? 0 : inspectionMap.get(i));
            faults.add(ObjectUtil.isEmpty(faultMap.get(i)) ? 0 : faultMap.get(i));
        }
        waveRes.setYear(years);
        waveRes.setPatrol(patrols);
        waveRes.setInspection(inspections);
        waveRes.setFault(faults);
        return waveRes;
    }

    @Override
    public List<HistoryResDTO> history(String userId) {
        List<FaultHistoryDTO> faultHistorys = personnelPortraitFaultApi.repairDeviceTopFive(userId);
        List<HistoryResDTO> historys = new ArrayList<>();
        HistoryResDTO history = null;
        for (FaultHistoryDTO faultHistory : faultHistorys) {
            history = new HistoryResDTO();
            history.setName(faultHistory.getName());
            history.setValue(faultHistory.getValue());
            historys.add(history);
        }
        return historys;
    }

    @Override
    public List<FaultDeviceDTO> deviceInfo(String userId) {
        return personnelPortraitFaultApi.deviceInfo(userId);

    }

    @Override
    public IPage<Fault> historyRecord(Integer pageNo, Integer pageSize, String userId, HttpServletRequest request) {
        SysUser sysUser = sysUserService.getById(userId);
        if (ObjectUtil.isEmpty(sysUser)) {
            throw new AiurtBootException("未找到对应的用户信息！");
        }
        Fault fault = new Fault();
        fault.setUsername(sysUser.getUsername());
        // 已完成状态
        fault.setStatus(FaultConstant.FAULT_STATUS);
        return personnelPortraitFaultApi.selectFaultRecordPageList(fault, pageNo, pageSize, request);
    }
}
