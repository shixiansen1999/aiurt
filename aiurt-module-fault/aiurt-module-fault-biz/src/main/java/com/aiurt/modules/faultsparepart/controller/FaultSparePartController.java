package com.aiurt.modules.faultsparepart.controller;

import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.faultsparepart.entity.FaultSparePart;
import com.aiurt.modules.faultsparepart.service.IFaultSparePartService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 故障知识库备件信息
 * @Author: aiurt
 * @Date:
 * @Version: V1.0
 */
@Api(tags = "故障知识库备件信息")
@RestController
@RequestMapping("/faultsparepart")
@Slf4j
public class FaultSparePartController extends BaseController<FaultSparePart, IFaultSparePartService> {

}
