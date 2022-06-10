package com.aiurt.boot.modules.patrol.controller;

import com.swsc.copsms.modules.patrol.service.IPatrolTaskEnclosureService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: 巡检-附件表
 * @Author: qian
 * @Date:   2021-09-21
 * @Version: V1.0
 */
@Slf4j
@Api(tags="巡检-附件表")
@RestController
@RequestMapping("/patrol/patrolTaskEnclosure")
public class PatrolTaskEnclosureController {
	@Autowired
	private IPatrolTaskEnclosureService patrolTaskEnclosureService;


}
