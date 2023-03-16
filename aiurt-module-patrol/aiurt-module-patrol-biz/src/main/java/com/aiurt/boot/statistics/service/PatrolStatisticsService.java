package com.aiurt.boot.statistics.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.constant.PatrolDictCode;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.boot.statistics.dto.*;
import com.aiurt.boot.statistics.model.*;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskOrganization;
import com.aiurt.boot.task.entity.PatrolTaskStandard;
import com.aiurt.boot.task.entity.PatrolTaskUser;
import com.aiurt.boot.task.mapper.*;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.jeecg.common.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author JB
 * @Description: 首页巡视模块业务层
 */
@Service
public class PatrolStatisticsService {
    @Autowired
    private ISysBaseAPI sysBaseApi;
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
    @Autowired
    private PatrolTaskStandardMapper patrolTaskStandardMapper;
    @Autowired
    private ISysParamAPI sysParamApi;
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
//        List<CsUserDepartModel> departList = null;
//        if (ObjectUtil.isEmpty(isAllData) || !ALLDATA.equals(isAllData)) {
//            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//            if (ObjectUtil.isEmpty(loginUser)) {
//                throw new AiurtBootException("检测到暂未登录，请登录系统后操作！");
//            }
//            departList = sysBaseApi.getDepartByUserId(loginUser.getId());
//        }
        boolean b = GlobalThreadLocal.setDataFilter(false);
        List<PatrolTaskStandard> standards = patrolTaskStandardMapper.selectList(new LambdaQueryWrapper<PatrolTaskStandard>().eq(PatrolTaskStandard::getDelFlag,CommonConstant.DEL_FLAG_0));
        GlobalThreadLocal.setDataFilter(b);
        List<PatrolTaskOrganization> departList = patrolTaskOrganizationMapper.selectList(new LambdaQueryWrapper<PatrolTaskOrganization>().eq(PatrolTaskOrganization::getDelFlag, CommonConstant.DEL_FLAG_0));
        boolean openClose = GlobalThreadLocal.setDataFilter(false);
        List<PatrolTask> list = patrolTaskMapper.getOverviewInfo(newStartDate, newEndDate, departList,standards);
        long sum = list.stream().count();
        long finish = list.stream().filter(l -> PatrolConstant.TASK_COMPLETE.equals(l.getStatus())).count();
        long unfinish = sum - finish;
        long abnormal = list.stream().filter(l -> PatrolConstant.TASK_ABNORMAL.equals(l.getAbnormalState())).count();
        long overhaul = list.stream().filter(l -> !PatrolConstant.TASK_COMPLETE.equals(l.getStatus())&&!PatrolConstant.TASK_INIT.equals(l.getStatus())).count();
        long omit = 0L;
        String omitRate = String.format("%.2f", 0F);

        List<Date> startList = this.getOmitDateScope(startDate);
        List<Date> endList = this.getOmitDateScope(endDate);
        Date startTime = startList.stream().min(Comparator.comparingLong(Date::getTime)).get();
        Date endTime = endList.stream().max(Comparator.comparingLong(Date::getTime)).get();
////         漏检任务列表
//        List<PatrolTask> omitList = patrolTaskService.lambdaQuery().eq(PatrolTask::getDelFlag, 0)
//                .between(PatrolTask::getPatrolDate, startTime, endTime).list();
        List<PatrolTask> omitList = patrolTaskMapper.getOverviewInfo(startTime, endTime, departList,standards);
        GlobalThreadLocal.setDataFilter(openClose);
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
        situation.setOverhaul(overhaul);
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
        SysParamModel sysParamModel = sysParamApi.selectByCode(SysParamCodeConstant.PATROL_WEEKDAYS);
        String value = sysParamModel.getValue();
        String[] split = StrUtil.split( value,",");
        List<Date> patrolList = new ArrayList();
        List<Integer> weekList = new ArrayList();
        //传入时间一周数据,当前日期所在周
        Date format = DateUtils.getWeekStartTime(date);
        DateTime monday = DateUtil.parse(DateUtil.format(format, "yyyy-MM-dd 00:00:00"));
        DateTime tuesDay = DateUtil.offsetDay(monday, 1);
        DateTime wedDay = DateUtil.offsetDay(monday, 2);
        DateTime thDay = DateUtil.offsetDay(monday, 3);
        DateTime friDay = DateUtil.offsetDay(monday, 4);
        DateTime saDay = DateUtil.offsetDay(monday, 5);
        DateTime sunDay = DateUtil.offsetDay(monday, 6);
        //参数日期所在周漏检日期
        for (String s : split) {
            if(("1").equals(s)){
                patrolList.add(monday);
            }
            if(("2").equals(s)){
                patrolList.add(tuesDay);
            }
            if(("3").equals(s)){
                patrolList.add(wedDay);
            }
            if(("4").equals(s)){
                patrolList.add(thDay);
            }
            if(("5").equals(s)){
                patrolList.add(friDay);
            }
            if(("6").equals(s)){
                patrolList.add(saDay);
            }
            if(("7").equals(s)){
                patrolList.add(sunDay);
            }
        }
        //漏检开始和结束时间
        Date firstDate = patrolList.stream().min(Comparator.comparingLong(Date::getTime)).get();
        Date secondDate =patrolList.stream().max(Comparator.comparingLong(Date::getTime)).get();
        long betweenDay = DateUtil.between(firstDate, secondDate, DateUnit.DAY);

        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = firstDate.toInstant().atZone(zoneId).toLocalDate();
        if( date.after(firstDate) && date.before(secondDate)){
            // 第一次漏检往前推两次漏检间隔天数
            Date start = Date.from(localDate.minusDays(7-betweenDay).atStartOfDay().atZone(zoneId).toInstant());
            // 第一次漏检往前推1天
            Date end = Date.from(localDate.minusDays(1).atStartOfDay().atZone(zoneId).toInstant());
            return Arrays.asList(DateUtil.parse(DateUtil.format(start, "yyyy-MM-dd 00:00:00")),
                    DateUtil.parse(DateUtil.format(end, "yyyy-MM-dd 23:59:59")));
        }else{
            if(date.before(firstDate)){
                Date start = Date.from(localDate.minusDays(7).atStartOfDay().atZone(zoneId).toInstant());
                // 第一次漏检往前1天
                Date end = Date.from(localDate.minusDays(7-betweenDay).atStartOfDay().atZone(zoneId).toInstant());
                return Arrays.asList(DateUtil.parse(DateUtil.format(start, "yyyy-MM-dd 00:00:00")),
                        DateUtil.parse(DateUtil.format(end, "yyyy-MM-dd 23:59:59")));
            }else{
                // 第一次漏检往后推两次检修间隔天数
                secondDate = Date.from(localDate.plusDays(betweenDay).atStartOfDay().atZone(zoneId).toInstant());
                return Arrays.asList(DateUtil.parse(DateUtil.format(firstDate, "yyyy-MM-dd 00:00:00")),
                        DateUtil.parse(DateUtil.format(secondDate, "yyyy-MM-dd 23:59:59")));
            }

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
            pageList = patrolTaskMapper.getIndexPatrolList(page, patrolCondition, regexp, null,null);
        } else {
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (ObjectUtil.isEmpty(loginUser)) {
                throw new AiurtBootException("检测到暂未登录，请登录系统后操作！");
            }
            List<PatrolTaskStandard> standards = patrolTaskStandardMapper.selectList(new LambdaQueryWrapper<PatrolTaskStandard>().eq(PatrolTaskStandard::getDelFlag,CommonConstant.DEL_FLAG_0));
            List<PatrolTaskOrganization> departList = patrolTaskOrganizationMapper.selectList(new LambdaQueryWrapper<PatrolTaskOrganization>().eq(PatrolTaskOrganization::getDelFlag, CommonConstant.DEL_FLAG_0));
            //下面禁用数据权限
            boolean b= GlobalThreadLocal.setDataFilter(false);
            pageList = patrolTaskMapper.getIndexPatrolList(page, patrolCondition, regexp, departList,standards);
            GlobalThreadLocal.setDataFilter(b);
        }

        // 巡视任务集
        Set<String> taskCodeSet = new HashSet<>();
        pageList.getRecords().stream().forEach(l -> {
            if (StrUtil.isNotEmpty(l.getTaskCode())) {
                taskCodeSet.addAll(Arrays.asList(l.getTaskCode().split(",")));
            }
        });

        // 任务下的巡视人员
        Map<String, Set<String>> userMap = new HashMap<>(16);
        // 巡视人员对应的组织机构
        Map<String, Set<String>> orgMap = new HashMap<>(16);
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

        Integer omitStatus = indexTaskDTO.getOmitStatus();
        if (ObjectUtil.isNotEmpty(omitStatus) && PatrolConstant.OMIT_STATUS.equals(omitStatus)) {
            Date startDate = this.getOmitDateScope(indexTaskDTO.getStartDate()).stream().min(Comparator.comparingLong(Date::getTime)).get();
            Date endDate = this.getOmitDateScope(indexTaskDTO.getEndDate()).stream().max(Comparator.comparingLong(Date::getTime)).get();
            indexTaskDTO.setStartDate(startDate);
            indexTaskDTO.setEndDate(endDate);
        } else {
            indexTaskDTO.setOmitStatus(null);
        }

        IPage<IndexTaskInfo> pageList = null;
        if (ObjectUtil.isNotEmpty(indexTaskDTO.getIsAllData()) && ALLDATA.equals(indexTaskDTO.getIsAllData())) {
            pageList = patrolTaskMapper.getIndexTaskList(page, indexTaskDTO, null,null);
        } else {
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (ObjectUtil.isEmpty(loginUser)) {
                throw new AiurtBootException("检测到暂未登录，请登录系统后操作！");
            }
            List<PatrolTaskStandard> standards = patrolTaskStandardMapper.selectList(new LambdaQueryWrapper<PatrolTaskStandard>().eq(PatrolTaskStandard::getDelFlag,CommonConstant.DEL_FLAG_0));
            List<PatrolTaskOrganization> departList = patrolTaskOrganizationMapper.selectList(new LambdaQueryWrapper<PatrolTaskOrganization>().eq(PatrolTaskOrganization::getDelFlag, CommonConstant.DEL_FLAG_0));
            //下面禁用数据权限
            boolean b= GlobalThreadLocal.setDataFilter(false);
            pageList = patrolTaskMapper.getIndexTaskList(page, indexTaskDTO, departList,standards);
            GlobalThreadLocal.setDataFilter(b);

        }
        boolean b1= GlobalThreadLocal.setDataFilter(false);
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
         GlobalThreadLocal.setDataFilter(b1);
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
//            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//            if (ObjectUtil.isEmpty(loginUser)) {
//                throw new AiurtBootException("检测到暂未登录，请登录系统后操作！");
//            }
//            List<CsUserDepartModel> departList = sysBaseApi.getDepartByUserId(loginUser.getId());
            //数据权限
            List<PatrolTaskOrganization> patrolTaskOrganizations = patrolTaskOrganizationMapper.selectList(new LambdaQueryWrapper<PatrolTaskOrganization>().eq(PatrolTaskOrganization::getDelFlag, CommonConstant.DEL_FLAG_0));
            pageList = patrolTaskMapper.getScheduleList(page, indexScheduleDTO,patrolTaskOrganizations);
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
