package com.aiurt.boot.modules.patrol.controller;

import com.swsc.copsms.modules.patrol.service.IPatrolPoolContentService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: 巡检人员任务项
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags="巡检人员任务项")
@RestController
@RequestMapping("/patrol/patrolPoolContent")
public class PatrolPoolContentController {

	@Autowired
	private IPatrolPoolContentService patrolPoolContentService;



}
