package com.aiurt.boot.modules.patrol.task;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.TaskStatusUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.aiurt.boot.modules.apphome.constant.UserTaskConstant;
import com.aiurt.boot.modules.apphome.service.UserTaskService;
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
import com.aiurt.boot.modules.patrol.service.IPatrolContentService;
import com.aiurt.boot.modules.patrol.service.IPatrolPoolContentService;
import com.aiurt.boot.modules.patrol.service.IPatrolPoolService;
import com.aiurt.boot.modules.patrol.service.IPatrolService;
import com.aiurt.boot.modules.patrol.utils.NumberGenerateUtils;
import com.aiurt.boot.modules.patrol.vo.PatrolTaskIgnoreVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: PatrolTask
 * @author: Mr.zhao
 * @date: 2021/9/15 16:42
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PatrolMission {

	private final IPatrolService patrolService;

	private final PatrolPoolMapper patrolPoolMapper;

	private final PatrolTaskMapper patrolTaskMapper;

	private final IPatrolContentService patrolContentService;

	private final NumberGenerateUtils numberGenerateUtils;

	private final IStationService stationService;

	private final ISubsystemService subsystemService;

	private final IPatrolPoolContentService patrolPoolContentService;

	private final IPatrolPoolService patrolPoolService;

	private final UserTaskService userTaskService;

	/**
	 * 生成巡检池
	 */
    @Scheduled(cron = "0 0 0 * * ? ")
    //@Scheduled(cron = "0 47 9 * * ?")
	public void generatePoolList(){
	    if (!TaskStatusUtil.getTaskStatus()) {
		    return;
	    }
		//查询是否有开启,但不存在的.
		//updateStatus();
		//生成
		generatePool();
	}

	/**
	 * 生成巡检池数据
	 */
	public void generatePool() {
		LocalTime lastTime = LocalTime.of(23, 59, 59);
		Date date = new Date();
		LocalDate localDate = LocalDate.now();
		int week = localDate.getDayOfWeek().getValue();
		LocalDateTime maxTime = localDate.plusDays(7 - week).atTime(lastTime);
		//巡检标准
		List<Patrol> patrolList = patrolService.lambdaQuery()
				.eq(Patrol::getDelFlag, CommonConstant.DEL_FLAG_0)
				.eq(Patrol::getStatus, PatrolConstant.ENABLE)
				.isNotNull(Patrol::getTactics)
				.list();

		//所有系统code与名称
		List<Subsystem> lineList = subsystemService.lambdaQuery()
				.eq(Subsystem::getDelFlag, CommonConstant.DEL_FLAG_0)
				.select(Subsystem::getSystemCode, Subsystem::getSystemName)
				.list();

		List<Station> stations = stationService.lambdaQuery()
				.select(Station::getId, Station::getTeamId, Station::getStationName, Station::getStationCode).list();


		Map<String, String> systemTypeMap = new HashMap<>();
		if (lineList != null && lineList.size() > 0) {
			systemTypeMap = lineList.stream().collect(Collectors.toMap(Subsystem::getSystemCode, Subsystem::getSystemName));
		}

		Map<Integer, Station> stationMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(stations)) {
			stationMap = stations.stream().collect(Collectors.toMap(Station::getId, s -> s));
		}

		for (Patrol patrol : patrolList) {

			//巡检频率
			Integer tactics = patrol.getTactics();
			//站点id集合
			String organizationIds = patrol.getOrganizationIds();
			//组织id集合
			List<String> ids = new ArrayList<>();
			//巡检项数据
			List<PatrolContent> list = patrolContentService.lambdaQuery()
					.eq(PatrolContent::getDelFlag, CommonConstant.DEL_FLAG_0)
					.eq(PatrolContent::getRecordId, patrol.getId())
					.list();
			boolean flag = false;
			//分割组织id
			if (StringUtils.isNotBlank(organizationIds)) {
				try {
					ids.addAll(Arrays.asList(organizationIds.trim().split(PatrolConstant.SPL)));
				} catch (NumberFormatException e) {
					flag = true;
				}
			}
			if (flag) {
				log.error("巡检标准:{} 策略站点信息错误", patrol);
				continue;
			}

			if (tactics == 1) {
				//一天一次
				insertPatrolPool(stationMap, systemTypeMap, list, ids, patrol, date, 1, localDate.atTime(lastTime), 0);
			} else if (tactics == 2) {
				//一周两次
				String dayOfWeek = patrol.getDayOfWeek();
				if (StringUtils.isNotBlank(dayOfWeek)) {
					String[] split = dayOfWeek.trim().split(PatrolConstant.SPL);
					if (Integer.parseInt(split[0]) == week || Integer.parseInt(split[1]) == week) {
						Integer ln = null;

						//判断是否为本周第二次巡检
						if (Integer.parseInt(split[0]) < Integer.parseInt(split[1])) {
							if (Integer.parseInt(split[0]) == week) {
								ln = 1;
							} else {
								ln = 2;
							}
						} else {
							if (Integer.parseInt(split[1]) == week) {
								ln = 1;
							} else {
								ln = 2;
							}
						}
						insertPatrolPool(stationMap, systemTypeMap, list, ids, patrol, date, ln, localDate.atTime(lastTime), 0);
					}
				} else if (week == 1) {
					//插入周两次 第一次为非隐藏,第二次为隐藏
					insertPatrolPool(stationMap, systemTypeMap, list, ids, patrol, date, 2, maxTime, 2);
				}
			} else if (tactics == 3) {
				//一周一次
				if (StringUtils.isNotBlank(patrol.getDayOfWeek())) {
					int theWeek = Integer.parseInt(patrol.getDayOfWeek());
					if (theWeek == week) {
						insertPatrolPool(stationMap, systemTypeMap, list, ids, patrol, date, 1, localDate.atTime(lastTime), 0);
					}
				} else if (week == 1) {
					insertPatrolPool(stationMap, systemTypeMap, list, ids, patrol, date, 1, maxTime, 2);
				}
			}
			log.info("添加巡检池内容:{}", patrol);
		}

	}


	/**
	 * 插入巡检池
	 *
	 * @param systemTypeMap 系统所有的map集合  key:code  value:名称
	 * @param list          树列表
	 * @param ids           组织id集合
	 * @param patrol        巡检标准
	 * @param date          日期
	 * @param counts        巡检次数
	 * @param exTime        未选择周的过期时间
	 * @param type          巡检类型
	 */
	public void insertPatrolPool(Map<Integer, Station> stationMap, Map<String, String> systemTypeMap, List<PatrolContent> list, List<String> ids,
	                             Patrol patrol, Date date, Integer counts, LocalDateTime exTime, Integer type) {

		String[] split = patrol.getTypes().split(PatrolConstant.SPL);

		StringBuilder typeName = new StringBuilder();
		//拼接系统名称
		if (split.length > 0) {
			for (String s : split) {
				typeName.append(StringUtils.isNotBlank(systemTypeMap.get(s)) ? systemTypeMap.get(s) : "");
			}
		}

		for (String stationId : ids) {
			PatrolPool pool = new PatrolPool();

			//拼接线路等字段
			String before = "X";

			Station station = null;
			try {
				station = stationMap.get(Integer.parseInt(stationId));
			} catch (NumberFormatException ignored) {
			}

			if (station == null || StringUtils.isBlank(station.getTeamId())) {
				log.error("站点信息错误,站点id:{},错误对象:{}", stationId, station);
				continue;
			}

			if (StringUtils.isNotBlank(station.getStationCode())) {
				before = before.concat(station.getStationCode());
			}


			// 获取线路等
			String codeNo = numberGenerateUtils.getCodeNo(before);

			pool.setDelFlag(0)
					//计划名称
					.setPatrolName(patrol.getTitle())
					//巡检系统
					.setSystemType(patrol.getTypes())
					//巡检系统名称
					.setSystemTypeName(typeName.toString())
					//组织id
					.setOrganizationId(station.getTeamId())
					//站点id
					.setLineId(station.getId())
					//站点名称
					.setLineName(station.getStationName())
					//编号
					.setCode(codeNo)
					//巡检频率
					.setTactics(patrol.getTactics())
					//巡检次数
					.setCounts(counts)
					//未指派状态
					.setStatus(0)
					//非手动下发
					.setType(type)
					//过期时间
					.setExecutionTime(exTime)
					//是否隐藏
					.setHide(0)
					.setNote(patrol.getNote())
					.setCreateBy(patrol.getCreateBy())
					.setUpdateBy(patrol.getUpdateBy())
					.setCreateTime(date)
					.setUpdateTime(date);

			//若是周任务 2次未选择,则先插入第一次
			if (type == 2 && counts == 2) {
				pool.setCounts(1);
				pool.setExecutionTime(exTime.plusDays(-3));
			}

			int insert = patrolPoolMapper.insert(pool);
			//复制巡检项数据
			if (insert > 0) {
				boolean flag = patrolPoolContentService.copyContent(list, pool.getId());
				if (!flag) {
					log.error("插入巡检池错误 pool:{}", pool);
				}
			} else {
				log.error("插入巡检池错误 pool:{}", pool);
			}

			if (type == 2 && counts == 2) {
				PatrolPool secPool = new PatrolPool();
				// 获取线路等
				String secCodeNo = numberGenerateUtils.getCodeNo(before);
				//插入第二条
				BeanUtils.copyProperties(pool, secPool);
				secPool.setCode(secCodeNo)
						.setCounts(2)
						.setHide(1)
						.setId(null)
                        .setSupId(pool.getId())
                        .setExecutionTime(exTime);
				int secInsert = patrolPoolMapper.insert(secPool);
				if (secInsert > 0) {
					//复制第二条的巡检项数据
					boolean flag = patrolPoolContentService.copyContent(list, secPool.getId());
					if (!flag) {
						log.error("插入巡检池错误 pool:{}", secPool);
					}
				} else {
					log.error("插入巡检池错误 pool:{}", secPool);
				}
			}


		}
	}




	/**
	 * 验证巡检任务是否完成
	 */
	//@Scheduled(cron = "0 0 1 * * ? ")
	@Scheduled(cron = "0 30 0 * * ? ")
	//@Scheduled(cron = "0 36 14 * * ? ")
	public void verifyTaskCompleted() {
		if (!TaskStatusUtil.getTaskStatus()) {
			return;
		}
		LocalTime lastLocalTime = LocalTime.of(23, 59, 59);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		//现在时间
		LocalDate now = LocalDate.now();

		//昨日最晚时间
		LocalDateTime lastTime = now.plusDays(-1).atTime(lastLocalTime);

		//查询已过期，未指派
		List<PatrolPool> unassignedPoolList = patrolPoolMapper.selectList(new LambdaQueryWrapper<PatrolPool>()
				.eq(PatrolPool::getStatus, PatrolConstant.DISABLE)
				.eq(PatrolPool::getDelFlag, CommonConstant.DEL_FLAG_0)
				.le(PatrolPool::getExecutionTime, lastTime)
		);
		//未指派处理
		for (PatrolPool pool : unassignedPoolList) {
			pool.setHide(PatrolConstant.DISABLE).setStatus(1);
			//查询用户名称
			// todo
			List<LoginUser> userList = new ArrayList<>();
			/*List<SysUser> userList = sysUserService.lambdaQuery()
					.eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
					.eq(SysUser::getStatus,PatrolConstant.ENABLE)
					.eq(SysUser::getOrgId, pool.getOrganizationId())
					.select(SysUser::getRealname, SysUser::getId).list();*/
			PatrolTask task = new PatrolTask();
			task.setPatrolPoolId(pool.getId())
					.setIgnoreStatus(1)
					.setStatus(0)
					.setDelFlag(0)
					.setCreateTime(pool.getCreateTime())
					.setUpdateTime(pool.getCreateTime())
					.setCode(pool.getCode())
					.setCreateBy(pool.getCreateBy())
					.setUpdateBy(pool.getCreateBy());

			//设置过期时间
			if (pool.getExecutionTime().toLocalDate().toString().equals(sdf.format(pool.getCreateTime()))) {
				task.setIgnoreTime(pool.getExecutionTime().toLocalDate().toString());
			} else {
				task.setIgnoreTime(sdf.format(pool.getCreateTime()).concat("至").concat(pool.getExecutionTime().toLocalDate().toString()));
			}
			Optional.ofNullable(userList).ifPresent(users -> {
				List<String> ids = users.stream().map(LoginUser::getId).collect(Collectors.toList());
				List<String> realNames = users.stream().map(LoginUser::getRealname).collect(Collectors.toList());
				try {
					task.setStaffIds(StringUtils.join(ids, ",")).setStaffName(StringUtils.join(realNames, ","));
				} catch (Exception ignored) {
				}
			});
			// 2022-03-07  改为无人也生成漏检.

			//if (StringUtils.isBlank(task.getStaffIds()) || StringUtils.isBlank(task.getStaffName())) {
			//	log.error("生成漏检巡检任务失败:{} , userList:{}", task, userList);
			//} else {
				int insert = patrolTaskMapper.insert(task);
				if (insert > 0) {
					int update = this.patrolPoolMapper.updateById(pool);
					log.info("过期任务:{} 更改执行结果(0.失败 1.成功):{}", pool, update);
				} else {
					log.error("生成漏检巡检任务失败:{}", task);
				}
			//}
		}
		List<PatrolTaskIgnoreVO> ignoreTaskList = patrolPoolMapper.selectIgnore(lastTime);
		for (PatrolTaskIgnoreVO taskVO : ignoreTaskList) {
			PatrolTask task = new PatrolTask();
			BeanUtils.copyProperties(taskVO, task);
			task.setIgnoreStatus(1);
			//处理漏检时间
			if (taskVO.getExecutionTime().toLocalDate().toString().equals(sdf.format(taskVO.getPoolCreateTime()))) {
				task.setIgnoreTime(taskVO.getExecutionTime().toLocalDate().toString());
			} else {
				task.setIgnoreTime(sdf.format(taskVO.getPoolCreateTime()).concat("至").concat(taskVO.getExecutionTime().toLocalDate().toString()));
			}
			int update = patrolTaskMapper.updateById(task);
			if (update > 0) {
				Long poolId = task.getPatrolPoolId();
				if (poolId!=null) {
					userTaskService.removeUserTaskWork(Arrays.asList(task.getStaffIds().split(PatrolConstant.SPL)),poolId, UserTaskConstant.USER_TASK_TYPE_1);
				}
				log.info("过期任务:{} 更改执行结果(0.失败 1.成功):{}", task, update);
			} else {
				log.error("修改漏检巡检任务失败:{}", task);
			}
		}
	}

	/**
	 * 查询周两次任务第一次是否完成或漏检,去除第二次隐藏
	 */
	//@Scheduled(cron = "0 30 0 * * ? ")
	@Scheduled(cron = "0 0 1 * * ? ")
	public void noHide() {
		if (!TaskStatusUtil.getTaskStatus()) {
			return;
		}
		//定义本周时间
		LocalTime lastTime = LocalTime.of(23, 59, 59);
		LocalDate localDate = LocalDate.now();
		int week = localDate.getDayOfWeek().getValue();
		LocalDateTime minTime = localDate.plusDays(1 - week).atTime(LocalTime.MIN);
		LocalDateTime maxTime = localDate.plusDays(7 - week).atTime(lastTime);

		List<PatrolPool> unHideList = null;
		//查询所有本周隐藏任务
		List<PatrolPool> patrolPools = patrolPoolMapper.selectList(new QueryWrapper<PatrolPool>().lambda()
				.eq(PatrolPool::getDelFlag, CommonConstant.DEL_FLAG_0)
				.eq(PatrolPool::getHide, PatrolConstant.ENABLE)
				.between(PatrolPool::getExecutionTime, minTime, maxTime)
		);

		if (CollectionUtils.isNotEmpty(patrolPools)) {
			Map<Long, PatrolPool> poolMap = patrolPools.stream().collect(Collectors.toMap(PatrolPool::getSupId, p -> p));
			//查出所有已指派的
			List<PatrolPool> poolList = patrolPoolMapper.selectList(new QueryWrapper<PatrolPool>().lambda()
					.eq(PatrolPool::getDelFlag, CommonConstant.DEL_FLAG_0)
					.in(PatrolPool::getId, poolMap.keySet())
					//根据需求改变
//					.eq(PatrolPool::getStatus, PatrolConstant.ENABLE)
					.select(PatrolPool::getId)
			);

			if (CollectionUtils.isNotEmpty(poolList)) {
				List<Long> ids = poolList.stream().map(PatrolPool::getId).collect(Collectors.toList());
				//查出所有已完成的
				List<PatrolTask> patrolTasks = patrolTaskMapper.selectList(new QueryWrapper<PatrolTask>().lambda()
						.eq(PatrolTask::getDelFlag, CommonConstant.DEL_FLAG_0)
//						.eq(PatrolTask::getStatus, PatrolConstant.ENABLE)
						//根据需求改变，所有已完成或是漏检的
						.and(q->q.eq(PatrolTask::getStatus, PatrolConstant.ENABLE).or().eq(PatrolTask::getIgnoreStatus,PatrolConstant.ENABLE))
						.in(PatrolTask::getPatrolPoolId, ids)
						.select(PatrolTask::getPatrolPoolId)
				);
				//已完成和漏检的
				if (CollectionUtils.isNotEmpty(patrolTasks)) {
					List<Long> noHideIds = patrolTasks.stream().map(PatrolTask::getPatrolPoolId).collect(Collectors.toList());
					unHideList = new ArrayList<>();
					//添加进修改集合
					for (Long hideId : noHideIds) {
						PatrolPool pool = poolMap.get(hideId);
						if (pool != null) {
							pool.setHide(PatrolConstant.DISABLE);
							unHideList.add(pool);
						}
					}
				}
			}
		}
		if (CollectionUtils.isNotEmpty(unHideList)) {
			patrolPoolService.updateBatchById(unHideList);
		}
	}

	/**
	 * 若系统内的站点或系统不存的,更新状态为未开启.
	 */
	public void updateStatus() {
		//全部存在的系统
		List<Subsystem> subsystemList = subsystemService.lambdaQuery().select(Subsystem::getSystemCode).eq(Subsystem::getDelFlag, CommonConstant.DEL_FLAG_0).list();

		//全部存在的站点
		List<Station> stationList = stationService.lambdaQuery().select(Station::getId).eq(Station::getDelFlag, CommonConstant.DEL_FLAG_0).list();

		List<Integer> stationIds = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(stationList)) {
			stationIds = stationList.stream().map(Station::getId).collect(Collectors.toList());
		}

		List<String> systemCodeList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(subsystemList)){
			systemCodeList = subsystemList.stream().map(Subsystem::getSystemCode).collect(Collectors.toList());
		}

		List<Patrol> patrolList = patrolService.lambdaQuery()
				.eq(Patrol::getDelFlag, CommonConstant.DEL_FLAG_0)
				.eq(Patrol::getStatus, PatrolConstant.ENABLE)
				.list();

		if (CollectionUtils.isNotEmpty(patrolList)) {
			List<Integer> finalStationIds = stationIds;
			List<String> finalSystemCodeList = systemCodeList;


			List<Long> patrolIds = patrolList.stream().filter(p -> {
				//站点id是否存在
				String ids = p.getOrganizationIds();
				if (StringUtils.isBlank(ids)) {
					return true;
				}
				String[] split = ids.split(PatrolConstant.SPL);
				if (split.length == 0) {
					return true;
				}
				for (String s : split) {
					int id;
					try {
						id = Integer.parseInt(s);
					} catch (NumberFormatException e) {
						return true;
					}
					if (!finalStationIds.contains(id)) {
						return true;
					}
				}

				//查询系统是否存在
				String types = p.getTypes();
				if (StringUtils.isBlank(types)) {
					return true;
				}

				String[] typeSplit = p.getTypes().split(PatrolConstant.SPL);
				if (typeSplit.length == 0) {
					return true;
				}
				for (String code : typeSplit) {
					if (finalSystemCodeList.contains(code)) {
						return true;
					}
				}
				return false;
			}).map(Patrol::getId).collect(Collectors.toList());

			patrolService.lambdaUpdate().in(Patrol::getId,patrolIds).update(new Patrol().setStatus(PatrolConstant.DISABLE));
		}

	}


	/**
	 * 删除redis中编号的记数
	 */
	@Scheduled(cron = "0 0 0 1 * ? ")
	public void delRedis() {
		if (!TaskStatusUtil.getTaskStatus()) {
			return;
		}
		numberGenerateUtils.delCode2Redis();
	}

}
