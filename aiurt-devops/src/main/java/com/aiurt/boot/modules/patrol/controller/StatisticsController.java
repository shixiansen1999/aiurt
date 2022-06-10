package com.aiurt.boot.modules.patrol.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.manage.service.ISubsystemService;
import com.aiurt.boot.modules.patrol.param.StatisticsParam;
import com.aiurt.boot.modules.patrol.service.PatrolStatisticsService;
import com.aiurt.boot.modules.patrol.vo.statistics.PatrolStatisticsVO;
import com.aiurt.boot.modules.patrol.vo.statistics.SimpStatisticsVO;
import com.aiurt.boot.modules.patrol.vo.statistics.StatisticsListVO;
import com.aiurt.boot.modules.patrol.vo.statistics.StatisticsTitleVO;
import com.aiurt.boot.modules.system.entity.SysDepart;
import com.aiurt.boot.modules.system.service.ISysDepartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: StatisticsController
 * @author: Mr.zhao
 * @date: 2021/9/27 15:39
 */
@Slf4j
@Api(tags = "巡检统计")
@RestController
@RequestMapping("/patrol/statistics")
@RequiredArgsConstructor
public class StatisticsController {

	private final PatrolStatisticsService statisticsService;

	private final ISysDepartService sysDepartService;

	private final ISubsystemService subsystemService;

	private final IStationService stationService;


	@AutoLog(value = "巡检统计-统计标题块")
	@ApiOperation(value = "巡检统计-统计标题块", notes = "巡检统计-统计标题块")
	@GetMapping(value = "/title")
	public Result<PatrolStatisticsVO> title(StatisticsParam param) {
		PatrolStatisticsVO vo = new PatrolStatisticsVO();
		LocalDate now = LocalDate.now();

		param.setStartTime(param.getEndTime() == null ? now.atTime(0, 0, 0) : param.getEndTime().toLocalDate().atTime(0, 0, 0));
		param.setEndTime(param.getEndTime() == null ? now.atTime(23, 59, 59) : param.getEndTime().toLocalDate().atTime(23, 59, 59));


		if (param.getOrganizationId()!=null){
			List<String> list = new ArrayList<>();
			list.add(param.getOrganizationId());
			param.setDepartList(list);
		}else {
			//若无班组选择,则查询所有存在的班组
			List<SysDepart> list = sysDepartService.lambdaQuery().eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0)
					.select(SysDepart::getId).list();
			if (CollectionUtils.isEmpty(list)) {
				return Result.ok(vo);
			}
			List<SysDepart> departFilter = list.stream().filter(f -> {
				return !(f.getParentId() == null || StringUtils.isBlank(f.getParentId()));
			}).collect(Collectors.toList());
			list.removeAll(departFilter);
			param.setDepartList( list.stream().map(SysDepart::getId).collect(Collectors.toList()));
		}


		if (StringUtils.isBlank(param.getStationId())) {
			if (StringUtils.isNotBlank(param.getLineId())) {
				List<Station> stations = stationService.lambdaQuery()
						.eq(Station::getDelFlag, CommonConstant.DEL_FLAG_0)
						.select(Station::getId)
						.eq(Station::getLineId, param.getLineId()).list();
				if (CollectionUtils.isEmpty(stations)) {
					return Result.ok(vo);
				}
				param.setStationIds(stations.stream().map(Station::getId).collect(Collectors.toList()));
			} else {
				if (StringUtils.isBlank(param.getStationId()) && StringUtils.isBlank(param.getLineId())) {
					List<Station> stations = stationService.lambdaQuery()
							.eq(Station::getDelFlag, CommonConstant.DEL_FLAG_0)
							.select(Station::getId)
							.list();
					if (CollectionUtils.isEmpty(stations)) {
						return Result.ok(vo);
					}
					param.setStationIds(stations.stream().map(Station::getId).collect(Collectors.toList()));
				}
			}
		}else {
			List<Integer> list = new ArrayList<>();
			try {
				list.add(Integer.parseInt(param.getStationId()));
			} catch (NumberFormatException e) {
				return Result.ok(vo);
			}
			param.setStationIds(list);
		}




		//班组
		List<SysDepart> departList = sysDepartService.lambdaQuery()
				.eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0)
				.eq(SysDepart::getOrgType,2).list();
		Map<String, String> departMap = null;
		if (CollectionUtils.isNotEmpty(departList)){
			departMap = departList.stream().collect(Collectors.toMap(SysDepart::getId, SysDepart::getDepartName));
		}
		//系统
		List<Subsystem> systemNameList = subsystemService.list(new LambdaQueryWrapper<Subsystem>()
				.eq(Subsystem::getDelFlag, CommonConstant.DEL_FLAG_0)
				.select(Subsystem::getSystemCode, Subsystem::getSystemName)
		);
		Map<String, String> systemMap = null;
		if (CollectionUtils.isNotEmpty(systemNameList)){
			systemMap = systemNameList.stream().collect(Collectors.toMap(Subsystem::getSystemCode, Subsystem::getSystemName));
		}

		//查询标题块
		StatisticsTitleVO title = statisticsService.title(param);
		vo.setTitle(title != null ? title : new StatisticsTitleVO());

		List<SimpStatisticsVO> systemList = null;
		List<SimpStatisticsVO> teamList = null;
		List<StatisticsListVO> list = null;
		if (systemMap!=null) {
			//查询系统对比
			systemList = statisticsService.systemList(param, systemMap);
		}
		if (departMap!=null) {
			//查询班组对比
			teamList = statisticsService.teamList(param, departMap);
			//查询列表
			list = statisticsService.list(param, departMap);
		}

		vo.setSystemList(systemList==null?new ArrayList<>():systemList);
		vo.setTeamList(teamList==null?new ArrayList<>():teamList);
		vo.setList(list==null?new ArrayList<>():list);

		return Result.ok(vo);
	}



}
