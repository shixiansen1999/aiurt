package com.aiurt.modules.faultcauseusagerecords.controller;

import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.faultcauseusagerecords.entity.FaultCauseUsageRecords;
import com.aiurt.modules.faultcauseusagerecords.service.IFaultCauseUsageRecordsService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 故障原因使用记录表
 * @Author: aiurt
 * @Version: V1.0
 */
@Api(tags = "故障原因使用记录表")
@RestController
@RequestMapping("/故障原因使用记录表")
@Slf4j
public class FaultCauseUsageRecordsController extends BaseController<FaultCauseUsageRecords, IFaultCauseUsageRecordsService> {

}
