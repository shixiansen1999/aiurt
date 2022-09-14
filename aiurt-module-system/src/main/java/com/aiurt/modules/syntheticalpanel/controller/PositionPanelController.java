package com.aiurt.modules.syntheticalpanel.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.modules.syntheticalpanel.model.PositionPanel;
import com.aiurt.modules.syntheticalpanel.service.PositionPanelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 综合看板线路信息
 * @Author: lkj
 * @Date:   2022-09-13
 */
@Api(tags="综合看板线路信息")
@RestController
@RequestMapping("/syntheticalpanel/positionPanel")
@Slf4j
public class PositionPanelController {

    @Autowired
    private PositionPanelService positionPanelService;


    /**
     * 综合大屏线路工区查询
     *
     * @param positionPanel
     * @return
     */
    @AutoLog(value = "综合大屏线路工区-查询", operateType =  1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value="综合大屏线路工区查询", notes="综合大屏线路工区查询")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "")
    public Result<List<PositionPanel>> queryPageList(PositionPanel positionPanel) {
        List<PositionPanel> positionPanels = positionPanelService.readAll(positionPanel);
        return Result.OK(positionPanels);
    }

    /**
     * 通过id查询
     *
     * @param positionPanel
     * @return
     */
    @AutoLog(value = "综合看板线路站点信息-通过id查询", operateType =  1, operateTypeAlias = "查询-通过id查询", permissionUrl = "")
    @ApiOperation(value="综合看板线路站点信息-通过id查询", notes="综合看板线路站点信息-通过id查询")
    @PostMapping(value = "/queryById")
    public Result<List<PositionPanel>> queryById(@RequestBody PositionPanel positionPanel) {
        List<PositionPanel> positionPanels = positionPanelService.queryById(positionPanel);
        return Result.OK(positionPanels);
    }

    /**
     *  编辑
     *
     * @param positionPanel
     * @return
     */
    @AutoLog(value = "综合看板线路站点信息-编辑", operateType =  3, operateTypeAlias = "编辑", permissionUrl = "/fault/faultKnowledgeBaseList")
    @ApiOperation(value="综合看板线路站点信息-编辑", notes="综合看板线路站点信息-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
    public Result<String> edit(@RequestBody PositionPanel positionPanel) {
        positionPanelService.edit(positionPanel);
        return Result.OK("编辑成功!");
    }
}
