package com.aiurt.boot.report.controller;

import com.aiurt.boot.api.PatrolApi;
import com.aiurt.boot.dto.UserTeamParameter;
import com.aiurt.boot.dto.UserTeamPatrolDTO;
import com.aiurt.common.aspect.annotation.AutoLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/10/9
 * @desc
 */
@Api(tags = "统计报表")
@RestController
@RequestMapping("/reportParameter")
@Slf4j
public class TestPatrolController {
    @Resource
    private PatrolApi patrolApi;
    @AutoLog(value = "统计报表-人员班组查询", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "统计报表-人员班组查询", notes = "统计报表-人员班组查询")
    @RequestMapping(value = "/getUserHours", method = {RequestMethod.GET, RequestMethod.POST})
   public Map<String, UserTeamPatrolDTO> getUserHours(UserTeamParameter userTeamParameter){
         Map<String, UserTeamPatrolDTO> hours = patrolApi.getUserParameter(userTeamParameter);
         return  hours;
    }
}
