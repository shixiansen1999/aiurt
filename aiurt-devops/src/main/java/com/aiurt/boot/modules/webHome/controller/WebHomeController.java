package com.aiurt.boot.modules.webHome.controller;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.modules.manage.entity.SpecialSituation;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.aiurt.boot.modules.manage.service.ISpecialSituationService;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.manage.service.ISubsystemService;
import com.aiurt.boot.modules.patrol.entity.PatrolPool;
import com.aiurt.boot.modules.patrol.param.StatisticsParam;
import com.aiurt.boot.modules.patrol.service.PatrolStatisticsService;
import com.aiurt.boot.modules.repairManage.mapper.RepairTaskMapper;
import com.aiurt.boot.modules.system.entity.SysDepart;
import com.aiurt.boot.modules.system.service.ISysDepartService;
import com.aiurt.boot.modules.webHome.service.WebHomeService;
import com.aiurt.boot.modules.webHome.utils.UserListenerUtils;
import com.aiurt.boot.modules.webHome.vo.PatrolHomeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: WebHomeController
 * @author: Mr.zhao
 * @date: 2021/10/28 16:15
 */
@Slf4j
@Api(tags = "web主页")
@RestController
@RequestMapping("/webHome")
@RequiredArgsConstructor
public class WebHomeController {

	@Resource
	private WebHomeService webHomeService;

	@Resource
	private PatrolStatisticsService patrolStatisticsService;

	@Resource
	private RepairTaskMapper repairTaskMapper;

	@Resource
	private UserListenerUtils userListenerUtils;

	@Resource
	private ISysDepartService sysDepartService;

	@Resource
	private ISubsystemService subsystemService;

	@Resource
	private IStationService stationService;

	@Resource
	private ISpecialSituationService specialSituationService;


	@AutoLog(value = "web主页-获取数量")
	@ApiOperation(value = "获取在线数量", notes = "获取在线数量")
	@GetMapping("/getCount")
	public Result<?> sessionCteate(HttpServletRequest request) {
		return Result.ok(userListenerUtils.getCount());
	}

	@AutoLog(value = "web主页-获取巡检数据")
	@ApiOperation(value = "web主页-获取巡检数据", notes = "web主页-获取巡检数据")
	@GetMapping("/getWebPatrol")
	public Result<PatrolHomeVO> getWebPatrol(StatisticsParam param) {
		PatrolHomeVO homeVO = new PatrolHomeVO();

		//查询检修数量
		final Integer repairTaskAmount = repairTaskMapper.getRepairTaskAmount(param.getStartTime(), param.getEndTime());
		homeVO.setRepaireAmount(repairTaskAmount);

		//默认时间
		LocalDate now = LocalDate.now();
		if (param.getStartTime()==null){
			param.setStartTime(now.atTime(0,0,0));
		}
		if (param.getEndTime()==null){
			param.setEndTime(now.atTime(23,59,59));
		}
		//判断是否开始时大于结束时间. 若是,则交换
		if (param.getStartTime().compareTo(param.getEndTime())>0){
			LocalDateTime startTime = param.getStartTime();
			param.setStartTime(param.getEndTime());
			param.setEndTime(startTime);
		}
		List<Subsystem> subsystemList = subsystemService.lambdaQuery().eq(Subsystem::getDelFlag, CommonConstant.DEL_FLAG_0).select(Subsystem::getSystemCode).list();
		List<SysDepart> departList = sysDepartService.lambdaQuery()
				.select(SysDepart::getId, SysDepart::getDepartName)
				.eq(SysDepart::getOrgType,2).list();

		List<Station> stationList = stationService.lambdaQuery().eq(Station::getDelFlag, CommonConstant.DEL_FLAG_0).select(Station::getId).list();
		if (CollectionUtils.isEmpty(subsystemList)||CollectionUtils.isEmpty(departList) ||CollectionUtils.isEmpty(stationList)){
			return Result.ok(homeVO);
		}

		Map<String, String> departMap = departList.stream().collect(Collectors.toMap(SysDepart::getId, SysDepart::getDepartName));
		List<String> codeList = subsystemList.stream().map(Subsystem::getSystemCode).collect(Collectors.toList());
		List<Integer> stationIds = stationList.stream().map(Station::getId).collect(Collectors.toList());


		param.setSystemCodes(codeList).setStationIds(stationIds).setDepartList(new ArrayList<>(departMap.keySet()));


		List<PatrolPool> poolList = webHomeService.getSize(param);
		if (CollectionUtils.isEmpty(poolList)) {
			return Result.ok(homeVO);
		}


		homeVO.setAllSize(poolList.size());

		homeVO.setSystem(webHomeService.getSystem(poolList));

		homeVO.setTeamList(patrolStatisticsService.teamList(param,departMap));


		return Result.ok(homeVO);
	}

	@AutoLog(value = "web主页-特情数量")
	@ApiOperation(value = "web主页-特情数量", notes = "web主页-特情数量")
	@GetMapping("/getWebSpecialSituation")
	public Result<?> getWebSpecialSituation(StatisticsParam param) {
		//默认时间
		LocalDate now = LocalDate.now();
		if (param.getStartTime()==null){
			param.setStartTime(now.atTime(0,0,0));
		}
		if (param.getEndTime()==null){
			param.setEndTime(now.atTime(23,59,59));
		}
		//判断是否开始时大于结束时间. 若是,则交换
		if (param.getStartTime().compareTo(param.getEndTime())>0){
			LocalDateTime startTime = param.getStartTime();
			param.setStartTime(param.getEndTime());
			param.setEndTime(startTime);
		}

		Integer count = specialSituationService.lambdaQuery().between(SpecialSituation::getEndTime, param.getStartTime(), param.getEndTime()).or().ge(SpecialSituation::getEndTime, param.getEndTime()).count();
		Map<String,Integer> map = new HashMap<>();
		map.put("specialSituationCount",count);
		return Result.ok(map);

	}



}
