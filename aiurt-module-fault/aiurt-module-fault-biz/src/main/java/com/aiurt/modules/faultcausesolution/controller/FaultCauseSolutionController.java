package com.aiurt.modules.faultcausesolution.controller;

import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.faultcausesolution.entity.FaultCauseSolution;
import com.aiurt.modules.faultcausesolution.service.IFaultCauseSolutionService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: 故障原因及解决方案
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
@Api(tags="故障原因及解决方案")
@RestController
@RequestMapping("/faultcausesolution")
@Slf4j
public class FaultCauseSolutionController extends BaseController<FaultCauseSolution, IFaultCauseSolutionService> {

}
