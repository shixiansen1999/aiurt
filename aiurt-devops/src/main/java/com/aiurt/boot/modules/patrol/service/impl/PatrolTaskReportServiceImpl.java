package com.aiurt.boot.modules.patrol.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.exception.SwscException;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.modules.apphome.constant.UserTaskConstant;
import com.aiurt.boot.modules.apphome.service.UserTaskService;
import com.aiurt.boot.modules.patrol.constant.PatrolConstant;
import com.aiurt.boot.modules.patrol.entity.*;
import com.aiurt.boot.modules.patrol.mapper.*;
import com.aiurt.boot.modules.patrol.param.*;
import com.aiurt.boot.modules.patrol.service.IPatrolTaskEnclosureService;
import com.aiurt.boot.modules.patrol.service.IPatrolTaskReportService;
import com.aiurt.boot.modules.patrol.vo.PatrolPoolContentOneTreeVO;
import com.aiurt.boot.modules.patrol.vo.PatrolPoolContentTreeVO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 巡检人员巡检项报告表
 * @Author: swsc
 * @Date: 2021-09-21
 * @Version: V1.0
 */
@Service
@RequiredArgsConstructor
public class PatrolTaskReportServiceImpl extends ServiceImpl<PatrolTaskReportMapper, PatrolTaskReport> implements IPatrolTaskReportService {

	private final PatrolPoolContentMapper patrolPoolContentMapper;

	private final PatrolPoolMapper patrolPoolMapper;

	private final PatrolTaskMapper patrolTaskMapper;

	private final PatrolTaskEnclosureMapper patrolTaskEnclosureMapper;

	private final IPatrolTaskEnclosureService patrolTaskEnclosureService;

	private final UserTaskService userTaskService;

	@Override
	public Result<List<PatrolPoolContentTreeVO>> tree(HttpServletRequest req, Long id, TreeParam param) {

		String title = null;
		Long poolId = null;
		Integer flag = null;
		if (param != null) {
			title = param.getTitle();
			poolId = param.getPoolId();
			flag = param.getFlag();
		}
		//查询
		GetTreeParam getTreeParam = new GetTreeParam();
		getTreeParam.setCount(1);
		List<PatrolPoolContentTreeVO> tree = null;
		PatrolTask task = patrolTaskMapper.selectById(id);
		if (poolId == null) {
			if (task == null) {
				throw new SwscException("未找到此项数据");
			}
			getTreeParam.setPoolId(task.getPatrolPoolId())
					.setTaskId(task.getId())
					.setParentId(0L)
					.setTitle(title)
					.setFlag(flag);
			tree = patrolPoolContentMapper.selectTreeDetails(getTreeParam.getPoolId(),
					getTreeParam.getTaskId(),
					getTreeParam.getParentId(),
					getTreeParam.getTitle());
		} else {
			getTreeParam.setPoolId(poolId)
					.setTaskId(null)
					.setParentId(0L)
					.setTitle(title)
					.setFlag(flag);
			tree = patrolPoolContentMapper.selectTreeDetails(getTreeParam.getPoolId(),
					getTreeParam.getTaskId(),
					getTreeParam.getParentId(),
					getTreeParam.getTitle());
		}


		Map<Long, List<PatrolPoolContentTreeVO>> listMap = tree.stream().collect(Collectors.groupingBy(PatrolPoolContentTreeVO::getParentId));
		List<PatrolPoolContentTreeVO> patrolPoolContentTreeList = new ArrayList<>();
		//生成树
		if (CollectionUtils.isNotEmpty(Collections.singleton(listMap))) {
			patrolPoolContentTreeList = listMap.get(CommonConstant.LONG_NUM_STR_0);
			if (patrolPoolContentTreeList != null) {
				for (PatrolPoolContentTreeVO treeVO : patrolPoolContentTreeList) {
					if (treeVO.getSaveStatus() == null) {
						treeVO.setSaveStatus(1);
						treeVO.setReportStatus(treeVO.getType());
					}
					treeVO.setFlag(getTreeParam.getFlag()).setCount(getTreeParam.getCount());
					//如果已生成故障上报, 设置为异常项,并且不能改变
					if (Objects.equals(treeVO.getReportStatus(), PatrolConstant.REPORT_STATUS_3)) {
						treeVO.setFlag(PatrolConstant.DISABLE).setReportStatus(PatrolConstant.REPORT_STATUS_2);
					}
					//给予子项赋值
					treeVO.setChildren(setTree(getTreeParam, listMap, treeVO));
				}
			} else {
				patrolPoolContentTreeList = new ArrayList<>();
			}
		}

		//筛选
		if (CollectionUtils.isNotEmpty(patrolPoolContentTreeList) && StringUtils.isNotBlank(param.getTitle())) {
			List<PatrolPoolContentTreeVO> filterTreeList = new ArrayList<>();
			for (PatrolPoolContentTreeVO vo : patrolPoolContentTreeList) {
				if (vo != null) {
					if (CollectionUtils.isNotEmpty(vo.getChildren())) {
						List<PatrolPoolContentTreeVO> treeVO = filterTree(vo, param.getTitle());
						if (treeVO != null) {
							vo.setChildren(treeVO);
							filterTreeList.add(vo);
						}
					} else if (vo.getContent().contains(param.getTitle())) {
						filterTreeList.add(vo);
					}
				}
			}

			patrolPoolContentTreeList = filterTreeList;
		}

		Result<List<PatrolPoolContentTreeVO>> result = new Result<>();
		result.setSuccess(true);
		result.setCode(200);
		result.setResult(patrolPoolContentTreeList);
		return result;
	}


	/**
	 * 设置树
	 *
	 * @param listMap 地图列表
	 * @param treeVO  树签证官
	 * @return {@code List<PatrolPoolContentTreeVO>}
	 */
	private List<PatrolPoolContentTreeVO> setTree(GetTreeParam getTreeParam, Map<Long, List<PatrolPoolContentTreeVO>> listMap, PatrolPoolContentTreeVO treeVO) {
		if (treeVO != null && treeVO.getCode() != null) {
			List<PatrolPoolContentTreeVO> treeList = listMap.get(treeVO.getCode());
			if (CollectionUtils.isNotEmpty(treeList)) {
				for (PatrolPoolContentTreeVO vo : treeList) {
					if (vo.getSaveStatus() == null) {
						vo.setSaveStatus(1);
						vo.setReportStatus(vo.getType());
					}
					vo.setFlag(getTreeParam.getFlag()).setCount(getTreeParam.getCount());
					//如果已生成故障上报, 设置为异常项,并且不能改变
					if (Objects.equals(vo.getReportStatus(), PatrolConstant.REPORT_STATUS_3)) {
						vo.setFlag(PatrolConstant.DISABLE).setReportStatus(PatrolConstant.REPORT_STATUS_2);
					}
					vo.setChildren(setTree(getTreeParam, listMap, vo));
				}
				return treeList;
			}
		}

		return new ArrayList<>();
	}


	/**
	 * 过滤树
	 *
	 * @param vo   签证官
	 * @param name 的名字
	 * @return {@code List<PatrolPoolContentTreeVO>}
	 */
	private List<PatrolPoolContentTreeVO> filterTree(PatrolPoolContentTreeVO vo, String name) {
		if (vo != null) {
			if (CollectionUtils.isNotEmpty(vo.getChildren())) {
				List<PatrolPoolContentTreeVO> list = new ArrayList<>();
				for (PatrolPoolContentTreeVO child : vo.getChildren()) {
					List<PatrolPoolContentTreeVO> treeVO = filterTree(child, name);

					if (treeVO == null) {
						if (child.getContent().contains(name)) {
							child.setChildren(null);
							list.add(child);
						}
					} else {
						if (treeVO.size() > 0) {
							child.setChildren(treeVO);
							list.add(child);
						} else {
							if (child.getContent().contains(name)) {
								child.setChildren(null);
								list.add(child);
							}
						}
					}
				}
				return list;
			} else {
				return new ArrayList<>();
			}
		}
		return null;
	}


	@Override
	public PatrolPoolContentOneTreeVO getOneTree(OneTreeParam param) {
		PatrolPoolContentOneTreeVO vo = new PatrolPoolContentOneTreeVO();
		Long poolId = param.getPoolId();
		Long taskId = param.getTaskId();
		if (poolId == null) {
			if (taskId == null) {
				throw new SwscException("未找到此项数据");
			}
			PatrolTask task = this.patrolTaskMapper.selectById(param.getTaskId());
			if (task != null) {
				param.setPoolId(task.getPatrolPoolId());
				poolId = task.getPatrolPoolId();
			}
		}
		vo = getOneTreeFun(param);


		return vo;
	}


	public PatrolPoolContentOneTreeVO getOneTreeFun(OneTreeParam getTreeParam) {
		Long id = getTreeParam.getTypeId();
		List<Long> ids = new ArrayList<>();
		ids.add(id);
		PatrolPoolContent content = this.patrolPoolContentMapper.selectById(id);
		Long poolId = content.getPatrolPoolId();
		Long code = content.getParentId();
		while (code != 0) {
			PatrolPoolContent poolContent = this.patrolPoolContentMapper.selectOne(new LambdaQueryWrapper<PatrolPoolContent>()
					.eq(PatrolPoolContent::getDelFlag, CommonConstant.DEL_FLAG_0)
					.eq(PatrolPoolContent::getPatrolPoolId, poolId)
					.eq(PatrolPoolContent::getCode, code)
					.last("limit 1")
			);
			code = poolContent.getParentId();
			ids.add(poolContent.getId());
		}

		List<PatrolPoolContentOneTreeVO> vos = patrolPoolContentMapper.selectOneTreeDetails(getTreeParam.getPoolId(), getTreeParam.getTaskId(), ids);

		PatrolPoolContentOneTreeVO vo = null;
		if (vos != null && vos.size() > 0) {
			vo = vos.get(vos.size() - 1);
			if (vos.size() > 1) {
				vo.setChildren(setChildren(vo, vos, vos.size() - 2));
			}
		}
		if (vo != null) {
			vo.setTempLen(vos.size());
		}
		return vo;
	}


	private PatrolPoolContentOneTreeVO setChildren(PatrolPoolContentOneTreeVO vo, List<PatrolPoolContentOneTreeVO> vos, int i) {
		PatrolPoolContentOneTreeVO treeVO = vos.get(i--);
		if (i >= 0) {
			vo.setChildren(setChildren(treeVO, vos, i));
		} else if (i == -1) {
			vo.setChildren(treeVO);
		}
		return treeVO;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Result<?> oneReport(HttpServletRequest req, ReportOneParam param) {

		PatrolTaskReport report = this.baseMapper.selectOne(new QueryWrapper<PatrolTaskReport>()
				.eq(PatrolTaskReport.DEL_FLAG, CommonConstant.DEL_FLAG_0)
				.eq(PatrolTaskReport.PATROL_TASK_ID, param.getTaskId())
				.eq(PatrolTaskReport.PATROL_POOL_CONTENT_ID, param.getContentId()));

		if (report != null && Objects.equals(report.getSaveStatus(), PatrolConstant.ENABLE)) {
			throw new SwscException("巡检项已提交,无法更改");
		}

		boolean flag = false;

		if (report == null) {
			report = new PatrolTaskReport();
			flag = true;
		}

		report.setDelFlag(CommonConstant.DEL_FLAG_0)
				.setSaveStatus(param.getSaveStatus())
				.setStatus(param.getReportStatus())
				.setNote(param.getNote())
				.setUnNote(param.getUnNote())
				.setPatrolTaskId(param.getTaskId())
				.setPatrolPoolContentId(param.getContentId());

		int i = 0;

		//插入或修改
		if (flag) {
			i = this.baseMapper.insert(report);
		} else {
			i = this.baseMapper.updateById(report);
		}
		if (i < 1) {
			throw new SwscException("保存/提交失败,请稍后重试");
		}

		setUrlList(param, report);

		return Result.ok();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Result<?> reportAll(HttpServletRequest req, ReportAllParam param) {

		PatrolTask task = patrolTaskMapper.selectById(param.getTaskId());
		if (task == null) {
			throw new SwscException("未找到任务");
		}
		if (task.getStatus() != null && task.getStatus() == 1) {
			throw new SwscException("已完成,请勿重复提交");
		}

		PatrolPool pool = patrolPoolMapper.selectById(task.getPatrolPoolId());

		//巡检报告
		List<PatrolTaskReport> reports = this.baseMapper.selectList(new LambdaQueryWrapper<PatrolTaskReport>()
				.eq(PatrolTaskReport::getDelFlag, CommonConstant.DEL_FLAG_0)
				.eq(PatrolTaskReport::getPatrolTaskId, param.getTaskId()));

		//删除图片
		patrolTaskEnclosureMapper.delete(new LambdaQueryWrapper<PatrolTaskEnclosure>()
				.eq(PatrolTaskEnclosure::getType, PatrolConstant.DB_PATROL_TASK)
				.eq(PatrolTaskEnclosure::getParentId, param.getTaskId()));
		if (CollectionUtils.isNotEmpty(param.getUrlList())) {
			param.getUrlList().forEach(l -> {
				if (StringUtils.isNotBlank(l)) {
					int insert = patrolTaskEnclosureMapper.insert(new PatrolTaskEnclosure()
							.setUrl(l)
							.setType(PatrolConstant.DB_PATROL_TASK)
							.setDelFlag(CommonConstant.DEL_FLAG_0)
							.setParentId(param.getTaskId()));
					if (insert < 1) {
						throw new SwscException("保存失败");
					}
				}
			});
		}

		Map<Long, PatrolTaskReport> reportMap = null;
		if (CollectionUtils.isNotEmpty(reports)) {
			reportMap = reports.stream().collect(Collectors.toMap(PatrolTaskReport::getPatrolPoolContentId, p -> p));
		}

		List<ReportOneParam> list = param.getList();
		for (ReportOneParam oneParam : list) {
			saveTree(oneParam, reportMap, param);
		}

		if(param.getStatus()==1){
			if(TextUtils.isEmpty(param.getUrl())){
				throw new SwscException("请上传签名");
			}
			//签名保存
			int patrolTask = patrolTaskEnclosureMapper.insert(new PatrolTaskEnclosure()
					.setDelFlag(CommonConstant.DEL_FLAG_0)
					.setParentId(task.getId())
					//签名
					.setType(PatrolConstant.DB_PATROL_TASK + PatrolConstant.SIGN)
					.setUrl(param.getUrl()));
			if (patrolTask < 1) {
				this.baseMapper.update(new PatrolTaskReport().setSaveStatus(PatrolConstant.DISABLE), new QueryWrapper<PatrolTaskReport>()
						.eq(PatrolTaskReport.PATROL_TASK_ID, task.getId()));
				throw new SwscException("签字失败,请稍后重试");
			}

			this.baseMapper.update(new PatrolTaskReport().setSaveStatus(PatrolConstant.ENABLE), new QueryWrapper<PatrolTaskReport>()
					.eq(PatrolTaskReport.PATROL_TASK_ID, task.getId()));

			//查询是否有异常项或故障项
			PatrolTaskReport one = this.baseMapper.selectOne(new LambdaQueryWrapper<PatrolTaskReport>().eq(PatrolTaskReport::getPatrolTaskId, task.getId())
					.and(query -> query.eq(PatrolTaskReport::getStatus, PatrolConstant.REPORT_STATUS_2)
							.or().eq(PatrolTaskReport::getStatus, PatrolConstant.REPORT_STATUS_3))
					.last("limit 1")
					.select(PatrolTaskReport::getId)
			);
			if (one!=null){//改变任务状态
				task.setWarningStatus(PatrolConstant.ENABLE);
			}
			LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			task.setStatus(1).setSubmitTime(new Date()).setSubmitUserId(user.getId()).setSubmitUserName(user.getRealname());
			patrolTaskMapper.updateById(task);
		}

		//待办事项改为完成
		if (ObjectUtil.equal(param.getStatus(), PatrolConstant.ENABLE)) {
			userTaskService.complete(Arrays.asList(task.getStaffIds().split(PatrolConstant.SPL)), pool.getId(), UserTaskConstant.USER_TASK_TYPE_1);
		}

		return Result.ok();
	}

	/**
	 * 保存树
	 *
	 * @param oneParam  一个参数
	 * @param reportMap 报告图
	 * @param param     参数
	 * @return boolean
	 */
	private boolean saveTree(ReportOneParam oneParam, Map<Long, PatrolTaskReport> reportMap, ReportAllParam param) {
		boolean flag = false;
		if (CollectionUtils.isNotEmpty(oneParam.getChildren())) {
			for (ReportOneParam child : oneParam.getChildren()) {
				saveTree(child, reportMap, param);
			}
		}
		if (reportMap != null) {
			PatrolTaskReport report = reportMap.get(oneParam.getContentId());
			if (report != null) {
				report.setDelFlag(CommonConstant.DEL_FLAG_0)
						.setPatrolTaskId(param.getTaskId())
						.setPatrolPoolContentId(oneParam.getContentId());
				//未巡检
				if (PatrolConstant.DISABLE.equals(report.getStatus())) {
					report.setNote(oneParam.getNote());
				}

				//保存状态 0.保存 1.提交
				if (PatrolConstant.DISABLE.equals(report.getSaveStatus())) {//保存
					report.setSaveStatus(param.getStatus());
					report.setStatus(oneParam.getReportStatus());
				}else{//提交
					if (PatrolConstant.REPORT_STATUS_0.equals(report.getStatus())){
						if (StringUtils.isBlank(report.getNote())){
							throw new SwscException("填写内容不能为空");
						}
					}
				}

				int i = this.baseMapper.updateById(report);
				if (i < 1) {
					throw new SwscException("保存失败");
				}
				if (Objects.equals(report.getStatus(),PatrolConstant.DISABLE)) {
					setUrlList(oneParam, report);
				}
				return true;
			}
		}

		if (PatrolConstant.REPORT_STATUS_0.equals(oneParam.getReportStatus())){
			if (StringUtils.isBlank(oneParam.getNote())){
				throw new SwscException("填写内容不能为空");
			}
		}

		//巡检任务上报
		PatrolTaskReport report = new PatrolTaskReport();
		report.setDelFlag(CommonConstant.DEL_FLAG_0)
				.setSaveStatus(param.getStatus())
				.setStatus(oneParam.getReportStatus())
				.setNote(oneParam.getNote())
				.setUnNote(oneParam.getUnNote())
				.setPatrolTaskId(param.getTaskId())
				.setPatrolPoolContentId(oneParam.getContentId());
		int insert = this.baseMapper.insert(report);
		if (insert < 1) {
			throw new SwscException("保存失败");
		}
		if (Objects.equals(report.getStatus(),PatrolConstant.DISABLE)) {
			setUrlList(oneParam, report);
		}

		return true;
	}


	/**
	 * 设置url列表
	 *
	 * @param oneParam 一个参数
	 * @param report   报告
	 */
	private void setUrlList(ReportOneParam oneParam, PatrolTaskReport report) {
		patrolTaskEnclosureService.remove(new LambdaQueryWrapper<PatrolTaskEnclosure>()
				.eq(PatrolTaskEnclosure::getParentId, report.getId())
				.eq(PatrolTaskEnclosure::getType, PatrolConstant.DB_PATROL_TASK_REPORT));

		List<String> urlList = oneParam.getUrlList();
		if (urlList != null && urlList.size() > 0) {
			List<PatrolTaskEnclosure> taskList = new ArrayList<>();
			for (String s : urlList) {
				if (StringUtils.isNotBlank(s)) {
					PatrolTaskEnclosure enclosure = new PatrolTaskEnclosure();
					enclosure.setDelFlag(CommonConstant.DEL_FLAG_0)
							.setParentId(report.getId())
							.setUrl(s)
							.setType(PatrolConstant.DB_PATROL_TASK_REPORT);
					taskList.add(enclosure);
				}
			}
			if (taskList.size() > 0) {
				boolean b = patrolTaskEnclosureService.saveBatch(taskList);
				if (!b) {
					throw new SwscException("附件保存失败,请稍后重试");
				}
			}
		}
	}

	@Override
	public Result<?> sign(HttpServletRequest req, ReportSignParam param) {
		String url = param.getUrl();
		Long id = param.getId();
		int patrolTask = patrolTaskEnclosureMapper.insert(new PatrolTaskEnclosure()
				.setDelFlag(CommonConstant.DEL_FLAG_0)
				.setParentId(id)
				//签名
				.setType(PatrolConstant.DB_PATROL_TASK + PatrolConstant.SIGN)
				.setUrl(url));
		if (patrolTask < 1) {
			this.baseMapper.update(new PatrolTaskReport().setSaveStatus(PatrolConstant.DISABLE), new QueryWrapper<PatrolTaskReport>()
					.eq(PatrolTaskReport.PATROL_TASK_ID, id));
			throw new SwscException("签字失败,请稍后重试");
		}
		this.baseMapper.update(new PatrolTaskReport().setSaveStatus(PatrolConstant.ENABLE), new QueryWrapper<PatrolTaskReport>()
				.eq(PatrolTaskReport.PATROL_TASK_ID, id));

		//查询是否有异常项或故障项
		PatrolTaskReport one = this.baseMapper.selectOne(new LambdaQueryWrapper<PatrolTaskReport>().eq(PatrolTaskReport::getPatrolTaskId, id)
				.and(query -> query.eq(PatrolTaskReport::getStatus, PatrolConstant.REPORT_STATUS_2)
						.or().eq(PatrolTaskReport::getStatus, PatrolConstant.REPORT_STATUS_3))
				.last("limit 1")
				.select(PatrolTaskReport::getId)
		);


		//改变任务状态
		PatrolTask task = patrolTaskMapper.selectById(id);
		if (one!=null){
			task.setWarningStatus(PatrolConstant.ENABLE);
		}
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		task.setStatus(1).setSubmitTime(new Date()).setSubmitUserId(user.getId()).setSubmitUserName(user.getRealname());
		patrolTaskMapper.updateById(task);
		//改变待办事项状态
		userTaskService.complete(Arrays.asList(task.getStaffIds().split(PatrolConstant.SPL)), task.getPatrolPoolId(), UserTaskConstant.USER_TASK_TYPE_1);

		return Result.ok();
	}

	@Override
	public boolean callback(Long taskId, Long poolContentId, String code) {
		PatrolTaskReport report = this.baseMapper.selectOne(new QueryWrapper<PatrolTaskReport>()
				.eq(PatrolTaskReport.DEL_FLAG, CommonConstant.DEL_FLAG_0)
				.eq(PatrolTaskReport.PATROL_TASK_ID, taskId)
				.eq(PatrolTaskReport.PATROL_POOL_CONTENT_ID, poolContentId)
				.last("limit 1"));
		//状态
		boolean flag = false;
		//若为空则新建
		if (report == null) {
			report = new PatrolTaskReport();
			report.setPatrolTaskId(taskId)
					.setPatrolPoolContentId(poolContentId)
					.setDelFlag(CommonConstant.DEL_FLAG_0)
					.setSaveStatus(PatrolConstant.ENABLE);
			flag = true;
		}
		//回调参数设置
		report.setStatus(PatrolConstant.REPORT_STATUS_3).setSaveStatus(PatrolConstant.ENABLE)
				.setCode(code).setNote("已生成故障上报!");
		int i = 0;
		if (flag) {
			i = this.baseMapper.insert(report);
		} else {
			i = this.baseMapper.updateById(report);
		}
		PatrolTask task = patrolTaskMapper.selectById(taskId);
		task.setErrorStatus(1);
		//修改: 失败false 成功true
		if (i < 1) {
			return false;
		}
		return true;
	}

	@Override
	public Result<?> getUrl(HttpServletRequest req, UrlParam param) {
		List<String> list = new ArrayList<>();

		List<PatrolTaskEnclosure> enclosureList = patrolTaskEnclosureMapper.selectList(new QueryWrapper<PatrolTaskEnclosure>()
				.eq(PatrolTaskEnclosure.DEL_FLAG, CommonConstant.DEL_FLAG_0)
				.eq(PatrolTaskEnclosure.TYPE, param.getName())
				.eq(PatrolTaskEnclosure.PARENT_ID, param.getId())
				.orderByAsc(PatrolTaskEnclosure.ID)
		);

		if (enclosureList != null && enclosureList.size() > 0) {
			list = enclosureList.stream().map(PatrolTaskEnclosure::getUrl).collect(Collectors.toList());
		}
		return Result.ok(list);
	}


}
