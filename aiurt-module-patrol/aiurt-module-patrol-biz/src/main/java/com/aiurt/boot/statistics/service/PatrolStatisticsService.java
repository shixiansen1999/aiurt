package com.aiurt.boot.statistics.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.api.PatrolApi;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.statistics.dto.*;
import com.aiurt.boot.statistics.model.*;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskUser;
import com.aiurt.boot.task.mapper.*;
import com.aiurt.boot.task.param.PatrolTaskParam;
import com.aiurt.boot.task.service.IPatrolTaskService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
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
    @Autowired
    private PatrolApi patrolApi;

    /**
     * 首页巡视概况
     *
     * @return
     */
    public PatrolSituation getOverviewInfo(Date startDate, Date endDate) {
        Date newStartDate = DateUtil.parse(DateUtil.format(startDate, "yyyy-MM-dd 00:00:00"));
        Date newEndDate = DateUtil.parse(DateUtil.format(endDate, "yyyy-MM-dd 23:59:59"));
        PatrolSituation situation = new PatrolSituation();
        List<PatrolTask> list = patrolTaskService.lambdaQuery().eq(PatrolTask::getDelFlag, 0)
                // 过滤手工下发
//                .and(i -> i.ne(PatrolTask::getSource, PatrolConstant.TASK_MANUAL).or().isNull(PatrolTask::getSource))
                .between(PatrolTask::getPatrolDate, newStartDate, newEndDate).list();
        long sum = list.stream().count();
        long finish = list.stream().filter(l -> PatrolConstant.TASK_COMPLETE.equals(l.getStatus())).count();
        long unfinish = sum - finish;
        long abnormal = list.stream().filter(l -> PatrolConstant.TASK_ABNORMAL.equals(l.getAbnormalState())).count();
        long omit = this.getOmitData(startDate);
        String omitRate = String.format("%.2f", 0F);

        // 日期是周五-周日统计的是周一至周四的漏检总数，是周一至周四统计的是周五至周日的漏检总数
        omit += list.stream().filter(l -> PatrolConstant.OMIT_STATUS.equals(l.getOmitStatus())).count();
//        omit += this.getOmitData(endDate);
        // 漏检率精确到小数点后两位，需要四舍五入
        if (omit != 0 && sum != 0) {
            // 漏检率=漏检数除以总数X100%
            double rate = (1.0 * omit / sum) * 100;
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
     * 如果参数日期是周一到周四，则获取上周五至周日的漏检数据，如果是周五到周日，则获取周一至周四的漏检数据
     *
     * @param date
     * @return
     */
    public long getOmitData(Date date) {
        long omit = 0L;
        // 参数日期所在周的周一
        Date monday = DateUtils.getWeekStartTime(date);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = monday.toInstant().atZone(zoneId).toLocalDate();
        if (Calendar.FRIDAY == DateUtil.dayOfWeek(date) || Calendar.SATURDAY == DateUtil.dayOfWeek(date)
                || Calendar.SUNDAY == DateUtil.dayOfWeek(date)) {
            // 周一往后3天，星期四
            Date thursday = Date.from(localDate.plusDays(3).atStartOfDay().atZone(zoneId).toInstant());
            omit = patrolTaskService.lambdaQuery().eq(PatrolTask::getDelFlag, 0)
                    .eq(PatrolTask::getOmitStatus, PatrolConstant.OMIT_STATUS)
                    .between(PatrolTask::getPatrolDate, monday, thursday)
                    .count();
        } else {
            // 周一往前3天，星期五
            Date friday = Date.from(localDate.minusDays(3).atStartOfDay().atZone(zoneId).toInstant());
            // 周一往前1天，星期天
            Date sunday = Date.from(localDate.minusDays(1).atStartOfDay().atZone(zoneId).toInstant());
            omit = patrolTaskService.lambdaQuery().eq(PatrolTask::getDelFlag, 0)
                    .eq(PatrolTask::getOmitStatus, PatrolConstant.OMIT_STATUS)
                    .between(PatrolTask::getPatrolDate, friday, sunday)
                    .count();
        }
        return omit;
    }

    /**
     * 首页巡视的列表
     *
     * @param page
     * @param patrolCondition
     * @return
     */
    public IPage<PatrolIndexTask> getIndexPatrolList(Page<PatrolIndexTask> page, PatrolCondition patrolCondition) {
//        Integer finishStatus = patrolCondition.getFinishStatus();
//        if (ObjectUtil.isNotEmpty(finishStatus)) {
//            List<String> stationCodes = patrolTaskMapper.getStationCodeUnfinish(patrolCondition.getStartDate(), patrolCondition.getEndDate(), finishStatus);
//            if (CollectionUtil.isEmpty(stationCodes)) {
//                return new Page<>(page.getCurrent(), page.getSize());
//            }
//            patrolCondition.setCodeList(stationCodes);
//        }

        // todo 检验数据正确性的集合,验证正确可删除
//        Set<String> set = new HashSet<>();

        // 任务为已完成状态的正则
        String regexp = "^" + PatrolConstant.TASK_COMPLETE + "{1}$";

        IPage<PatrolIndexTask> pageList = patrolTaskMapper.getIndexPatrolList(page, patrolCondition, regexp);
        pageList.getRecords().stream().forEach(l -> {
            List<String> taskCodeList = new ArrayList<>();
            if (StrUtil.isNotEmpty(l.getTaskCode())) {
                taskCodeList = Arrays.asList(l.getTaskCode().split(","));
            }

            // 任务状态翻译，0未完成，1已完成
            Integer status = l.getStatus();
            if (ObjectUtil.isNotEmpty(status)) {
                if (Integer.valueOf(1).equals(status)) {
                    l.setStatusName("已完成");
                } else {
                    l.setStatusName("未完成");
                }
            }


            // todo 检验数据正确性的集合,验证正确可删除
//            set.addAll(taskCodeList);

            // 任务下的巡视人员
            Set<String> userSet = new HashSet<>();
            // 巡视人员对应的组织机构
            Set<String> orgSet = new HashSet<>();

            taskCodeList.stream().forEach(taskCode -> {
                QueryWrapper<PatrolTaskUser> userWrapper = new QueryWrapper<>();
                userWrapper.lambda().eq(PatrolTaskUser::getDelFlag, 0).eq(PatrolTaskUser::getTaskCode, taskCode);
                List<PatrolTaskUser> patrolTaskUsers = patrolTaskUserMapper.selectList(userWrapper);
                List<String> userId = patrolTaskUsers.stream().map(PatrolTaskUser::getUserId).distinct().collect(Collectors.toList());
                List<String> username = patrolTaskUsers.stream().map(PatrolTaskUser::getUserName).distinct().collect(Collectors.toList());
//                userId.stream().forEach(uid -> {
//                    String deptName = patrolTaskUserMapper.getDeptName(uid);
//                    orgSet.add(deptName);
//                });
                List<String> deptName = patrolTaskUserMapper.getDeptName(userId);

                userSet.addAll(username);
                orgSet.addAll(deptName);
            });

            // 获取站点下的任务

            String userInfo = userSet.stream().collect(Collectors.joining("；"));
            String orgInfo = orgSet.stream().collect(Collectors.joining("；"));

            l.setUserInfo(userInfo);
            l.setOrgInfo(orgInfo);
        });

        // todo 检验数据正确性的集合,验证正确可删除
//        System.out.println(set.size());

        return pageList;
    }

    /**
     * 首页巡视列表下的任务列表
     *
     * @param page
     * @param abnormalDTO
     * @return
     */
    public IPage<IndexTaskInfo> getIndexTaskList(Page<IndexTaskInfo> page, IndexTaskDTO abnormalDTO) {
        IPage<IndexTaskInfo> pageList = patrolTaskMapper.getIndexTaskList(page, abnormalDTO);
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
            String stationCode = abnormalDTO.getStationCode();
            if (ObjectUtil.isEmpty(stationCode)) {
                stationInfo = patrolTaskStationMapper.getStationInfo(taskCode);
            } else {
                String stationName = patrolTaskDeviceMapper.getStationName(stationCode);
                stationInfo.add(new IndexStationDTO(stationCode, stationName));
            }

            // 字典翻译
            String abnormalDictName = sysBaseApi.getDictItems("patrol_abnormal_state").stream()
                    .filter(item -> item.getValue().equals(String.valueOf(l.getStatus())))
                    .map(DictModel::getText).collect(Collectors.joining());
            String statusDictName = sysBaseApi.getDictItems("patrol_task_status").stream()
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
        IPage<ScheduleTask> pageList = patrolTaskMapper.getScheduleList(page, indexScheduleDTO);
        return pageList;
    }
}
