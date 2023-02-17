package com.aiurt.boot.strategy.controller;

import com.aiurt.boot.strategy.entity.InspectionCoOrgRel;
import com.aiurt.boot.strategy.service.IInspectionCoOrgRelService;
import com.aiurt.common.system.base.controller.BaseController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* @Description: inspection_str_org_rel
* @Author: aiurt
* @Date:   2022-06-22
* @Version: V1.0
*/
@Api(tags="inspection_str_org_rel")
@RestController
@RequestMapping("/strategy/inspectionCoOrgRel")
@Slf4j
public class InspectionCoOrgRelController extends BaseController<InspectionCoOrgRel, IInspectionCoOrgRelService> {
   @Autowired
   private IInspectionCoOrgRelService iInspectionCoOrgRelService;


}
