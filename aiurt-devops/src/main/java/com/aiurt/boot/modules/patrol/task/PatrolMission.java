package com.aiurt.boot.modules.patrol.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.swsc.copsms.common.util.LocalDateUtil;
import com.swsc.copsms.modules.patrol.constant.PatrolConstant;
import com.swsc.copsms.modules.patrol.entity.*;
import com.swsc.copsms.modules.patrol.mapper.*;
import com.swsc.copsms.modules.patrol.service.IPatrolPoolContentService;
import com.swsc.copsms.modules.patrol.utils.CopyUtils;
import com.swsc.copsms.modules.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @description: PatrolTask
 * @author: Mr.zhao
 * @date: 2021/9/15 16:42
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PatrolMission {

	private final PatrolMapper patrolMapper;

	private final PatrolContentMapper patrolContentMapper;

	private final PatrolPoolMapper patrolPoolMapper;

	private final PatrolPoolContentMapper patrolPoolContentMapper;

	private final IPatrolPoolContentService iPatrolPoolContentService;

	private final PatrolTaskMapper patrolTaskMapper;

	private final SysUserMapper sysUserMapper;

	@Autowired
	private RedisTemplate redisTemplate;


	/**
	 * 生成巡检池
	 */
	@Scheduled(cron = "0 0 0 * * ? ")
	public void generatePoll() {
		Date date = new Date();
		LocalDate localDate = LocalDate.now();
		int week = localDate.getDayOfWeek().getValue();


		List<Patrol> patrolList = patrolMapper.selectList(new QueryWrapper<Patrol>()
				.eq(Patrol.DEL_FLAG, PatrolConstant.UN_DEL_FLAG)
				.eq(Patrol.STATUS, PatrolConstant.ENABLE)
				.isNotNull(Patrol.TACTICS)
		);

		for (Patrol patrol : patrolList) {

			//巡检频率
			Integer tactics = patrol.getTactics();
			//组织id集合
			String organizationIds = patrol.getOrganizationIds();
			List<String> ids = new ArrayList<>();


			List<PatrolContent> list = patrolContentMapper.selectList(new QueryWrapper<PatrolContent>()
					.eq(PatrolContent.RECORD_ID, patrol.getId())
					.eq(PatrolContent.DEL_FLAG, PatrolConstant.UN_DEL_FLAG)
					.orderByAsc(PatrolContent.SEQUENCE));


			boolean flag = false;

			//分割组织id
			if (StringUtils.isNotBlank(organizationIds)) {
				try {
					if (organizationIds.contains(",")) {
						ids.addAll(Arrays.asList(organizationIds.trim().split(PatrolConstant.SPL)));
					} else {
						ids.add(organizationIds.trim());
					}
				} catch (NumberFormatException e) {
					flag = true;
				}
			}
			if (flag) {
				log.error("巡检标准:{} 策略班组信息错误", patrol);
				continue;
			}

			if (tactics == 1) {
				//一天一次
				insertPatrolPool(list, ids, patrol, date, 1);
			} else if (tactics == 2) {
				//一周两次
				String dayOfWeek = patrol.getDayOfWeek();
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

					insertPatrolPool(list, ids, patrol, date, ln);
				}
			} else if (tactics == 3) {
				//一周一次
				int theWeek = Integer.parseInt(patrol.getDayOfWeek());
				if (theWeek == week) {
					insertPatrolPool(list, ids, patrol, date, 1);
				}
			}
			log.info("添加巡检池内容:{}", patrol);
		}

	}


	/**
	 * 插入巡检池
	 *
	 * @param list   列表
	 * @param ids    id
	 * @param patrol 巡逻
	 * @param date   日期
	 * @param counts 巡检次数
	 */
	public void insertPatrolPool(List<PatrolContent> list, List<String> ids, Patrol patrol, Date date, Integer counts) {

		for (String oId : ids) {
			PatrolPool pool = new PatrolPool();

			pool.setDelFlag(0)
					//计划名称
					.setPatrolName(patrol.getTitle())
					//巡检系统
					.setSystemType(patrol.getTypes())
					//组织id
					.setOrganizationId(oId)
					//巡检频率
					.setTactics(patrol.getTactics())
					//巡检次数
					.setCounts(counts)
					//未指派状态
					.setStatus(0)
					//非手动下发
					.setType(0)
					.setCreateBy(PatrolConstant.ADMIN)
					.setUpdateBy(PatrolConstant.ADMIN)
					.setCreateTime(date)
					.setUpdateTime(date);
			patrolPoolMapper.insert(pool);

			List<PatrolPoolContent> contents = CopyUtils.copyPatrolPoolContent(list, pool.getId());
			iPatrolPoolContentService.saveBatch(contents);
		}

	}


	/**
	 * 验证巡检任务是否完成
	 */
	@Scheduled(cron = "0 0 1 * * ? ")
	public void verifyTaskCompleted() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

		//现在时间
		LocalDate now = LocalDate.now();

		//昨日最晚时间
		LocalDateTime date = now.plusDays(-1).atTime(23, 59, 59);

		//昨日周几
		int week = date.getDayOfWeek().getValue();

		//与这周最后一天相差
		int len = 7 - date.getDayOfWeek().getValue();

		//这周最后一天
		LocalDateTime weekDay = now.plusDays(len).atTime(23, 59, 59);


		//初步筛选过期任务
		List<PatrolTask> tasks = patrolTaskMapper.selectList(new QueryWrapper<PatrolTask>()
				.eq(PatrolTask.STATUS, PatrolConstant.DISABLE)
				.le(PatrolTask.CREATE_TIME, now.plusDays(-1).atTime(23, 59, 59))
				.eq(PatrolTask.DEL_FLAG, PatrolConstant.UN_DEL_FLAG)
				.eq(PatrolTask.IGNORE_STATUS, PatrolConstant.DISABLE)
		);
		for (PatrolTask task : tasks) {
			//任务池id
			Long patrolPoolId = task.getPatrolPoolId();
			//任务池
			PatrolPool patrolPool = patrolPoolMapper.selectById(patrolPoolId);
			//频率
			Integer tactics = patrolPool.getTactics();

			if (tactics == 1) {
				//一天一次
				task.setIgnoreStatus(1);
				task.setIgnoreTime(sdf.format(task.getCreateTime()));
				patrolTaskMapper.updateById(task);
				log.info("系统查询漏检:{}", task);

			} else {
				//创建时间
				String format = sdf.format(task.getCreateTime());
				String[] split = format.split("-");
				LocalDate of = LocalDate.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));

				int dayOfWeek = of.getDayOfWeek().getValue();
				if (tactics == 2) {

					//一周两次
					if (patrolPool.getCounts() == 1) {
						if (date.plusDays(-1 * week).compareTo(of.atTime(23, 59, 59)) > 0) {
							//若上周周日大于创建日期,则说明非本周任务,直接认定为漏检
							String start = LocalDateUtil.strToLocalDate(of.plusDays(-(dayOfWeek - 1)), "yyyy/MM/dd");
							String end = LocalDateUtil.strToLocalDate(of.plusDays(7 - dayOfWeek), "yyyy/MM/dd");
							//一天一次
							task.setIgnoreStatus(1);
							task.setIgnoreTime(start + "-" + end);


						} else {
							//第一次
							if (len < 2 ) {


							}
						}
					} else {
						//这星期最后一天或

					}

				} else if (tactics == 3) {
					//一周一次

				}


			}
		}
	}


}
