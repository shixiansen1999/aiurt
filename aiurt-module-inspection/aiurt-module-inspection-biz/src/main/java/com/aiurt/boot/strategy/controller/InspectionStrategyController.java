package com.aiurt.boot.strategy.controller;

import com.aiurt.boot.strategy.entity.InspectionStrategy;
import com.aiurt.boot.strategy.service.IInspectionStrategyService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: inspection_strategy
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
@Api(tags = "inspection_strategy")
@RestController
@RequestMapping("/strategy/inspectionStrategy")
@Slf4j
public class InspectionStrategyController extends BaseController<InspectionStrategy, IInspectionStrategyService> {
    @Autowired
    private IInspectionStrategyService inspectionStrategyService;

    /**
     * 分页列表查询
     *
     * @param inspectionStrategy
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "inspection_strategy-分页列表查询")
    @ApiOperation(value = "inspection_strategy-分页列表查询", notes = "inspection_strategy-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<InspectionStrategy>> queryPageList(InspectionStrategy inspectionStrategy,
                                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                           HttpServletRequest req) {
        QueryWrapper<InspectionStrategy> queryWrapper = QueryGenerator.initQueryWrapper(inspectionStrategy, req.getParameterMap());
        Page<InspectionStrategy> page = new Page<InspectionStrategy>(pageNo, pageSize);
        IPage<InspectionStrategy> pageList = inspectionStrategyService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param inspectionStrategy
     * @return
     */
    @AutoLog(value = "inspection_strategy-添加")
    @ApiOperation(value = "inspection_strategy-添加", notes = "inspection_strategy-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody InspectionStrategy inspectionStrategy) {
        inspectionStrategyService.save(inspectionStrategy);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param inspectionStrategy
     * @return
     */
    @AutoLog(value = "inspection_strategy-编辑")
    @ApiOperation(value = "inspection_strategy-编辑", notes = "inspection_strategy-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody InspectionStrategy inspectionStrategy) {
        inspectionStrategyService.updateById(inspectionStrategy);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "inspection_strategy-通过id删除")
    @ApiOperation(value = "inspection_strategy-通过id删除", notes = "inspection_strategy-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        inspectionStrategyService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "inspection_strategy-批量删除")
    @ApiOperation(value = "inspection_strategy-批量删除", notes = "inspection_strategy-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.inspectionStrategyService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "inspection_strategy-通过id查询")
    @ApiOperation(value = "inspection_strategy-通过id查询", notes = "inspection_strategy-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<InspectionStrategy> queryById(@RequestParam(name = "id", required = true) String id) {
        InspectionStrategy inspectionStrategy = inspectionStrategyService.getById(id);
        if (inspectionStrategy == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(inspectionStrategy);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param inspectionStrategy
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, InspectionStrategy inspectionStrategy) {
        return super.exportXls(request, inspectionStrategy, InspectionStrategy.class, "inspection_strategy");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, InspectionStrategy.class);
    }


    /**
     * 生成年检计划
     *
     * @param id
     * @return
     */
    @AutoLog(value = "生成年检计划")
    @ApiOperation(value = "生成年检计划-通过id", notes = "生成年检计划-通过id")
    @PostMapping("/addAnnualPlan")
    public Result addAnnualPlan(@RequestParam @ApiParam(name = "id", required = true, value = "检修策略id") String id) {
        return inspectionStrategyService.addAnnualPlan(id);
    }

    /**
     * 重新生成年检计划
     *
     * @param id
     * @return
     */
    @AutoLog(value = "重新生成年检计划")
    @ApiOperation(value = "重新生成年检计划-通过id", notes = "重新生成年检计划-通过id")
    @PostMapping("/addAnnualNewPlan")
    public Result addAnnualNewPlan(@RequestParam @ApiParam(name = "id", required = true, value = "检修策略id") String id) {
        return inspectionStrategyService.addAnnualNewPlan(id);
    }

}
