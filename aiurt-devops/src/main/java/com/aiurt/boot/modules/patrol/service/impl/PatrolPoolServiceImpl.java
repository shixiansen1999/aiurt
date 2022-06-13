package com.aiurt.boot.modules.patrol.service.impl;


import com.aiurt.boot.modules.appMessage.constant.MessageConstant;
import com.aiurt.boot.modules.appMessage.entity.Message;
import com.aiurt.boot.modules.appMessage.param.MessageAddParam;
import com.aiurt.boot.modules.appMessage.service.IMessageService;
import com.aiurt.boot.modules.apphome.constant.UserTaskConstant;
import com.aiurt.boot.modules.apphome.param.UserTaskAddParam;
import com.aiurt.boot.modules.apphome.service.UserTaskService;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.manage.service.ISubsystemService;
import com.aiurt.boot.modules.patrol.constant.PatrolConstant;
import com.aiurt.boot.modules.patrol.entity.PatrolPool;
import com.aiurt.boot.modules.patrol.entity.PatrolPoolContent;
import com.aiurt.boot.modules.patrol.entity.PatrolTask;
import com.aiurt.boot.modules.patrol.mapper.PatrolPoolMapper;
import com.aiurt.boot.modules.patrol.mapper.PatrolTaskMapper;
import com.aiurt.boot.modules.patrol.param.PoolAppointParam;
import com.aiurt.boot.modules.patrol.param.PoolPageParam;
import com.aiurt.boot.modules.patrol.service.IPatrolPoolContentService;
import com.aiurt.boot.modules.patrol.service.IPatrolPoolService;
import com.aiurt.boot.modules.patrol.vo.PatrolPoolDetailVO;
import com.aiurt.boot.modules.patrol.vo.PatrolPoolVO;
import com.aiurt.boot.modules.patrol.vo.PoolTreeVO;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.RedisUtil;
import com.aiurt.common.util.RoleAdditionalUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 巡检计划池
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Service
@RequiredArgsConstructor
public class PatrolPoolServiceImpl extends ServiceImpl<PatrolPoolMapper, PatrolPool> implements IPatrolPoolService {


	private final PatrolTaskMapper patrolTaskMapper;

//	private final ISysUserService sysUserService;

	private final IStationService stationService;

	private final UserTaskService userTaskService;

	private final ISubsystemService subsystemService;

	private final IPatrolPoolContentService patrolPoolContentService;

	private final RoleAdditionalUtils roleAdditionalUtils;

	private final IMessageService messageService;

	private final RedisUtil redisUtil;

	@Override
	public Result<?> selectPage(PoolPageParam param) {

		IPage<PatrolPoolVO> page = new Page<PatrolPoolVO>(param.getPageNo(), param.getPageSize());

		//权限设置
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String userId = user.getId();
		if (StringUtils.isBlank(param.getOrganizationId())){
			List<String> ids = roleAdditionalUtils.getListDepartIdsByUserId(userId);
			if (CollectionUtils.isNotEmpty(ids)){
				//return Result.ok(page);
				param.setDepartList(ids);
			}
		}
		if (StringUtils.isBlank(param.getTypes())){
			List<String> ids = roleAdditionalUtils.getListSystemCodesByUserId(userId);
			if (CollectionUtils.isNotEmpty(ids)){
				//return Result.ok(page);
				param.setSystemCodes(ids);
			}
		}

		LocalDate now = LocalDate.now();
		param.setStartTime(now.atTime(0, 0, 0)).setEndTime(now.atTime(23, 59, 59));

		int value = now.getDayOfWeek().getValue();
		param.setLastTime(now.plusDays(7-value).atTime(23, 59, 59));

		if (param.getStationId() == null && param.getLineId() != null) {
			List<Station> list = stationService.lambdaQuery()
					.eq(Station::getDelFlag, CommonConstant.DEL_FLAG_0)
					.eq(Station::getLineCode, param.getLineId()).select(Station::getId)
					.list();
			if (CollectionUtils.isNotEmpty(list)) {
				param.setStationIds(list.stream().map(Station::getId).collect(Collectors.toList()));
			}
		}
		List<Subsystem> lineList = subsystemService.lambdaQuery()
						.eq(Subsystem::getDelFlag, CommonConstant.DEL_FLAG_0)
						.select(Subsystem::getSystemCode,Subsystem::getSystemName).list();

		Map<String,String> map = new HashMap<>();
		if (CollectionUtils.isNotEmpty(lineList)) {
			map = lineList.stream().collect(Collectors.toMap(Subsystem::getSystemCode,Subsystem::getSystemName));
		}
		page = this.baseMapper.selectPageList(new Page<>(param.getPageNo(), param.getPageSize()), param);

		//设置系统名称
		for (PatrolPoolVO record : page.getRecords()) {
			if (StringUtils.isNotBlank(record.getSystemType())) {
				record.setSystemTypeName(map.get(record.getSystemType()));
			}
		}

		return Result.ok(page);
	}


	@Transactional(rollbackFor = Exception.class)
	@Override
	public Result<?> appoint(HttpServletRequest req, PoolAppointParam param) {

		Long id = param.getId();

		try {
			Object o = redisUtil.get(PatrolConstant.APPOINT_PREFIX.concat(param.getId() + ""));
			if (ObjectUtils.isNotEmpty(o)){
				return Result.error("频繁指派/领取,请3秒后重试");
			}else {
				redisUtil.set(PatrolConstant.APPOINT_PREFIX.concat(param.getId() + ""),id,3);
			}
		} catch (Exception e) {
			log.error("巡检指派任务发生异常: ".concat(e.getMessage()));
		}

		//任务池
		PatrolPool pool = this.getById(id);
		if (LocalDate.now().atTime(0,0,0).compareTo(pool.getExecutionTime())>0){
			return Result.error("已漏检,无法指派");
		}
		if (pool.getStatus() == 1) {
			PatrolTask task = patrolTaskMapper.selectOne(new LambdaQueryWrapper<PatrolTask>().eq(PatrolTask::getDelFlag, CommonConstant.DEL_FLAG_0)
					.eq(PatrolTask::getPatrolPoolId, id));
			if (task.getStatus() == 1) {
				throw new AiurtBootException("已完成巡检,无法指派");
			} else {
				throw new AiurtBootException("存在已指派任务,请调用重新指派按钮");
			}
		}
		pool.setStatus(PatrolConstant.ENABLE);

		//任务
		PatrolTask task = new PatrolTask();

        // todo 后期修改
        List<LoginUser> sysUsers = new ArrayList<>();
//		List<SysUser> sysUsers = sysUserService.lambdaQuery()
//				.select(SysUser::getRealname)
//				.in(SysUser::getId, param.getUserIds())
//				.eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0).list();

		task.setPatrolPoolId(id)
				.setDelFlag(CommonConstant.DEL_FLAG_0)
				.setStatus(PatrolConstant.DISABLE)
				.setCounts(0)
				.setCode(pool.getCode())
				//.setType(param.getType())
				.setStaffIds(StringUtils.join(param.getUserIds(), PatrolConstant.SPL))
				.setStaffName(StringUtils.join(sysUsers.stream().map(LoginUser::getRealname).collect(Collectors.toList()), PatrolConstant.SPL))
				.setIgnoreStatus(0);

		if (!this.updateById(pool)) {
			throw new AiurtBootException("更新任务池错误");
		}

		int insert = patrolTaskMapper.insert(task);
		if (insert < 1) {
			throw new AiurtBootException("分配任务失败");
		}

		String lineStationName = "";
		Station byId = this.stationService.getById(pool.getId());
		if (byId != null) {
			lineStationName = lineStationName.concat(StringUtils.isNotBlank(byId.getLineName()) ? byId.getLineName() : "").concat(StringUtils.isNotBlank(byId.getStationName()) ? byId.getStationName() : "");
		}


		//待办事项添加
		UserTaskAddParam addParam = new UserTaskAddParam();

		addParam.setUserIds(param.getUserIds())
				.setWorkCode(pool.getCode())
				.setRecordId(id)
				.setTitle(pool.getPatrolName())
				.setContent(StringUtils.isNotBlank(lineStationName) ? lineStationName : pool.getLineName())
				.setType(UserTaskConstant.USER_TASK_TYPE_1)
				.setLevel(2);

		userTaskService.add(addParam);

		//发送app消息
		Message message = new Message();
		message.setTitle("消息通知").setContent("您有一条新的巡检任务!").setType(MessageConstant.MESSAGE_TYPE_0).setCode(id.toString());
		messageService.addMessage(MessageAddParam.builder().message(message).userIds(param.getUserIds()).build());

		return Result.ok();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Result<?> receive(HttpServletRequest req, Long id) {

		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String userId = user.getId();

		//任务
		PatrolTask task = new PatrolTask();

		// todo 后期修改
        List<LoginUser> sysUsers = new ArrayList<>();
//		List<SysUser> sysUsers = sysUserService.lambdaQuery()
//				.select(SysUser::getRealname)
//				.eq(SysUser::getId, userId)
//				.eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0).list();

		//任务池
		PatrolPool pool = this.baseMapper.selectById(id);
		pool.setStatus(PatrolConstant.ENABLE);


		task.setPatrolPoolId(id)
				.setDelFlag(CommonConstant.DEL_FLAG_0)
				.setStatus(PatrolConstant.DISABLE)
				.setCounts(0)
				.setCode(pool.getCode())
				//.setType(param.getType())
				.setStaffIds(userId)
				.setStaffName(StringUtils.join(sysUsers.stream().map(LoginUser::getRealname).collect(Collectors.toList()), PatrolConstant.SPL))
				.setIgnoreStatus(PatrolConstant.DISABLE);

		if (!this.updateById(pool)) {
			throw new AiurtBootException("更新任务池错误");
		}

		int insert = patrolTaskMapper.insert(task);
		if (insert < 1) {
			throw new AiurtBootException("分配任务失败");
		}
		return Result.ok();
	}



	@Override
	public Result<?> detail(HttpServletRequest req, Long id) {

		PatrolPoolDetailVO vo = new PatrolPoolDetailVO();

		PatrolPool patrolPool = this.baseMapper.selectById(id);

		vo.setPatrolName(patrolPool.getPatrolName());

		List<PoolTreeVO> tree = getTree(id, 0L);

		vo.setList(tree);

		return Result.ok();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Result<?> reAppoint(HttpServletRequest req, PoolAppointParam param) {

		Long id = param.getId();

		try {
			Object o = redisUtil.get(PatrolConstant.APPOINT_PREFIX.concat(param.getId() + ""));
			if (ObjectUtils.isNotEmpty(o)){
				return Result.error("频繁指派/领取,请3秒后重试");
			}else {
				redisUtil.set(PatrolConstant.APPOINT_PREFIX.concat(param.getId() + ""),id,3);
			}
		} catch (Exception e) {
			log.error("巡检重新指派任务发生异常: ".concat(e.getMessage()));
		}

		PatrolPool patrolPool = this.getById(id);
		if (LocalDate.now().atTime(0,0,0).compareTo(patrolPool.getExecutionTime())>0){
			return Result.error("已漏检,无法重新指派");
		}
		//任务
		PatrolTask one = patrolTaskMapper.selectOne(new QueryWrapper<PatrolTask>()
						.lambda()
				.eq(PatrolTask::getDelFlag, CommonConstant.DEL_FLAG_0)
				.eq(PatrolTask::getPatrolPoolId, id)
				.last("limit 1"));
		if (one == null) {
			throw new AiurtBootException("未找到已指派数据,重新指派失败");
		}
		if (one.getStatus() == 1 || one.getIgnoreStatus() == 1) {
			throw new AiurtBootException("已完成或已漏检,无法重新指派");
		}

		// todo 后期修改
        List<LoginUser> sysUsers = new ArrayList<>();
//		List<LoginUser> sysUsers = sysUserService.lambdaQuery()
//				.select(SysUser::getRealname)
//				.in(SysUser::getId, param.getUserIds())
//				.eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0).list();

		one.setStaffIds(StringUtils.join(param.getUserIds(), PatrolConstant.SPL))
				.setStaffName(StringUtils.join(sysUsers.stream().map(LoginUser::getRealname).collect(Collectors.toList()), PatrolConstant.SPL));


		int update = patrolTaskMapper.updateById(one);
		if (update < 1) {
			throw new AiurtBootException("重新指派失败");
		}

		PatrolPool pool = this.baseMapper.selectById(one.getPatrolPoolId());

		userTaskService.removeUserTaskWork(Arrays.asList(one.getStaffIds().split(",")), pool.getId(), UserTaskConstant.USER_TASK_TYPE_1);

		//线路+站点名称
		String lineStationName = "";
		Station byId = this.stationService.getById(pool.getId());
		if (byId != null) {
			lineStationName = lineStationName.concat(StringUtils.isNotBlank(byId.getLineName()) ? byId.getLineName() : "").concat(StringUtils.isNotBlank(byId.getStationName()) ? byId.getStationName() : "");
		}

		//删除原来的待办事项 待办事项添加
		userTaskService.del(pool.getId(),UserTaskConstant.USER_TASK_TYPE_1);//删除原来待办事项
		UserTaskAddParam addParam = new UserTaskAddParam();
		addParam.setUserIds(param.getUserIds())
				.setRecordId(pool.getId())
				.setTitle(pool.getPatrolName())
				.setContent(StringUtils.isNotBlank(lineStationName) ? lineStationName : pool.getLineName())
				.setWorkCode(pool.getCode())
				.setType(UserTaskConstant.USER_TASK_TYPE_1)
				.setLevel(2);
		userTaskService.add(addParam);

		//发送app消息
		Message message = new Message();
		message.setTitle("消息通知").setContent("您有一条新的巡检任务!").setType(MessageConstant.MESSAGE_TYPE_0).setCode(id.toString());
		messageService.addMessage(MessageAddParam.builder().message(message).userIds(param.getUserIds()).build());

		return Result.ok();
	}

	@Override
	public Result<PatrolTask> appointDetail(HttpServletRequest req, Long poolId) {
		PatrolTask task = patrolTaskMapper.selectOne(new LambdaQueryWrapper<PatrolTask>().eq(PatrolTask::getPatrolPoolId, poolId).eq(PatrolTask::getDelFlag, CommonConstant.DEL_FLAG_0).last("limit 1"));

		return Result.ok(task);
	}


	/**
	 * 树形获取
	 *
	 * @param id       id
	 * @param parentId 父id
	 * @return {@code List<PoolTreeVO>}
	 */
	public List<PoolTreeVO> getTree(Long id, Long parentId) {
		List<PoolTreeVO> list = new ArrayList<>();

		List<PatrolPoolContent> contents = patrolPoolContentService.lambdaQuery()
				.eq(PatrolPoolContent::getPatrolPoolId, id)
				.eq(PatrolPoolContent::getParentId, parentId)
				.eq(PatrolPoolContent::getDelFlag, CommonConstant.DEL_FLAG_0).list();
		for (PatrolPoolContent content : contents) {
			PoolTreeVO vo = new PoolTreeVO();
			BeanUtils.copyProperties(content, vo);
			vo.setChildren(getTree(id, vo.getId()));
			list.add(vo);
		}
		return list;
	}
}
