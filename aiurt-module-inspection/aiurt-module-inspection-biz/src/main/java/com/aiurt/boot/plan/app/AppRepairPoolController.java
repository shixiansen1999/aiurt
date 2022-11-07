package com.aiurt.boot.plan.app;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.manager.dto.OrgDTO;
import com.aiurt.boot.plan.service.IRepairPoolService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.enums.ModuleType;
import com.aiurt.common.exception.AiurtBootException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/7/1518:05
 */
@Api(tags = "app检修计划")
@RestController
@RequestMapping("/app/plan")
@Slf4j
public class AppRepairPoolController {
    @Resource
    private IRepairPoolService repairPoolService;

    /**
     * 指派时的下拉人员
     *
     * @param id 检修计划id
     */
    @AutoLog(value = "检修管理-检修计划-查询指派时可选的人员", operateType =  1, operateTypeAlias = "查询指派时可选的人员", module = ModuleType.INSPECTION)
    @ApiOperation(value = "指派时的下拉人员", notes = "指派时的下拉人员")
    @GetMapping(value = "/queryUserDownList")
    public Result<List<OrgDTO>> queryUserDownList(@RequestParam @ApiParam(value = "检修计划id", name = "id", required = true) String id) {
        List<OrgDTO> result = repairPoolService.queryUserDownList(id);
        result = Optional.ofNullable(result).orElseGet(Collections::emptyList).stream()
                .filter(l -> ObjectUtil.isNotEmpty(l.getUsers())).collect(Collectors.toList());
        if(CollectionUtil.isEmpty(result)){
            throw new AiurtBootException("您没有指派当前任务人员的权限或当前暂无排班人员!");
        }
        return Result.OK(result);
    }

}
