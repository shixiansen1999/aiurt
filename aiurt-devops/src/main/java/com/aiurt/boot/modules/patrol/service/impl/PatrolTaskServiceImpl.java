package com.aiurt.boot.modules.patrol.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.exception.SwscException;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.common.util.DateUtils;
import com.aiurt.boot.common.util.PageUtils;
import com.aiurt.boot.common.util.RoleAdditionalUtils;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.manage.service.ISubsystemService;
import com.aiurt.boot.modules.patrol.constant.PatrolConstant;
import com.aiurt.boot.modules.patrol.entity.Patrol;
import com.aiurt.boot.modules.patrol.entity.PatrolContent;
import com.aiurt.boot.modules.patrol.entity.PatrolPool;
import com.aiurt.boot.modules.patrol.entity.PatrolTask;
import com.aiurt.boot.modules.patrol.mapper.PatrolPoolMapper;
import com.aiurt.boot.modules.patrol.mapper.PatrolTaskMapper;
import com.aiurt.boot.modules.patrol.param.*;
import com.aiurt.boot.modules.patrol.service.*;
import com.aiurt.boot.modules.patrol.utils.NumberGenerateUtils;
import com.aiurt.boot.modules.patrol.vo.PatrolPoolContentOneTreeVO;
import com.aiurt.boot.modules.patrol.vo.PatrolPoolContentTreeVO;
import com.aiurt.boot.modules.patrol.vo.PatrolTaskVO;
import com.aiurt.boot.modules.patrol.vo.TaskDetailVO;
import com.aiurt.boot.modules.patrol.vo.export.ExportTaskSubmitVO;
import com.aiurt.boot.modules.patrol.vo.statistics.AppStationPatrolStatisticsVO;
import com.aiurt.boot.modules.statistical.vo.StatisticsVO;
import com.aiurt.boot.modules.system.entity.SysDepart;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.service.ISysDepartService;
import com.aiurt.boot.modules.system.service.ISysUserService;
import com.aiurt.boot.modules.system.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 巡检人员任务
 * @Author: swsc
 * @Date: 2021-09-17
 * @Version: V1.0
 */
@Service
@RequiredArgsConstructor
public class PatrolTaskServiceImpl extends ServiceImpl<PatrolTaskMapper, PatrolTask> implements IPatrolTaskService {

    private final IPatrolService patrolService;

    private final IPatrolContentService patrolContentService;

    private final IPatrolPoolService patrolPoolService;

    private final IPatrolTaskReportService patrolTaskReportService;

    private final ISysUserService sysUserService;

    private final ISysDepartService sysDepartService;

    private final NumberGenerateUtils numberGenerateUtils;

    private final IPatrolPoolContentService patrolPoolContentService;

    private final IStationService stationService;

    private final ISubsystemService subsystemService;

    private final RoleAdditionalUtils roleAdditionalUtils;

    private final PatrolPoolMapper patrolPoolMapper;


    @Override
    public Result<IPage<PatrolTaskVO>> pageList(PatrolPoolParam param) {

        IPage<PatrolTaskVO> page = new Page<>(param.getPageNo(), param.getPageSize());

        //权限设置
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = user.getId();
        if (StringUtils.isBlank(param.getOrganizationId())) {
            List<String> ids = roleAdditionalUtils.getListDepartIdsByUserId(userId);
            if (CollectionUtils.isNotEmpty(ids)) {
                param.setDepartList(ids);
            }
        }
        if (StringUtils.isBlank(param.getSystemTypeName())) {
            List<String> ids = roleAdditionalUtils.getListSystemCodesByUserId(userId);
            if (CollectionUtils.isNotEmpty(ids)) {
                param.setSystemCodes(ids);
            }
            //this.count();
        }


        if (param.getStationId() == null && param.getLineId() != null) {
            List<Station> list = stationService.lambdaQuery()
                    .eq(Station::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .eq(Station::getLineCode, param.getLineId())
                    .select(Station::getId).list();
            if (CollectionUtils.isNotEmpty(list)) {
                param.setStationIds(list.stream().map(Station::getId).collect(Collectors.toList()));
            } else {
                return Result.ok(page);
            }
        }

        //查询type传0非手动下发
        page = this.baseMapper.selectPageList(page, param);

        for (PatrolTaskVO record : page.getRecords()) {
            //漏检状态处理
            if (record != null && record.getIgnoreStatus() != null && record.getIgnoreStatus() == 1) {
                if (StringUtils.isBlank(record.getIgnoreContent())) {
                    //若为空则未处理
                    record.setIgnoreStatus(0);
                }
            }
        }

        return Result.ok(page);
    }

    @Override
    public Result<IPage<PatrolTaskVO>> appPage(HttpServletRequest req, PatrolPoolParam param) {
        IPage<PatrolTaskVO> page = PageUtils.getPage(PatrolTaskVO.class, param);
        //权限设置
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = user.getId();

        if (StringUtils.isBlank(param.getSystemTypeName())) {
            List<String> ids = roleAdditionalUtils.getListSystemCodesByUserId(userId);
            if (CollectionUtils.isNotEmpty(ids)) {
                //return Result.ok(page);
                param.setSystemCodes(ids);
            }
        }


        if (param.getOrganizationId() == null) {
            //获取组id
            if (StringUtils.isNotBlank(user.getOrgId())) {
                param.setOrganizationId(user.getOrgId());
            }
        }

        //查询type传0非手动下发
        page = this.baseMapper.selectAppPage(page, param);

        for (PatrolTaskVO record : page.getRecords()) {
            if (record == null) {
                continue;
            }
            //用户名称
            if (StringUtils.isNotBlank(record.getStaffIds())) {
                String[] userIds = record.getStaffIds().trim().split(PatrolConstant.SPL);
                List<SysUser> sysUsers = sysUserService.lambdaQuery()
                        .eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
                        .in(SysUser::getId, Arrays.asList(userIds)).list();
                if (sysUsers != null && sysUsers.size() > 0) {
                    record.setStaffName(org.apache.commons.lang.StringUtils.join(
                            sysUsers.stream().map(SysUser::getRealname).collect(Collectors.toList()),
                            PatrolConstant.SPL));
                }
            }

            //漏检状态处理
            if (record.getIgnoreStatus() != null && record.getIgnoreStatus() == 1) {
                //漏检
                record.setPatrolFlag(4);
            } else {
                //
                if (record.getTaskStatus() != null) {
                    if (record.getTaskStatus() == 1) {
                        //已完成
                        record.setPatrolFlag(3);
                    } else {
                        //巡检中
                        record.setPatrolFlag(2);
                    }
                } else {
                    //未指派
                    record.setPatrolFlag(0);
                }
            }
        }


        return Result.ok(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<?> manuallyAddTasks(HttpServletRequest req, TaskAddParam param) {

        Date date = new Date();
        //班组id
        List<String> ids = param.getOrganizationIds();
        if (CollectionUtils.isEmpty(ids)) {
            throw new SwscException("站点不能为空");
        }

        List<Patrol> patrolList = patrolService.lambdaQuery()
                .eq(Patrol::getDelFlag, CommonConstant.DEL_FLAG_0)
                .in(Patrol::getId, param.getPatrolIds()).list();
        //巡检标准map
        Map<Long, Patrol> patrolMap = patrolList.stream().collect(Collectors.toMap(Patrol::getId, l -> l));
        Map<String, List<Patrol>> collect = patrolList.stream().collect(Collectors.groupingBy(Patrol::getTypes));

        List<Subsystem> subsystemList = subsystemService.list(new LambdaQueryWrapper<Subsystem>().eq(Subsystem::getDelFlag, CommonConstant.DEL_FLAG_0)
                .in(Subsystem::getSystemCode, collect.keySet()).select(Subsystem::getSystemCode, Subsystem::getSystemName));
        Map<String, String> systemMap = null;
        if (CollectionUtils.isNotEmpty(subsystemList)) {
            systemMap = subsystemList.stream().collect(Collectors.toMap(Subsystem::getSystemCode, Subsystem::getSystemName));
        }
        if (systemMap == null) {
            return Result.error("未找到系统数据");
        }

        for (Long patrolId : param.getPatrolIds()) {
            Patrol patrol = patrolMap.get(patrolId);
            if (patrol == null) {
                throw new SwscException("未找到巡检标准数据");
            }
            if (patrol.getStatus() == null || patrol.getStatus() == 0) {
                throw new SwscException("此条巡检标准状态为未生效状态,无法下发任务.");
            }
            //树形结构巡检项
            List<PatrolContent> list = patrolContentService.list(new LambdaQueryWrapper<PatrolContent>()
                    .eq(PatrolContent::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .eq(PatrolContent::getRecordId, patrol.getId()));

            if (list == null || list.size() < 1) {
                throw new SwscException("巡检项数据为空,不能手动下发任务");
            }
            for (String id : ids) {
                String before = "X";

                Station station = stationService.getById(id);

                if (station == null || StringUtils.isBlank(station.getTeamId())) {
                    log.error(MessageFormat.format("站点信息错误,站点id:{0},错误对象:{1}", id, station));
                    throw new SwscException("数据保存失败,站点信息错误或无班组管理此站点.");
                }

                before = before.concat(station.getStationCode());

                String codeNo = numberGenerateUtils.getCodeNo(before);
                //巡检池数据
                PatrolPool pool = new PatrolPool();
                //手动下发任务
                pool.setType(1)
                        .setStatus(0)
                        .setCounts(1)
                        .setDelFlag(CommonConstant.DEL_FLAG_0)
                        .setTactics(4)
                        .setSystemType(patrol.getTypes())
                        .setSystemTypeName(systemMap.get(patrol.getTypes()))
                        .setPatrolName(patrol.getTitle())
                        .setCode(codeNo)
                        //执行时间
                        .setExecutionTime(param.getTime() != null ? param.getTime().toLocalDate().atTime(23, 59, 59) : param.getTime())
                        .setCreateTime(date)
                        .setUpdateTime(date)
                        .setNote(param.getNote())
                        .setOrganizationId(station.getTeamId())
                        .setLineId(station.getId())
                        .setLineName(station.getStationName())
                ;

                if (!patrolPoolService.save(pool)) {
                    throw new SwscException("巡检数据保存失败");
                }

                boolean b = patrolPoolContentService.copyContent(list, pool.getId());
                if (!b) {
                    throw new SwscException("巡检项数据保存失败");
                }

            }
        }

        return Result.ok();
    }


    @Override
    public Result<?> detail(HttpServletRequest req, PatrolTaskDetailParam param) {

        Long id = param.getId();
        String code = param.getCode();
        Long poolId = param.getPoolId();

        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = user.getId();
        if (id == null && StringUtils.isBlank(code) && param.getPoolId() == null) {
            return Result.error("id与code不能同时为空");
        }

        PatrolTask patrolTask = null;
        if (id != null) {
            patrolTask = this.getById(id);
        } else if (param.getPoolId() != null) {
            patrolTask = this.lambdaQuery()
                    .eq(PatrolTask::getPatrolPoolId, poolId)
                    .eq(PatrolTask::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .last("limit 1").one();
        } else {
            patrolTask = this.lambdaQuery()
                    .eq(PatrolTask::getCode, code.trim())
                    .eq(PatrolTask::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .last("limit 1").one();
        }

        //返回vo
        TaskDetailVO vo = new TaskDetailVO();

        vo.setTaskStatus(patrolTask != null ? patrolTask.getStatus() : 0);
        if (patrolTask != null) {
            vo.setSpotTestUser(patrolTask.getSpotTestUser());
            vo.setSpotTest(patrolTask.getSpotTest());
            vo.setSpotTestTechnician(patrolTask.getSpotTestTechnician());
            vo.setSpotTestTechnicianId(patrolTask.getSpotTestTechnicianId());
        }
        PatrolPool pool = null;
        if (poolId != null) {
            pool = patrolPoolService.getById(poolId);
        } else {
            pool = patrolPoolService.getById(patrolTask.getPatrolPoolId());
        }

        if (patrolTask != null) {
            //任务表id
            vo.setTaskId(patrolTask.getId());
        }
        if (pool.getStatus() == 1 && patrolTask != null
                && (patrolTask.getStatus() == null || patrolTask.getStatus() == 0)
                && (patrolTask.getIgnoreStatus() == null
                || patrolTask.getIgnoreStatus() == 0)
                && patrolTask.getStaffIds().contains(userId)) {
            vo.setFlag(1);
        } else {
            vo.setFlag(0);
        }

        //标题
        vo.setTitle(pool.getPatrolName());

        //工单编号
        vo.setCode(pool.getCode());
        if (patrolTask != null) {
            //提交时间
            vo.setSubmitTime(patrolTask.getSubmitTime());

            //巡检人及部门
            if (StringUtils.isNotBlank(patrolTask.getStaffIds())) {
                List<SysUser> sysUsers = sysUserService.lambdaQuery()
                        .eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
                        .in(SysUser::getId, Arrays.asList(patrolTask.getStaffIds().trim().split(PatrolConstant.SPL)))
                        .list();
                if (sysUsers != null && sysUsers.size() > 0) {
                    vo.setStaffName(org.apache.commons.lang.StringUtils.join(
                            sysUsers.stream().map(SysUser::getRealname).collect(Collectors.toList()),
                            PatrolConstant.SPL));
                }
            }

        }

        //树查询参数
        TreeParam treeParam = new TreeParam();
        treeParam.setPoolId(poolId)
                .setTitle(param.getTitle())
                .setFlag(vo.getFlag());

        //树状查询
        Result<List<PatrolPoolContentTreeVO>> tree = patrolTaskReportService.tree(req, id, treeParam);

        List<PatrolPoolContentTreeVO> list = tree.getResult();
        vo.setList(list);

        SysDepart sysDepart = sysDepartService.getById(pool.getOrganizationId());
        if (sysDepart != null) {
            vo.setOrganizationName(sysDepart.getDepartName());
        }

        return Result.ok(vo);
    }

    @Override
    public List<PatrolTaskVO> selectExportListVO(PatrolPoolParam param) {
        List<PatrolTaskVO> list = new ArrayList<>();
        //权限设置
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = user.getId();
        if (StringUtils.isBlank(param.getOrganizationId())) {
            List<String> ids = roleAdditionalUtils.getListDepartIdsByUserId(userId);
            if (CollectionUtils.isNotEmpty(ids)) {
                //return list;
                param.setDepartList(ids);
            }
        }
        if (StringUtils.isBlank(param.getSystemTypeName())) {
            List<String> ids = roleAdditionalUtils.getListSystemCodesByUserId(userId);
            if (CollectionUtils.isNotEmpty(ids)) {
                //return list;
                param.setSystemCodes(ids);
            }
        }
        list = this.baseMapper.selectExportListVO(param);
        return list;
    }

    @Override
    public Result<?> appOneDetail(HttpServletRequest req, OneTreeParam param) {
        PatrolPoolContentOneTreeVO vo = patrolTaskReportService.getOneTree(param);
        return Result.ok(vo);
    }

    @Override
    public Map<String, Integer> getUserNameMap(StatisticsVO statisticsVO) {

        LambdaQueryWrapper<PatrolTask> queryWrapper = new LambdaQueryWrapper<PatrolTask>()
                .eq(PatrolTask::getDelFlag, CommonConstant.DEL_FLAG_0);


        if (StringUtils.isNotBlank(statisticsVO.getUserName())) {
            queryWrapper.like(PatrolTask::getStaffName, statisticsVO.getUserName());
        }
        if (StringUtils.isNotBlank(statisticsVO.getStartTime())) {
            queryWrapper.ge(PatrolTask::getCreateTime, statisticsVO.getStartTime());
        }
        if (StringUtils.isNotBlank(statisticsVO.getEndTime())) {
            queryWrapper.le(PatrolTask::getCreateTime, statisticsVO.getEndTime());
        }
        if (StringUtils.isNotBlank(statisticsVO.getTeamId())) {
            List<PatrolPool> poolList = patrolPoolService.lambdaQuery()
                    .eq(PatrolPool::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .eq(PatrolPool::getOrganizationId, statisticsVO.getTeamId())
                    .select(PatrolPool::getId).list();
            if (poolList != null && poolList.size() > 0) {
                //巡检池内所有包含此班组的id
                List<Long> poolIds = poolList.stream().map(PatrolPool::getId).collect(Collectors.toList());
                queryWrapper.in(PatrolTask::getPatrolPoolId, poolIds);
            } else {
                queryWrapper.eq(PatrolTask::getId, CommonConstant.PATROL_STATUS_DISABLE);
            }
        }

        List<PatrolTask> patrolTaskList = this.baseMapper.selectList(queryWrapper);

        Map<String, Integer> map = new HashMap<>();

        patrolTaskList.forEach(task -> {
            for (String userName : task.getStaffName().split(PatrolConstant.SPL)) {
                Integer num = map.get(userName);
                if (num == null) {
                    num = 1;
                } else {
                    num++;
                }
                if (StringUtils.isNotBlank(statisticsVO.getUserName()) && StrUtil.equals(userName, statisticsVO.getUserName())) {
                    map.put(userName, num);
                }
                if (StrUtil.isBlank(statisticsVO.getUserName())) {
                    map.put(userName, num);
                }
            }
        });

        List<SysUser> list = sysUserService.lambdaQuery()
                .eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(StringUtils.isNotBlank(statisticsVO.getTeamId()), SysUser::getOrgId, statisticsVO.getTeamId())
                .like(StringUtils.isNotBlank(statisticsVO.getUserName()), SysUser::getRealname, statisticsVO.getUserName())
                .list();

        List<String> userNameList = list.stream().map(SysUser::getRealname).collect(Collectors.toList());
        for (String userName : userNameList) {
            if (!map.containsKey(userName)) {
                map.put(userName, 0);
            }
        }

        return map;
    }

    @Override
    public List<ExportTaskSubmitVO> selectExportTaskList(PatrolPoolParam param) {
        //权限设置
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = user.getId();
        if (StringUtils.isBlank(param.getOrganizationId())) {
            List<String> ids = roleAdditionalUtils.getListDepartIdsByUserId(userId);
            if (CollectionUtils.isNotEmpty(ids)) {
                //return new ArrayList<>();
                param.setDepartList(ids);
            }
        }
        if (StringUtils.isBlank(param.getSystemTypeName())) {
            List<String> ids = roleAdditionalUtils.getListSystemCodesByUserId(userId);
            if (CollectionUtils.isNotEmpty(ids)) {
                //return new ArrayList<>();
                param.setSystemCodes(ids);
            }
        }
        return this.baseMapper.selectExportTaskList(param);
    }

    @Override
    public Result deleteTaskByIds(List<String> poolIdList) {
        int num = patrolPoolMapper.deleteByIds(poolIdList,DateUtils.getDate());
        if (num < poolIdList.size()){
            return Result.ok("已指派、已完成、漏检的任务无法被删除");
        }
        return Result.ok("删除成功");
    }

    @Override
    public Integer countCompletedPatrolNumByOrgIdAndTime(String orgId, DateTime startTime, DateTime endTime) {
        Integer completedNum = this.baseMapper.countCompletedPatrolNumByOrgIdAndTime(orgId, startTime, endTime);
        completedNum += this.baseMapper.countTwiceIgnoreContentPatrolNum(orgId, startTime, endTime);
        return completedNum;
    }

    @Override
    public Integer countIgnoredPatrolNumByOrgIdAndTime(String orgId, DateTime startTime, DateTime endTime) {
        Integer ignoredNum = this.baseMapper.countIgnoredPatrolNumByOrgIdAndTime(orgId, startTime, endTime);
        ignoredNum -= this.baseMapper.countTwiceIgnoreContentPatrolNum(orgId, startTime, endTime);
        return ignoredNum;
    }
    @Override
    public Result<List<AppStationPatrolStatisticsVO>> appStationPatrolStatistics() {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Date startTime = TimeUtil.getCurrentWeekDayStartTime();//本周第一天
        Date endTime = TimeUtil.getCurrentWeekDayEndTime();//本周最后一天
        return Result.ok(this.baseMapper.appStationPatrolStatistics(startTime,endTime,user.getOrgId()));
    }
}
