package com.aiurt.boot.modules.patrol.controller;

import com.swsc.copsms.modules.patrol.service.INumberGenerateService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: 任务编号表
 * @Author: qian
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Slf4j
@Api(tags="任务编号表")
@RestController
@RequestMapping("/patrol/numberGenerate")
public class NumberGenerateController {
	@Autowired
	private INumberGenerateService numberGenerateService;

}
