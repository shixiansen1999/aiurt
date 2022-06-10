package com.aiurt.boot.modules.patrol.service;

import com.aiurt.boot.modules.patrol.param.StatisticsParam;
import com.aiurt.boot.modules.patrol.vo.statistics.SimpStatisticsVO;
import com.aiurt.boot.modules.patrol.vo.statistics.StatisticsListVO;
import com.aiurt.boot.modules.patrol.vo.statistics.StatisticsTitleVO;

import java.util.List;
import java.util.Map;

/**
 * @description: PatrolStatisticsService
 * @author: Mr.zhao
 * @date: 2021/11/19 12:17
 */

public interface PatrolStatisticsService {


	/**
	 * 标题
	 */
	StatisticsTitleVO title(StatisticsParam param );

	/**
	 * 查询班组对比
	 */
	List<SimpStatisticsVO> teamList(StatisticsParam param,Map<String, String> departMap);

	/**
	 * 查询系统对比
	 */
	List<SimpStatisticsVO> systemList(StatisticsParam param,Map<String, String> systemMap);

	/**
	 * 查询下方列表
	 */
	List<StatisticsListVO> list(StatisticsParam param,Map<String, String> departMap);
}
