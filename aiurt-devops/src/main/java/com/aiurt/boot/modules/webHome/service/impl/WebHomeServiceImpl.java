package com.aiurt.boot.modules.webHome.service.impl;

import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.aiurt.boot.modules.manage.service.ISubsystemService;
import com.aiurt.boot.modules.patrol.entity.PatrolPool;
import com.aiurt.boot.modules.patrol.param.StatisticsParam;
import com.aiurt.boot.modules.patrol.service.IPatrolPoolService;
import com.aiurt.boot.modules.sysFile.entity.SimpNameVO;
import com.aiurt.boot.modules.webHome.service.WebHomeService;
import com.aiurt.common.constant.CommonConstant;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: WebHomeServiceImpl
 * @author: Mr.zhao
 * @date: 2021/11/19 18:48
 */
@Service
@RequiredArgsConstructor
public class WebHomeServiceImpl implements WebHomeService {

	private final IPatrolPoolService patrolPoolService;

	private final ISubsystemService subsystemService;

//	private final ISysDepartService sysDepartService;

	@Override
	public List<PatrolPool> getSize(StatisticsParam param) {

		List<PatrolPool> patrolPools =patrolPoolService.lambdaQuery().eq(PatrolPool::getDelFlag, CommonConstant.DEL_FLAG_0)
				.select(PatrolPool::getStatus,PatrolPool::getId,PatrolPool::getSystemType,PatrolPool::getSystemTypeName,PatrolPool::getType,PatrolPool::getTactics,PatrolPool::getOrganizationId)
				.and(query ->
						query.apply("{0} between create_time and execution_time ", param.getStartTime())
								.or().apply("{0} between create_time and execution_time ", param.getEndTime())
								.or(queryOr -> queryOr.ge(PatrolPool::getCreateTime, param.getStartTime())
										.le(PatrolPool::getExecutionTime, param.getEndTime()))
				)
				.in(PatrolPool::getLineId,param.getStationIds())
				.in(PatrolPool::getSystemType,param.getSystemCodes())
				.in(PatrolPool::getOrganizationId,param.getDepartList())
				.list();

		return patrolPools;

	}

	@Override
	public List<SimpNameVO> getSystem(List<PatrolPool> poolList) {

		List<SimpNameVO> voList = new ArrayList<>();

		Map<String, Integer>  map= new HashMap<>();

		Map<String, List<PatrolPool>> collect = poolList.stream().collect(Collectors.groupingBy(PatrolPool::getSystemType));

		Set<String> keySet = collect.keySet();

		//添加其余的系统名称
		List<Subsystem> list = subsystemService.list(new LambdaQueryWrapper<Subsystem>()
				.eq(Subsystem::getDelFlag, CommonConstant.DEL_FLAG_0)
				.select(Subsystem::getSystemCode,Subsystem::getSystemName)
		);
		if (CollectionUtils.isEmpty(list)){
			return voList;
		}
		Map<String, String> systemMap = list.stream().collect(Collectors.toMap(Subsystem::getSystemCode, Subsystem::getSystemName));

		//系统分组算占比
		for (String s : keySet) {
			List<PatrolPool> patrolPools = collect.get(s);
			if (CollectionUtils.isNotEmpty(patrolPools)){
				String typeName = patrolPools.get(0).getSystemTypeName();
				if (StringUtils.isBlank(typeName)){
					typeName = systemMap.get(s);
				}
				if (StringUtils.isBlank(typeName)){
					continue;
				}
				map.put(typeName,patrolPools.size());
			}
		}

		for (Subsystem subsystem : list) {
			//加入其余的系统名称
			if (!map.containsKey(subsystem.getSystemName())) {
				map.put(subsystem.getSystemName(),0);
			}
		}
		if (CollectionUtils.isNotEmpty(map.keySet())) {
			for (String s : map.keySet()) {
				SimpNameVO vo = new SimpNameVO();
				vo.setName(s).setNum(map.get(s));
				voList.add(vo);
			}
		}

		return voList;
	}

}
