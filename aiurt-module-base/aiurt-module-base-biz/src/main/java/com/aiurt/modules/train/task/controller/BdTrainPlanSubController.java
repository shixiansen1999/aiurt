package com.aiurt.modules.train.task.controller;

import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.train.task.entity.BdTrainPlanSub;
import com.aiurt.modules.train.task.mapper.BdTrainPlanSubMapper;
import com.aiurt.modules.train.task.service.IBdTrainPlanService;
import com.aiurt.modules.train.task.service.IBdTrainPlanSubService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import com.aiurt.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 年子计划
 * @Author: jeecg-boot
 * @Date: 2022-04-20
 * @Version: V1.0
 */
@Api(tags = "年子计划")
@RestController
@RequestMapping("/task/bdTrainPlanSub")
@Slf4j
public class BdTrainPlanSubController extends BaseController<BdTrainPlanSub, IBdTrainPlanSubService> {
    @Autowired
    private IBdTrainPlanSubService bdTrainPlanSubService;

    @Autowired
    private BdTrainPlanSubMapper bdTrainPlanSubMapper;

    @Autowired
    private IBdTrainPlanService bdTrainPlanService;

    /**
     * 分页列表查询
     *
     * @param bdTrainPlanSub
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "年子计划-分页列表查询")
    @ApiOperation(value = "年子计划-分页列表查询", notes = "年子计划-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(BdTrainPlanSub bdTrainPlanSub,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<BdTrainPlanSub> queryWrapper = QueryGenerator.initQueryWrapper(bdTrainPlanSub, req.getParameterMap());
        Page<BdTrainPlanSub> page = new Page<BdTrainPlanSub>(pageNo, pageSize);
        IPage<BdTrainPlanSub> pageList = bdTrainPlanSubService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param bdTrainPlanSub
     * @return
     */
    @AutoLog(value = "年子计划-添加")
    @ApiOperation(value = "年子计划-添加", notes = "年子计划-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody BdTrainPlanSub bdTrainPlanSub) {
        bdTrainPlanSubService.save(bdTrainPlanSub);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param bdTrainPlanSub
     * @return
     */
    @AutoLog(value = "年子计划-编辑")
    @ApiOperation(value = "年子计划-编辑", notes = "年子计划-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody BdTrainPlanSub bdTrainPlanSub) {
        bdTrainPlanSubService.updateById(bdTrainPlanSub);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "年子计划-通过id删除")
    @ApiOperation(value = "年子计划-通过id删除", notes = "年子计划-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        bdTrainPlanSubService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "年子计划-批量删除")
    @ApiOperation(value = "年子计划-批量删除", notes = "年子计划-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.bdTrainPlanSubService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "年子计划-通过id查询")
    @ApiOperation(value = "年子计划-通过id查询", notes = "年子计划-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        BdTrainPlanSub bdTrainPlanSub = bdTrainPlanSubService.getById(id);
        if (bdTrainPlanSub == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(bdTrainPlanSub);
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
        return super.importExcel(request, response, BdTrainPlanSub.class);
    }

    /**
     * 培训任务中的年子计划查询
     *
     * @param bdTrainPlanSub
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "培训任务中的年子计划查询-分页列表查询")
    @ApiOperation(value = "培训任务中的年子计划查询", notes = "培训任务中的年子计划查询")
    @GetMapping(value = "/getPageList")
    public Result<?> getPageList(BdTrainPlanSub bdTrainPlanSub,
                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<BdTrainPlanSub> pageList = new Page<>(pageNo, pageSize);
        Page<BdTrainPlanSub> bdTrainPlanSubPage = bdTrainPlanSubService.filterPlanSub(pageList, bdTrainPlanSub);
        return Result.OK(bdTrainPlanSubPage);
    }
}
