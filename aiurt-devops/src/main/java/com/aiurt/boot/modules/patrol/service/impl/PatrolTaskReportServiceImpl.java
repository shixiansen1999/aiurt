package com.aiurt.boot.modules.patrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.exception.SwscException;
import com.swsc.copsms.modules.patrol.constant.PatrolConstant;
import com.swsc.copsms.modules.patrol.entity.PatrolTask;
import com.swsc.copsms.modules.patrol.entity.PatrolTaskEnclosure;
import com.swsc.copsms.modules.patrol.entity.PatrolTaskReport;
import com.swsc.copsms.modules.patrol.mapper.PatrolPoolContentMapper;
import com.swsc.copsms.modules.patrol.mapper.PatrolTaskEnclosureMapper;
import com.swsc.copsms.modules.patrol.mapper.PatrolTaskMapper;
import com.swsc.copsms.modules.patrol.mapper.PatrolTaskReportMapper;
import com.swsc.copsms.modules.patrol.param.ReportAllParam;
import com.swsc.copsms.modules.patrol.param.ReportOneParam;
import com.swsc.copsms.modules.patrol.service.IPatrolTaskEnclosureService;
import com.swsc.copsms.modules.patrol.service.IPatrolTaskReportService;
import com.swsc.copsms.modules.patrol.vo.PatrolPoolContentTreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

	private final PatrolTaskMapper patrolTaskMapper;

	private final PatrolTaskEnclosureMapper patrolTaskEnclosureMapper;

	private final IPatrolTaskEnclosureService patrolTaskEnclosureService;

	@Override
	public Result<List<PatrolPoolContentTreeVO>> tree(HttpServletRequest req, Long id) {

		PatrolTask task = patrolTaskMapper.selectById(id);

		Long patrolPoolId = task.getPatrolPoolId();

		List<PatrolPoolContentTreeVO> tree = getTree(patrolPoolId, task.getId(), 0L);

		Result<List<PatrolPoolContentTreeVO>> result = new Result<>();
		result.setSuccess(true);
		result.setCode(200);
		result.setResult(tree);
		return result;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Result<?> oneReport(HttpServletRequest req, ReportOneParam param) {

		PatrolTaskReport report = this.baseMapper.selectOne(new QueryWrapper<PatrolTaskReport>()
				.eq(PatrolTaskReport.DEL_FLAG, PatrolConstant.UN_DEL_FLAG)
				.eq(PatrolTaskReport.PATROL_TASK_ID, param.getId())
				.eq(PatrolTaskReport.PATROL_POOL_CONTENT_ID, param.getContentId()));

		if (report != null && report.getSaveStatus() == 1) {
			throw new SwscException("巡检项已提交,无法更改");
		}

		boolean flag = false;

		if (report == null) {
			report = new PatrolTaskReport();
			flag = true;
		}

		report.setDelFlag(0)
				.setNote(param.getNote())
				.setPatrolTaskId(param.getId())
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
		//巡检报告
		List<PatrolTaskReport> reports = this.baseMapper.selectList(new QueryWrapper<PatrolTaskReport>()
				.eq(PatrolTaskReport.DEL_FLAG, PatrolConstant.UN_DEL_FLAG)
				.eq(PatrolTaskReport.PATROL_TASK_ID, param.getId()));

		Map<Long, PatrolTaskReport> reportMap = null;
		if (reports != null && reports.size() > 0) {
			reportMap = reports.stream().collect(Collectors.toMap(PatrolTaskReport::getPatrolPoolContentId, p -> p));
		}
		List<ReportOneParam> list = param.getList();
		for (ReportOneParam oneParam : list) {
			if (reportMap != null) {
				PatrolTaskReport report = reportMap.get(oneParam.getContentId());
				if (report != null) {
					if (!report.getSaveStatus().equals(param.getStatus())) {
						report.setSaveStatus(param.getStatus());
						int i = this.baseMapper.updateById(report);
						if (i < 1) {
							throw new SwscException("保存失败");
						}
					}
					continue;
				}
			}

			PatrolTaskReport report = new PatrolTaskReport();
			report.setDelFlag(0)
					.setNote(oneParam.getNote())
					.setPatrolTaskId(param.getId())
					.setPatrolPoolContentId(oneParam.getContentId());
			int insert = this.baseMapper.insert(report);
			if (insert < 1) {
				throw new SwscException("保存失败");
			}

			setUrlList(oneParam, report);

		}


		return Result.ok();
	}

	/**
	 * 设置url列表
	 *
	 * @param oneParam 一个参数
	 * @param report   报告
	 */
	private void setUrlList(ReportOneParam oneParam, PatrolTaskReport report) {
		List<String> urlList = oneParam.getUrlList();
		if (urlList != null && urlList.size() > 0) {
			List<PatrolTaskEnclosure> taskList = new ArrayList<>();
			for (String s : urlList) {
				PatrolTaskEnclosure enclosure = new PatrolTaskEnclosure();
				enclosure.setDelFlag(0)
						.setParentId(report.getId())
						.setUrl(s)
						.setType(PatrolConstant.DB_PATROL_TASK_REPORT);
				taskList.add(enclosure);
			}
			boolean b = patrolTaskEnclosureService.saveBatch(taskList);
			if (!b) {
				throw new SwscException("附件保存失败,请稍后重试");
			}
		}
	}

	@Override
	public Result<?> sign(HttpServletRequest req, Long id, String url) {
		int patrolTask = patrolTaskEnclosureMapper.insert(new PatrolTaskEnclosure()
				.setDelFlag(PatrolConstant.UN_DEL_FLAG)
				.setType(PatrolConstant.DB_PATROL_TASK)
				.setUrl(url));
		if (patrolTask < 1) {
			throw new SwscException("签字失败,请稍后重试");
		}

		return Result.ok();
	}

	@Override
	public boolean callback(Long taskId, Long poolContentId, String code) {
		PatrolTaskReport report = this.baseMapper.selectOne(new QueryWrapper<PatrolTaskReport>()
				.eq(PatrolTaskReport.DEL_FLAG, PatrolConstant.UN_DEL_FLAG)
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
					.setDelFlag(PatrolConstant.UN_DEL_FLAG)
					.setSaveStatus(1);
			flag = true;
		}
		//回调参数设置
		report.setStatus(3)
				.setNote(code);
		int i = 0;
		if (flag) {
			i = this.baseMapper.insert(report);
		} else {
			i = this.baseMapper.updateById(report);
		}
		//修改: 失败false 成功true
		if (i < 1) {
			return false;
		}
		return true;
	}

	@Override
	public Result<?> getUrl(HttpServletRequest req, Long id) {
		List<String> list = new ArrayList<>();

		List<PatrolTaskEnclosure> enclosureList = patrolTaskEnclosureMapper.selectList(new QueryWrapper<PatrolTaskEnclosure>()
				.eq(PatrolTaskEnclosure.DEL_FLAG, PatrolConstant.UN_DEL_FLAG)
				.eq(PatrolTaskEnclosure.TYPE, PatrolConstant.DB_PATROL_TASK_REPORT)
				.eq(PatrolTaskEnclosure.PARENT_ID, id)
				.orderByAsc(PatrolTaskEnclosure.ID)
		);

		if (enclosureList != null && enclosureList.size() > 0) {
			list = enclosureList.stream().map(PatrolTaskEnclosure::getUrl).collect(Collectors.toList());
		}
		return Result.ok(list);
	}


	/**
	 * 树形获取
	 *
	 * @param id       id
	 * @param taskId   任务id
	 * @param parentId 父id
	 * @return {@link List}<{@link PatrolPoolContentTreeVO}>
	 */
	public List<PatrolPoolContentTreeVO> getTree(Long id, Long taskId, Long parentId) {
		List<PatrolPoolContentTreeVO> list = new ArrayList<>();

		List<PatrolPoolContentTreeVO> contents = patrolPoolContentMapper.selectTreeDetails(id, taskId, parentId);

		for (PatrolPoolContentTreeVO content : contents) {

			content.setChildren(getTree(id, taskId, content.getId()));
			list.add(content);
		}
		return list;
	}


}
