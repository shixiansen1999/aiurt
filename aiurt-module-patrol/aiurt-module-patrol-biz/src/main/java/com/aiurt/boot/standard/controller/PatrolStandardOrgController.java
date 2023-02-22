package com.aiurt.boot.standard.controller;

import com.aiurt.boot.standard.entity.PatrolStandardOrg;
import com.aiurt.boot.standard.service.IPatrolStandardOrgService;
import com.aiurt.common.system.base.controller.BaseController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * patrol_standard_org
 */
@Api(tags="patrol_standard_org")
@RestController
@RequestMapping("/patrolStandardOrg")
@Slf4j
public class PatrolStandardOrgController extends BaseController<PatrolStandardOrg, IPatrolStandardOrgService> {
   @Autowired
   private IPatrolStandardOrgService iPatrolStandardOrgService;


}
