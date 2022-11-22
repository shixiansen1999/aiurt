package com.aiurt.boot.weeklyplan.controller;

import com.aiurt.boot.weeklyplan.entity.ConstructionCommandAssist;
import com.aiurt.boot.weeklyplan.service.IConstructionCommandAssistService;
import com.aiurt.common.system.base.controller.BaseController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: construction_command_assist
 * @Author: aiurt
 * @Date:   2022-11-22
 * @Version: V1.0
 */
@Api(tags="construction_command_assist")
@RestController
@RequestMapping("/weeklyplan/constructionCommandAssist")
@Slf4j
public class ConstructionCommandAssistController extends BaseController<ConstructionCommandAssist, IConstructionCommandAssistService> {
	@Autowired
	private IConstructionCommandAssistService constructionCommandAssistService;

}
