package com.aiurt.boot.modules.webHome.service;

import com.aiurt.boot.modules.patrol.entity.PatrolPool;
import com.aiurt.boot.modules.patrol.param.StatisticsParam;
import com.aiurt.boot.modules.sysFile.entity.SimpNameVO;

import java.util.List;

/**
 * @description: WebHomeService
 * @author: Mr.zhao
 * @date: 2021/11/19 18:48
 */
public interface WebHomeService {


	List<PatrolPool> getSize(StatisticsParam param);

	List<SimpNameVO> getSystem(List<PatrolPool> poolList);

}
