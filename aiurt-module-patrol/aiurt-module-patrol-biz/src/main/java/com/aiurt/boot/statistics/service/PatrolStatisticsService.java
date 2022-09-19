package com.aiurt.boot.statistics.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.constant.PatrolDictCode;
import com.aiurt.boot.statistics.dto.*;
import com.aiurt.boot.statistics.model.*;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskUser;
import com.aiurt.boot.task.mapper.*;
import com.aiurt.boot.task.service.IPatrolTaskService;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatrolStatisticsService {
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private IPatrolTaskService patrolTaskService;
    @Autowired
    private PatrolTaskMapper patrolTaskMapper;
    @Autowired
    private PatrolTaskUserMapper patrolTaskUserMapper;
    @Autowired
    private PatrolTaskOrganizationMapper patrolTaskOrganizationMapper;
    @Autowired
    private PatrolTaskStationMapper patrolTaskStationMapper;
    @Autowired
    private PatrolTaskDeviceMapper patrolTaskDeviceMapper;
    /**
     * 权限过滤标识
     */
    private final Integer ALLDATA = 1;

    /**
     * 首页巡视概况
     *
     * @return
     */
    public PatrolSituation getOverviewInfo(Date startDate, Date endDate, Integer isAllData) {
        Date newStartDate = DateUtil.parse(DateUtil.format(startDate, "yyyy-MM-dd 00:00:00"));
        Date newEndDate = DateUtil.parse(DateUtil.format(endDate, "yyyy-MM-dd 23:59:59"));
        PatrolSituation situation = new PatrolSituation();
//        List<PatrolTask> list = patrolTaskService.lambdaQuery().eq(PatrolTask::getDelFlag, 0)
//                // 过滤手工下发
////                .and(i -> i.ne(PatrolTask::getSource, PatrolConstant.TASK_MANUAL).or().isNull(PatrolTask::getSource))
//                .between(PatrolTask::getPatrolDate, newStartDate, newEndDate).list();
        List<CsUserDepartModel> departList = null;
        if (ObjectUtil.isEmpty(isAllData) || !ALLDATA.equals(isAllData)) {
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (ObjectUtil.isEmpty(loginUser)) {
                throw new AiurtBootException("检测到暂未登录，请登录系统后操作！");
            }
            departList = sysBaseApi.getDepartByUserId(loginUser.getId());
        }
        List<PatrolTask> list = patrolTaskMapper.getOverviewInfo(newStartDate, newEndDate, departList);

        long sum = list.stream().count();
        long finish = list.stream().filter(l -> PatrolConstant.TASK_COMPLETE.equals(l.getStatus())).count();
        long unfinish = sum - finish;
        long abnormal = list.stream().filter(l -> PatrolConstant.TASK_ABNORMAL.equals(l.getAbnormalState())).count();
        long omit = 0L;
        String omitRate = String.format("%.2f", 0F);

        List<Date> startList = this.getOmitDateScope(startDate);
        List<Date> endList = this.getOmitDateScope(endDate);
        Date startTime = startList.stream().min(Comparator.comparingLong(Date::getTime)).get();
        Date endTime = endList.stream().max(Comparator.comparingLong(Date::getTime)).get();
        // 漏检任务列表
//        List<PatrolTask> omitList = patrolTaskService.lambdaQuery().eq(PatrolTask::getDelFlag, 0)
//                .between(PatrolTask::getPatrolDate, startTime, endTime).list();
        List<PatrolTask> omitList = patrolTaskMapper.getOverviewInfo(startTime, endTime, departList);

        // 漏检时间范围内的任务总数
        long omitScopeSum = omitList.size();
        omit += omitList.stream().filter(l -> PatrolConstant.OMIT_STATUS.equals(l.getOmitStatus())).count();

        // 漏检率精确到小数点后两位，需要四舍五入
        if (omit != 0 && omitScopeSum != 0) {
            // 漏检率=漏检数除以总数X100%
            double rate = (1.0 * omit / omitScopeSum) * 100;
            omitRate = String.format("%.2f", rate);
        }
        situation.setSum(sum);
        situation.setFinish(finish);
        situation.setUnfinish(unfinish);
        situation.setAbnormal(abnormal);
        situation.setOmit(omit);
        situation.setOmitRate(omitRate);
        return situation;
    }

    /**
     * 如果参数日期是周一至周四，则返回上周五00时00分00秒和周日23时59分59秒，否则返回周一00时00分00秒和周四23时59分59秒
     *
     * @param date
     * @return
     */
    public List<Date> getOmitDateScope(Date date) {
        // 参数日期所在周的周一
        Date monday = DateUtils.getWeekStartTime(date);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = monday.toInstant().atZone(zoneId).toLocalDate();
        if (Calendar.FRIDAY == DateUtil.dayOfWeek(date) || Calendar.SATURDAY == DateUtil.dayOfWeek(date)
                || Calendar.SUNDAY == DateUtil.dayOfWeek(date)) {
            // 周一往后3天，星期四
            Date thursday = Date.from(localDate.plusDays(3).atStartOfDay().atZone(zoneId).toInstant());
            return Arrays.asList(DateUtil.parse(DateUtil.format(monday, "yyyy-MM-dd 00:00:00")),
                    DateUtil.parse(DateUtil.format(thursday, "yyyy-MM-dd 23:59:59")));
        } else {
            // 周一往前3天，星期五
            Date friday = Date.from(localDate.minusDays(3).atStartOfDay().atZone(zoneId).toInstant());
            // 周一往前1天，星期天
            Date sunday = Date.from(localDate.minusDays(1).atStartOfDay().atZone(zoneId).toInstant());
            return Arrays.asList(DateUtil.parse(DateUtil.format(friday, "yyyy-MM-dd 00:00:00")),
                    DateUtil.parse(DateUtil.format(sunday, "yyyy-MM-dd 23:59:59")));
        }
    }

    /**
     * 首页巡视的列表
     *
     * @param page
     * @param patrolCondition
     * @return
     */
    public IPage<PatrolIndexTask> getIndexPatrolList(Page<PatrolIndexTask> page, PatrolCondition patrolCondition) {
        Integer omitStatus = patrolCondition.getOmitStatus();
        if (ObjectUtil.isNotEmpty(omitStatus) && PatrolConstant.OMIT_STATUS.equals(omitStatus)) {
            Date startDate = this.getOmitDateScope(patrolCondition.getStartDate()).stream().min(Comparator.comparingLong(Date::getTime)).get();
            Date endDate = this.getOmitDateScope(patrolCondition.getEndDate()).stream().max(Comparator.comparingLong(Date::getTime)).get();
            patrolCondition.setStartDate(startDate);
            patrolCondition.setEndDate(endDate);
        } else {
            patrolCondition.setOmitStatus(null);
        }

        // 任务为已完成状态的正则
        String regexp = "^" + PatrolConstant.TASK_COMPLETE + "{1}$";

        IPage<PatrolIndexTask> pageList = null;
        if (ObjectUtil.isNotEmpty(patrolCondition.getIsAllData()) && ALLDATA.equals(patrolCondition.getIsAllData())) {
            pageList = patrolTaskMapper.getIndexPatrolList(page, patrolCondition, regexp, null);
        } else {
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (ObjectUtil.isEmpty(loginUser)) {
                throw new AiurtBootException("检测到暂未登录，请登录系统后操作！");
            }
            List<CsUserDepartModel> departList = sysBaseApi.getDepartByUserId(loginUser.getId());
            pageList = patrolTaskMapper.getIndexPatrolList(page, patrolCondition, regexp, departList);
        }

        // 巡视任务集
        Set<String> taskCodeSet = new HashSet<>();
        pageList.getRecords().stream().forEach(l -> {
            if (StrUtil.isNotEmpty(l.getTaskCode())) {
                taskCodeSet.addAll(Arrays.asList(l.getTaskCode().split(",")));
            }
        });

        // 任务下的巡视人员
        Map<String, Set<String>> userMap = new HashMap<>();
        // 巡视人员对应的组织机构
        Map<String, Set<String>> orgMap = new HashMap<>();
        taskCodeSet.stream().forEach(code -> {
            LambdaQueryWrapper<PatrolTaskUser> userWrapper = Wrappers.<PatrolTaskUser>lambdaQuery()
                    .select(PatrolTaskUser::getUserId, PatrolTaskUser::getUserName)
                    .eq(PatrolTaskUser::getDelFlag, 0)
                    .eq(PatrolTaskUser::getTaskCode, code);
            List<PatrolTaskUser> patrolTaskUsers = patrolTaskUserMapper.selectList(userWrapper);
            List<String> userId = patrolTaskUsers.stream().map(PatrolTaskUser::getUserId).distinct().collect(Collectors.toList());
            List<String> username = patrolTaskUsers.stream().map(PatrolTaskUser::getUserName).distinct().collect(Collectors.toList());
            // 用户名称
            if (CollectionUtil.isNotEmpty(username)) {
                userMap.put(code, new HashSet<>(username));
            }
            // 组织机构名称
            if (CollectionUtil.isNotEmpty(userId)) {
                List<String> deptName = patrolTaskUserMapper.getDeptName(userId);
                if (CollectionUtil.isNotEmpty(deptName)) {
                    orgMap.put(code, new HashSet<>(deptName));
                }
            }
        });

        pageList.getRecords().stream().forEach(l -> {
            List<String> taskCodeList = StrUtil.splitTrim(l.getTaskCode(), ',');
            // 任务状态翻译，0未完成，1已完成
            Integer status = l.getStatus();
            if (ObjectUtil.isNotEmpty(status)) {
                if (Integer.valueOf(1).equals(status)) {
                    l.setStatusName("已完成");
                } else {
                    l.setStatusName("未完成");
                }
            }

            // 任务下的巡视人员
            Set<String> userSet = new HashSet<>();
            // 巡视人员对应的组织机构
            Set<String> orgSet = new HashSet<>();
            for (String taskCode : taskCodeList) {
                Set<String> userInfo = userMap.get(taskCode);
                Set<String> orgInfo = orgMap.get(taskCode);
                if (CollectionUtil.isNotEmpty(userInfo)) {
                    userSet.addAll(userInfo);
                }
                if (CollectionUtil.isNotEmpty(orgInfo)) {
                    orgSet.addAll(orgInfo);
                }
            }

            String userInfo = userSet.stream().collect(Collectors.joining("；"));
            String orgInfo = orgSet.stream().collect(Collectors.joining("；"));
            l.setUserInfo(userInfo);
            l.setOrgInfo(orgInfo);
        });
        return pageList;
    }

    /**
     * 首页巡视列表下的任务列表
     *
     * @param page
     * @param indexTaskDTO
     * @return
     */
    public IPage<IndexTaskInfo> getIndexTaskList(Page<IndexTaskInfo> page, IndexTaskDTO indexTaskDTO) {

        if (!PatrolConstant.OMIT_STATUS.equals(indexTaskDTO.getOmitStatus())) {
            indexTaskDTO.setOmitStatus(null);
        }

        IPage<IndexTaskInfo> pageList = null;
        if (ObjectUtil.isNotEmpty(indexTaskDTO.getIsAllData()) && ALLDATA.equals(indexTaskDTO.getIsAllData())) {
            pageList = patrolTaskMapper.getIndexTaskList(page, indexTaskDTO, null);
        } else {
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (ObjectUtil.isEmpty(loginUser)) {
                throw new AiurtBootException("检测到暂未登录，请登录系统后操作！");
            }
            List<CsUserDepartModel> departList = sysBaseApi.getDepartByUserId(loginUser.getId());
            pageList = patrolTaskMapper.getIndexTaskList(page, indexTaskDTO, departList);
        }
        pageList.getRecords().stream().forEach(l -> {
            String taskCode = l.getCode();
            // 巡视用户信息
            QueryWrapper<PatrolTaskUser> userWrapper = new QueryWrapper<>();
            userWrapper.lambda().eq(PatrolTaskUser::getTaskCode, taskCode).eq(PatrolTaskUser::getDelFlag, 0);
            List<PatrolTaskUser> userList = patrolTaskUserMapper.selectList(userWrapper);
            ArrayList<IndexUserDTO> indexUsers = new ArrayList<>();
            userList.stream().forEach(u -> {
                IndexUserDTO user = new IndexUserDTO();
                user.setUserId(u.getUserId());
                user.setUserName(u.getUserName());
                if (StrUtil.isEmpty(u.getUserName())) {
                    String username = patrolTaskUserMapper.getUsername(u.getUserId());
                    user.setUserName(username);
                }
                indexUsers.add(user);
            });

            // 巡视组织机构信息
            List<IndexOrgDTO> orgInfo = patrolTaskOrganizationMapper.getOrgInfo(taskCode);

            // 巡视站点信息
            List<IndexStationDTO> stationInfo = new ArrayList<>();
            String stationCode = indexTaskDTO.getStationCode();
            if (ObjectUtil.isEmpty(stationCode)) {
                stationInfo = patrolTaskStationMapper.getStationInfo(taskCode);
            } else {
                String stationName = patrolTaskDeviceMapper.getStationName(stationCode);
                stationInfo.add(new IndexStationDTO(stationCode, stationName));
            }

            // 字典翻译
            String abnormalDictName = sysBaseApi.getDictItems(PatrolDictCode.ABNORMAL_STATE).stream()
                    .filter(item -> item.getValue().equals(String.valueOf(l.getAbnormalState())))
                    .map(DictModel::getText).collect(Collectors.joining());
            String statusDictName = sysBaseApi.getDictItems(PatrolDictCode.TASK_STATUS).stream()
                    .filter(item -> item.getValue().equals(String.valueOf(l.getStatus())))
                    .map(DictModel::getText).collect(Collectors.joining());

            l.setUserInfo(indexUsers);
            l.setOrgInfo(orgInfo);
            l.setStationInfo(stationInfo);
            l.setAbnormalDictName(abnormalDictName);
            l.setStatusDictName(statusDictName);
        });
        return pageList;
    }

    /**
     * 获取首页的日程的巡检列表
     *
     * @param page
     * @param indexScheduleDTO
     * @return
     */
    public IPage<ScheduleTask> getScheduleList(Page<ScheduleTask> page, IndexScheduleDTO indexScheduleDTO) {
        IPage<ScheduleTask> pageList = null;
        // 默认已完成
        if (ObjectUtil.isEmpty(indexScheduleDTO.getStatus())) {
            indexScheduleDTO.setStatus(PatrolConstant.TASK_COMPLETE);
        }
        if (ObjectUtil.isNotEmpty(indexScheduleDTO.getIsAllData()) && ALLDATA.equals(indexScheduleDTO.getIsAllData())) {
            pageList = patrolTaskMapper.getScheduleList(page, indexScheduleDTO, null);
        } else {
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (ObjectUtil.isEmpty(loginUser)) {
                throw new AiurtBootException("检测到暂未登录，请登录系统后操作！");
            }
            List<CsUserDepartModel> departList = sysBaseApi.getDepartByUserId(loginUser.getId());
            pageList = patrolTaskMapper.getScheduleList(page, indexScheduleDTO, departList);
        }

        pageList.getRecords().stream().forEach(l -> {
            // 字典翻译
            String statusName = sysBaseApi.getDictItems(PatrolDictCode.TASK_STATUS).stream()
                    .filter(item -> item.getValue().equals(String.valueOf(l.getStatus())))
                    .map(DictModel::getText).collect(Collectors.joining());
            l.setStatusName(statusName);
        });
        return pageList;
    }
}
