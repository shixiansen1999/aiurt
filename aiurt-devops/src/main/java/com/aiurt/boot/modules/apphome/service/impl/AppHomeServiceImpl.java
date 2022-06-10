package com.aiurt.boot.modules.apphome.service.impl;

import cn.hutool.core.date.DateUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.TokenUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.aiurt.boot.modules.apphome.entity.UserTask;
import com.aiurt.boot.modules.apphome.param.HomeListParam;
import com.aiurt.boot.modules.apphome.service.AppHomeService;
import com.aiurt.boot.modules.apphome.service.UserTaskService;
import com.aiurt.boot.modules.apphome.vo.*;
import com.aiurt.boot.modules.fault.mapper.FaultMapper;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.patrol.mapper.PatrolTaskMapper;
import com.aiurt.boot.modules.patrol.param.PatrolAppHomeParam;
import com.aiurt.boot.modules.patrol.vo.PatrolTaskVO;
import com.aiurt.boot.modules.repairManage.entity.RepairTask;
import com.aiurt.boot.modules.repairManage.mapper.RepairPoolMapper;
import com.aiurt.boot.modules.repairManage.mapper.RepairTaskMapper;
import com.aiurt.boot.modules.worklog.mapper.WorkLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: AppHomeServiceImpl
 * @author: Mr.zhao
 * @date: 2021/10/1 13:09
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class AppHomeServiceImpl implements AppHomeService {

	private final PatrolTaskMapper patrolTaskMapper;

	private final FaultMapper faultMapper;

	private final RepairTaskMapper repairTaskMapper;

	private final RepairPoolMapper repairPoolMapper;

	private final IStationService stationService;

	private final ISysBaseAPI iSysBaseAPI;

	private final WorkLogMapper workLogMapper;


	private final UserTaskService userTaskService;

	@Override
	public Result<HomeVO> getHomeList(HttpServletRequest req, HomeListParam param) {
		//当前日期
		LocalDate now = LocalDate.now();
		//返回对象
		HomeVO homeVO = new HomeVO();
		//返回内置对象 待办:pendingVO  已完成:completedVO
		SimpSizeVO pendingVO = new SimpSizeVO();
		SimpSizeVO completedVO = new SimpSizeVO();
		//状态: 0.未完成 1.已完成
		Integer type = param.getType();
		//任务类型  0&null: 全部 1.巡检 2.检修 3.故障
		Integer taskType = param.getTaskType();
		//用户id
		String userId = TokenUtils.getUserId(req, iSysBaseAPI);
		//设置用户id与查询状态
		PatrolAppHomeParam homeParam = new PatrolAppHomeParam()
				.setKeyName(param.getKeyName())
				.setUserId(userId)
				.setType(type)
				.setStartTime(param.getStartTime() == null ? now.atTime(0, 0, 0) : param.getStartTime())
				.setEndTime(param.getEndTime() == null ? now.atTime(23, 59, 59) : param.getEndTime());

		//查询巡检条件
		if (taskType == null || taskType == 0 || taskType == 1) {
			//巡检查询
			List<PatrolTaskVO> patrolTaskList = patrolTaskMapper.selectAppHome(homeParam);

			List<PatrolAppTaskVO> list = new ArrayList<>();
			for (PatrolTaskVO vo : patrolTaskList) {
				PatrolAppTaskVO appVO = new PatrolAppTaskVO();
				appVO.setId(vo.getPoolId())
						.setCode(vo.getCode())
						.setTitle(vo.getName())
						.setStaffName(vo.getStaffName())
						.setStationName(vo.getLineName())
						.setStartTime(vo.getCreateTime())
						.setEndTime(vo.getExecutionTime());
				if (vo.getIgnoreStatus() != null && vo.getIgnoreStatus() == 1) {
					//漏检
					appVO.setStatus(2);
				} else if (vo.getTaskStatus() != null && vo.getTaskStatus() == 0) {
					//待巡检
					appVO.setStatus(1);
				} else if (vo.getPoolStatus() != null && vo.getPoolStatus() == 0) {
					//待指派/领取
					appVO.setStatus(0);
				}
				list.add(appVO);
			}
			homeVO.setPatrolList(list);
			try {
				pendingVO.setPatrolSize(list.size());
			} catch (Exception ignored) {
			}
		}

		////检修查询
		Date date = new Date();
		Date st = DateUtils.getWeekStartTime(date);
		Date et = DateUtils.getWeekEndTime(date);
		String startTime = DateUtil.format(st, "yyyy-MM-dd HH:mm:ss");
		String endTime = DateUtil.format(et, "yyyy-MM-dd HH:mm:ss");
		LambdaQueryWrapper<RepairTask> wrapper = new LambdaQueryWrapper<RepairTask>()
				.eq(RepairTask::getDelFlag, CommonConstant.DEL_FLAG_0)
				.in(RepairTask::getStaffIds, userId)
				//关键字or匹配

				.ge(RepairTask::getStartTime, param.getStartTime() == null ? startTime : param.getStartTime())
				.le(RepairTask::getEndTime, param.getEndTime() == null ? endTime : param.getEndTime());

		if (StringUtils.isNotBlank(param.getKeyName())) {
			wrapper.and(query -> query
							//.like(RepairTask::getContent,param.getKeyName()).or()
							.like(RepairTask::getCode, param.getKeyName())
					//.or().like(RepairTask::getLineName,param.getKeyName())
			);
		}

		if (type == 0) {
			//新增反方向查询操作
			LambdaQueryWrapper<RepairTask> copyWrapper = new LambdaQueryWrapper<RepairTask>();
			BeanUtils.copyProperties(wrapper, copyWrapper);
			copyWrapper.in(RepairTask::getStatus, "2,3,4,5");

			wrapper.eq(RepairTask::getStatus, 0);

			//未完成
			Integer count = repairTaskMapper.selectCount(wrapper);
			//已完成
			Integer count1 = repairTaskMapper.selectCount(copyWrapper);
			pendingVO.setRepairSize(count);
			completedVO.setRepairSize(count1);
		}
		if (type == 1) {
			wrapper.in(RepairTask::getStatus, "2,3,4,5");
		}
		List<RepairTask> repairTaskList = repairTaskMapper.selectList(wrapper);
		for (RepairTask repairTask : repairTaskList) {
			String[] split = repairTask.getRepairPoolIds().split(",");
			String organizationId = repairPoolMapper.selectById(split[0]).getOrganizationId();
			Station station = stationService.getById(organizationId);
			if (station != null) {
				repairTask.setLineName(station.getLineName());
				repairTask.setStationName(station.getStationName());
				repairTask.setTeamName(station.getTeamName());
			}
		}
		homeVO.setRepairTaskList(repairTaskList);


		//故障查询
		if (taskType == null || taskType == 0 || taskType == 3) {
			List<FaultHomeVO> faultList = faultMapper.selectAppHome(homeParam);
			faultList.forEach(f -> {
				f.setType(3);
			});
			//标题设置
			for (FaultHomeVO vo : faultList) {
				if (vo.getTitle() == null || "".equals(vo.getTitle())) {
					if (vo.getType() == 3) {
						vo.setTitle("故障");
					}
				}
			}
			homeVO.setFaultList(faultList);
		}

		//工作日志
		List<WorkLogVO> workLogVOS = workLogMapper.selectAppHome(homeParam);
		for (WorkLogVO workLogVO : workLogVOS) {
			workLogVO.setType(4);
			workLogVO.setTitle("日志");
		}
		homeVO.setWorkLogList(workLogVOS);

		//todo: 总条数 = 巡检总条数 + 检修总条数 + 故障总条数 + 日志条数
		//计算总条数
		homeVO.setSize(homeVO.getPatrolList() != null ? homeVO.getPatrolList().size() : 0);
		if (homeVO.getRepairTaskList().size() != 0) {
			homeVO.setSize(homeVO.getSize() + homeVO.getRepairTaskList().size());
			pendingVO.setRepairSize(homeVO.getRepairTaskList().size());
		}
		if (homeVO.getFaultList().size() != 0) {
			homeVO.setSize(homeVO.getSize() + homeVO.getFaultList().size());
		}
		if (homeVO.getWorkLogList().size() != 0) {
			homeVO.setSize(homeVO.getSize() + homeVO.getWorkLogList().size());
		}

		//查询已完结
		Integer count = patrolTaskMapper.selectAppHomeCount(homeParam);
		completedVO.setPatrolSize(count);
		homeVO.setOverSize(homeVO.getOverSize() != null ? homeVO.getOverSize() + count : count);
		//查询故障已完成
		Integer homeCount = faultMapper.selectAppHomeCount(homeParam);
		completedVO.setFaultSize(homeCount);
		homeVO.setOverSize(homeVO.getOverSize() + homeCount);

		homeVO.setCompletedVO(completedVO);
		homeVO.setPendingVO(pendingVO);
		return Result.ok(homeVO);

	}

	@Override
	public IPage<UserTask> getHomeTaskList(HttpServletRequest req, HomeListParam param, Pageable pageable) {


		LambdaQueryWrapper<UserTask> queryWrapper = new LambdaQueryWrapper<>();

		queryWrapper.eq(UserTask::getUserId, param.getUserId())
				.eq(UserTask::getDelFlag, CommonConstant.DEL_FLAG_0)
				.eq(UserTask::getStatus, param.getType())
				.eq(param.getTaskType() != null && param.getTaskType() != 0,UserTask::getType, param.getTaskType())
				//关键字匹配
				.and(StringUtils.isNotBlank(param.getKeyName()),query -> query.like(UserTask::getRecordCode, param.getKeyName())
						.or().eq(UserTask::getRecordId, param.getKeyName())
						.or().eq(UserTask::getTitle, param.getKeyName())
						.or().eq(UserTask::getNote, param.getKeyName())
						.or().eq(UserTask::getContent, param.getKeyName()))
				//时间匹配
				.ge(param.getStartTime() != null,UserTask::getCompleteTime, param.getStartTime())
				.le(param.getEndTime() != null,UserTask::getCompleteTime, param.getEndTime())
				//排序
				//.orderByAsc(UserTask::getLevel)
				//.orderByDesc(UserTask::getCreateTime);
				.last("  order by case `level` when 4 then 0 when 3 then 1 else 2 end , create_time desc");


		return userTaskService.page(new Page<>(pageable.getPageNumber(), pageable.getPageSize()), queryWrapper);
	}

	@Override
	public AppHomeVO getHomeCount(HttpServletRequest req, HomeListParam param, AppHomeVO vo) {


		//LambdaQueryWrapper<UserTask> queryWrapper = new LambdaQueryWrapper<>();
		//
		//queryWrapper.eq(UserTask::getUserId, param.getUserId())
		//		.eq(UserTask::getDelFlag, CommonConstant.DEL_FLAG_0)
		//		.eq(UserTask::getStatus, param.getType())
		//		.orderByAsc(UserTask::getLevel)
		//		.orderByDesc(UserTask::getId);
		//if (param.getTaskType() != null && param.getTaskType() != 0) {
		//	queryWrapper.eq(UserTask::getType, param.getTaskType());
		//}
		//
		////关键字匹配
		//if (StringUtils.isNotBlank(param.getKeyName())) {
		//	queryWrapper.and(query -> query.like(UserTask::getRecordCode, param.getKeyName())
		//			.or().eq(UserTask::getRecordId, param.getKeyName())
		//			.or().eq(UserTask::getTitle, param.getKeyName())
		//			.or().eq(UserTask::getNote, param.getKeyName())
		//			.or().eq(UserTask::getContent, param.getKeyName())
		//	);
		//}
		List<UserTask> list = userTaskService.lambdaQuery()
				.eq(UserTask::getUserId, param.getUserId())
				.eq(UserTask::getDelFlag, CommonConstant.DEL_FLAG_0)
				.select(UserTask::getType, UserTask::getStatus, UserTask::getId)
				.list();

		//已完成
		SimpSizeVO sizeVO = new SimpSizeVO();
		//待办
		SimpSizeVO noSizeVO = new SimpSizeVO();

		if (CollectionUtils.isNotEmpty(list)) {
			//状态分解
			Map<Integer, List<UserTask>> typeMap = list.stream().collect(Collectors.groupingBy(UserTask::getType));

			List<UserTask> patrolList = typeMap.get(1);
			List<UserTask> repairList = typeMap.get(2);
			List<UserTask> faultList = typeMap.get(3);

			if (CollectionUtils.isNotEmpty(patrolList)) {
				//巡检
				Map<Integer, List<UserTask>> patrolMap = patrolList.stream().collect(Collectors.groupingBy(UserTask::getStatus));
				if (patrolMap != null) {
					List<UserTask> unTasks = patrolMap.get(0);
					List<UserTask> successTasks = patrolMap.get(1);
					if (CollectionUtils.isNotEmpty(unTasks)) {
						sizeVO.setPatrolSize(unTasks.size());
						vo.setSize(vo.getSize() + unTasks.size());
					}
					if (CollectionUtils.isNotEmpty(successTasks)) {
						noSizeVO.setPatrolSize(successTasks.size());
						vo.setOverSize(vo.getOverSize() + successTasks.size());
					}
				}
			}
			if (CollectionUtils.isNotEmpty(repairList)) {
				//检修
				Map<Integer, List<UserTask>> repairMap = repairList.stream().collect(Collectors.groupingBy(UserTask::getStatus));
				if (repairMap != null) {
					List<UserTask> unTasks = repairMap.get(0);
					List<UserTask> successTasks = repairMap.get(1);
					if (CollectionUtils.isNotEmpty(unTasks)) {
						sizeVO.setRepairSize(unTasks.size());
						vo.setSize(vo.getSize() + unTasks.size());
					}
					if (CollectionUtils.isNotEmpty(successTasks)) {
						noSizeVO.setRepairSize(successTasks.size());
						vo.setOverSize(vo.getOverSize() + successTasks.size());
					}
				}
			}
			if (CollectionUtils.isNotEmpty(faultList)) {
				//故障
				Map<Integer, List<UserTask>> faultMap = faultList.stream().collect(Collectors.groupingBy(UserTask::getStatus));
				if (faultMap != null) {
					List<UserTask> unTasks = faultMap.get(0);
					List<UserTask> successTasks = faultMap.get(1);
					if (CollectionUtils.isNotEmpty(unTasks)) {
						sizeVO.setFaultSize(unTasks.size());
						vo.setSize(vo.getSize() + unTasks.size());

					}
					if (CollectionUtils.isNotEmpty(successTasks)) {
						noSizeVO.setFaultSize(successTasks.size());
						vo.setOverSize(vo.getOverSize() + successTasks.size());
					}
				}
			}
		}
		vo.setCompletedVO(sizeVO);
		vo.setPendingVO(noSizeVO);

		return vo;
	}

}
