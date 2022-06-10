package com.aiurt.boot.modules.patrol.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.aiurt.boot.modules.patrol.constant.PatrolConstant;
import com.aiurt.boot.modules.patrol.mapper.PatrolPoolMapper;
import com.aiurt.boot.modules.patrol.param.StatisticsParam;
import com.aiurt.boot.modules.patrol.service.PatrolStatisticsService;
import com.aiurt.boot.modules.patrol.vo.statistics.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: PatrolStatisticsServiceImpl
 * @author: Mr.zhao
 * @date: 2021/11/19 12:17
 */
@Service
@RequiredArgsConstructor
public class PatrolStatisticsServiceImpl implements PatrolStatisticsService {

	private final PatrolPoolMapper patrolPoolMapper;

	@Override
	public StatisticsTitleVO title(StatisticsParam param) {
		StatisticsTitleVO vo = new StatisticsTitleVO();

		List<SimpIntegerSqlVO> voList = patrolPoolMapper.selectTitle(param);

		if (CollectionUtils.isNotEmpty(voList)) {
			for (SimpIntegerSqlVO sqlVO : voList) {
				Integer key = sqlVO.getKey();
				Integer name = sqlVO.getName();
				Integer count = sqlVO.getCount();
				if (count != null && count > 0 && key != null) {
					//日
					if (ObjectUtil.equal(PatrolConstant.PATROL_TACTICS_1, key) || ObjectUtil.equal(PatrolConstant.PATROL_TACTICS_4, key)) {
						vo.setDayPatrolSize(vo.getDayPatrolSize() + count);
						if (PatrolConstant.ENABLE.equals(name)) {
							vo.setInspectedSize(vo.getInspectedSize() + count);
							vo.setDayInspectedSize(vo.getDayInspectedSize() + count);
						} else {
							vo.setNotInspectedSize(vo.getNotInspectedSize() + count);
							vo.setDayNotInspectedSize(vo.getDayNotInspectedSize() + count);
						}
					}
					//周
					if (ObjectUtil.equal(PatrolConstant.PATROL_TACTICS_2, key) || ObjectUtil.equal(PatrolConstant.PATROL_TACTICS_3, key)) {
						vo.setWeekPatrolSize(vo.getWeekPatrolSize() + count);
						if (PatrolConstant.ENABLE.equals(name)) {
							vo.setInspectedSize(vo.getInspectedSize() + count);
							vo.setWeekInspectedSize(vo.getWeekInspectedSize() + count);
						} else {
							vo.setNotInspectedSize(vo.getNotInspectedSize() + count);
							vo.setWeekNotInspectedSize(vo.getWeekNotInspectedSize() + count);
						}
					}
					vo.setPatrolSize(vo.getPatrolSize() + count);
				}
			}
		}

		return vo;
	}


	@Override
	public List<SimpStatisticsVO> teamList(StatisticsParam param, Map<String, String> departMap) {

		if (departMap == null) {
			return new ArrayList<>();
		}

		//全部班组的map计数对象 key:班组id
		Map<String, SimpStatisticsVO> map = new HashMap<>();

		List<SimpStringSqlVO> voList = patrolPoolMapper.selectTeamCount(param);

		if (CollectionUtils.isNotEmpty(voList)) {
			for (SimpStringSqlVO sqlVO : voList) {
				String key = sqlVO.getKey();
				Integer name = sqlVO.getName();
				Integer count = sqlVO.getCount();
				if (count != null && count > 0 && StringUtils.isNotBlank(key)) {

					SimpStatisticsVO vo = map.get(key);
					if (vo == null) {
						vo = new SimpStatisticsVO();
						String departName = departMap.get(key);
						if (StringUtils.isBlank(departName)) {
							continue;
						}
						vo.setName(departName);
					}
					if (PatrolConstant.ENABLE.equals(name)) {
						vo.setCompleted(vo.getCompleted() + count);
					} else {
						vo.setUndone(vo.getUndone() + count);
					}
					map.put(key, vo);
				}
			}
		}


		for (String departId : departMap.keySet()) {
			if (!map.containsKey(departId)) {
				if (departMap.get(departId) != null) {
					map.put(departId, new SimpStatisticsVO().setName(departMap.get(departId)));
				}
			}
		}

		return new ArrayList<>(map.values());
	}

	@Override
	public List<SimpStatisticsVO> systemList(StatisticsParam param, Map<String, String> systemMap) {

		//全部班组的map计数对象 key:系统id
		Map<String, SimpStatisticsVO> map = new HashMap<>();

		List<SimpStringSqlVO> voList = patrolPoolMapper.selectSystemCount(param);


		if (CollectionUtils.isNotEmpty(voList)) {
			for (SimpStringSqlVO sqlVO : voList) {
				String key = sqlVO.getKey();
				Integer name = sqlVO.getName();
				Integer count = sqlVO.getCount();
				if (StringUtils.isNotBlank(key) && count != null && count > 0) {
					SimpStatisticsVO vo = map.get(key);
					if (vo == null) {
						String systemName = systemMap.get(key);
						if (StringUtils.isBlank(systemName)) {
							continue;
						}
						vo = new SimpStatisticsVO();
						vo.setName(systemName);
					}
					if (PatrolConstant.ENABLE.equals(name)) {
						vo.setCompleted(vo.getCompleted() + count);
					} else {
						vo.setUndone(vo.getUndone() + count);
					}
					map.put(key, vo);
				}
			}
		}

		//添加其余的系统名称
		for (String code : systemMap.keySet()) {
			if (!map.containsKey(code) && systemMap.get(code) != null) {
				map.put(code, new SimpStatisticsVO().setName(systemMap.get(code)));
			}
		}

		return new ArrayList<>(map.values());
	}


	@Override
	public List<StatisticsListVO> list(StatisticsParam param, Map<String, String> departMap) {
		//全部班组的map计数对象 key:班组id
		Map<String, StatisticsListVO> map = new HashMap<>();

		//合计
		StatisticsListVO all = new StatisticsListVO();
		all.setName("合计");

		List<SimpStringSqlVO> voList = patrolPoolMapper.selectTeamCount(param);

		if (CollectionUtils.isNotEmpty(voList)) {

			List<SimpStringSqlVO> warnList = patrolPoolMapper.selectWarn(param);
			List<SimpStringSqlVO> errorList = patrolPoolMapper.selectError(param);

			Map<String, Integer> warnMap = null;
			Map<String, Integer> errorMap = null;

			if (CollectionUtils.isNotEmpty(warnList)) {
				warnMap = warnList.stream().collect(Collectors.toMap(SimpStringSqlVO::getKey, SimpStringSqlVO::getCount));
			}
			if (CollectionUtils.isNotEmpty(errorList)) {
				errorMap = errorList.stream().collect(Collectors.toMap(SimpStringSqlVO::getKey, SimpStringSqlVO::getCount));
			}

			for (SimpStringSqlVO sqlVO : voList) {
				String key = sqlVO.getKey();
				Integer name = sqlVO.getName();
				Integer count = sqlVO.getCount();
				if (count != null && count > 0 && StringUtils.isNotBlank(key)) {

					StatisticsListVO vo = map.get(key);
					if (vo == null) {
						vo = new StatisticsListVO();
						String departName = departMap.get(key);
						if (StringUtils.isBlank(departName)) {
							continue;
						}
						vo.setName(departName);
					}
					if (PatrolConstant.ENABLE.equals(name)) {
						vo.setSuccessFlag(vo.getSuccessFlag() + count);
						all.setSuccessFlag(all.getSuccessFlag() + count);
					} else {
						vo.setUnFlag(vo.getUnFlag() + count);
						all.setUnFlag(all.getUnFlag() + count);
					}
					vo.setAllSize(vo.getAllSize() + count);
					all.setAllSize(all.getAllSize() + count);

					if (name!=null && name.equals(1)) {
						if (warnMap != null && warnMap.get(key) != null) {
							vo.setWarmFlag(vo.getWarmFlag() + warnMap.get(key));
							all.setWarmFlag(all.getWarmFlag() + warnMap.get(key));
						}
						if (errorMap != null && errorMap.get(key) != null) {
							vo.setErrorFlag(vo.getErrorFlag() + errorMap.get(key));
							all.setErrorFlag(all.getErrorFlag() + errorMap.get(key));
						}
					}
					map.put(key, vo);
				}
			}
		}

		ArrayList<StatisticsListVO> listVO = new ArrayList<>(map.values());
		listVO.add(all);
		return listVO;
	}


}
