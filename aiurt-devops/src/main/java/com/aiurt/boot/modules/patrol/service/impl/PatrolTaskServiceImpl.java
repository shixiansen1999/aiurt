package com.aiurt.boot.modules.patrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.exception.SwscException;
import com.swsc.copsms.modules.patrol.constant.PatrolConstant;
import com.swsc.copsms.modules.patrol.entity.*;
import com.swsc.copsms.modules.patrol.mapper.PatrolContentMapper;
import com.swsc.copsms.modules.patrol.mapper.PatrolMapper;
import com.swsc.copsms.modules.patrol.mapper.PatrolPoolMapper;
import com.swsc.copsms.modules.patrol.mapper.PatrolTaskMapper;
import com.swsc.copsms.modules.patrol.param.IgnoreTaskParam;
import com.swsc.copsms.modules.patrol.param.PatrolPoolParam;
import com.swsc.copsms.modules.patrol.param.TaskAddParam;
import com.swsc.copsms.modules.patrol.service.IPatrolPoolContentService;
import com.swsc.copsms.modules.patrol.service.IPatrolTaskReportService;
import com.swsc.copsms.modules.patrol.service.IPatrolTaskService;
import com.swsc.copsms.modules.patrol.utils.CopyUtils;
import com.swsc.copsms.modules.patrol.vo.PatrolPoolContentTreeVO;
import com.swsc.copsms.modules.patrol.vo.PatrolTaskVO;
import com.swsc.copsms.modules.patrol.vo.TaskDetailVO;
import com.swsc.copsms.modules.system.entity.SysDepart;
import com.swsc.copsms.modules.system.entity.SysUser;
import com.swsc.copsms.modules.system.mapper.SysDepartMapper;
import com.swsc.copsms.modules.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

	private final PatrolMapper patrolMapper;

	private final PatrolContentMapper patrolContentMapper;

	private final PatrolPoolMapper patrolPoolMapper;

	private final IPatrolPoolContentService patrolPoolContentService;

	private final IPatrolTaskReportService patrolTaskReportService;

	private final SysUserMapper sysUserMapper;

	private final SysDepartMapper sysDepartMapper;

	@Override
	public Result<?> pageList(HttpServletRequest req, Integer pageNo, Integer pageSize, PatrolPoolParam param) {
		if (StringUtils.isNotBlank(param.getName())) {
			SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>().like(SysUser.REAL_NAME, param.getName()));
			if (user != null) {
				param.setId(user.getId());
			}
		}
		//查询type传0非手动下发
		IPage<PatrolTaskVO> page = this.baseMapper.selectPageList(new Page<>(pageNo, pageSize), param);

		for (PatrolTaskVO record : page.getRecords()) {
			//用户名称
			if (org.apache.commons.lang.StringUtils.isNotBlank(record.getStaffIds())) {
				String[] userIds = record.getStaffIds().trim().split(PatrolConstant.SPL);
				List<SysUser> sysUsers = sysUserMapper.selectList(new QueryWrapper<SysUser>()
						.eq(SysUser.DEL_FLAG, PatrolConstant.UN_DEL_FLAG)
						.in(SysUser.ID, userIds));
				if (sysUsers != null && sysUsers.size() > 0) {
					record.setStaffName(org.apache.commons.lang.StringUtils.join(
							sysUsers.stream().map(SysUser::getRealname).collect(Collectors.toList()),
							PatrolConstant.SPL));
				}
			}
			//部门名称
			SysDepart sysDepart = sysDepartMapper.selectById(record.getOrganizationId());
			if (sysDepart != null) {
				record.setOrganizationName(sysDepart.getDepartName());
			}

			//漏检状态处理
			if (record.getIgnoreStatus() == 1) {
				if (StringUtils.isBlank(record.getIgnoreContent())) {
					//若为空则未处理
					record.setIgnoreStatus(0);
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


		List<Patrol> patrolList = patrolMapper.selectList(new QueryWrapper<Patrol>()
				.eq(Patrol.DEL_FLAG, PatrolConstant.UN_DEL_FLAG)
				.in(Patrol.ID, param.getPatrolIds()));
		//巡检标准map
		Map<Long, Patrol> patrolMap = patrolList.stream().collect(Collectors.toMap(Patrol::getId, l -> l));

		List<PatrolContent> patrolContentList = patrolContentMapper.selectList(new QueryWrapper<PatrolContent>()
				.eq(PatrolContent.DEL_FLAG, PatrolConstant.UN_DEL_FLAG)
				.in(PatrolContent.RECORD_ID, param.getPatrolIds())
		);
		//巡检项map
		Map<Long, List<PatrolContent>> patrolContentMap = patrolContentList.stream().collect(Collectors.groupingBy(PatrolContent::getRecordId));


		for (Long patrolId : param.getPatrolIds()) {
			Patrol patrol = patrolMap.get(patrolId);
			if (patrol == null) {
				throw new SwscException("id:" + patrolId + "未找到巡检规范表数据");
			}
			List<PatrolContent> contentList = patrolContentMap.get(patrolId);
			if (contentList == null || contentList.size() < 1) {
				throw new SwscException("id:" + patrolId + "未找到巡检项数据");
			}

			for (String id : ids) {
				//巡检池数据
				PatrolPool pool = new PatrolPool();
				//手动下发任务
				pool.setType(1)
						.setStatus(0)
						.setCounts(1)
						.setDelFlag(PatrolConstant.UN_DEL_FLAG)
						.setTactics(4)
						.setSystemType(patrol.getTypes())
						.setPatrolName(patrol.getTitle())
						//执行时间
						.setExecutionTime(param.getTime())
						.setCreateTime(date)
						.setUpdateTime(date)
						.setNote(param.getNote())
						.setOrganizationId(id)
				;

				int insert = patrolPoolMapper.insert(pool);
				if (insert < 1) {
					throw new SwscException("id:" + patrolId + "巡检池数据保存失败");
				}
				//插入巡检任务项数据
				List<PatrolPoolContent> contents = CopyUtils.copyPatrolPoolContent(contentList, pool.getId());
				if (!patrolPoolContentService.saveBatch(contents)) {
					throw new SwscException("id:" + patrolId + "巡检项数据保存失败");
				}
			}
		}

		return Result.ok();
	}

	@Override
	public Result<?> ignoreTasks(HttpServletRequest req, IgnoreTaskParam param) {



		int i = this.baseMapper.updateById(new PatrolTask().setId(param.getId()).setIgnoreContent(param.getContent()));
		if (i < 1) {
			throw new SwscException("处理失败");
		}

		return Result.ok();
	}

	@Override
	public Result<?> detail(HttpServletRequest req, Long id, String code) {

		if (id==null || StringUtils.isBlank(code)){
			return Result.error("id与code不能同时为空");
		}

		PatrolTask patrolTask = null;
		if (id != null) {
			patrolTask = this.baseMapper.selectById(id);
		} else {
			patrolTask = this.baseMapper.selectOne(new QueryWrapper<PatrolTask>()
					.eq(PatrolTask.CODE, code.trim())
					.eq(PatrolTask.DEL_FLAG, PatrolConstant.UN_DEL_FLAG)
					.last("limit 1"));
		}

		if (patrolTask == null) {
			return Result.error("未查询到此条信息");
		}
		PatrolPool pool = patrolPoolMapper.selectById(patrolTask.getPatrolPoolId());


		TaskDetailVO vo = new TaskDetailVO();
		//标题
		vo.setTitle(pool.getPatrolName());
		//提交时间
		vo.setSubmitTime(patrolTask.getSubmitTime());

		//巡检人及部门
		if (StringUtils.isNotBlank(patrolTask.getStaffIds())) {
			String[] userIds = patrolTask.getStaffIds().trim().split(PatrolConstant.SPL);
			List<SysUser> sysUsers = sysUserMapper.selectList(new QueryWrapper<SysUser>()
					.eq(SysUser.DEL_FLAG, PatrolConstant.UN_DEL_FLAG)
					.in(SysUser.ID, userIds));
			if (sysUsers != null && sysUsers.size() > 0) {
				vo.setStaffName(org.apache.commons.lang.StringUtils.join(
						sysUsers.stream().map(SysUser::getRealname).collect(Collectors.toList()),
						PatrolConstant.SPL));
			}
		}
		SysDepart sysDepart = sysDepartMapper.selectById(pool.getOrganizationId());
		if (sysDepart != null) {
			vo.setOrganizationName(sysDepart.getDepartName());
		}

		//树状查询
		Result<List<PatrolPoolContentTreeVO>> tree = patrolTaskReportService.tree(req, patrolTask.getId());
		List<PatrolPoolContentTreeVO> list = tree.getResult();
		vo.setList(list);

		return Result.ok(vo);
	}


}
