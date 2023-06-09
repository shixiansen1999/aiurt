package com.aiurt.modules.training.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.training.entity.TrainingPlanUser;
import com.aiurt.modules.training.param.PlanUserParam;
import com.aiurt.modules.training.service.ITrainingPlanUserService;
import com.aiurt.modules.training.vo.PlanUserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

/**
 * @Description: 培训人员
 * @Author: hlq
 * @Date: 2023-06-06
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "培训人员")
@RestController
@RequestMapping("/training/trainingPlanUser")
public class TrainingPlanUserController extends BaseController<TrainingPlanUser, ITrainingPlanUserService> {

    @Autowired
    private ITrainingPlanUserService trainingPlanUserService;


    /**
     * 培训计划-分页列表查询
     *
     * @param param    查询参数
     * @param pageNo   页码
     * @param pageSize 页数
     * @return 培训计划列表
     */
    @AutoLog(value = "培训计划-分页查询个人列表")
    @ApiOperation(value = "培训计划-分页查询个人列表", notes = "培训计划-分页查询个人列表")
    @GetMapping(value = "/listPlan")
    public Result<IPage<PlanUserVO>> queryPageList(PlanUserParam param,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<PlanUserVO> page = new Page<>(pageNo, pageSize);
        IPage<PlanUserVO> pageList = this.trainingPlanUserService.listPlan(page, param);
        return Result.ok(pageList);
    }


    /**
     * 分页列表查询
     *
     * @param trainingPlanUser 查询参数
     * @param pageNo           页码
     * @param pageSize         页数
     * @return 培训人员列表
     */
    @AutoLog(value = "培训人员-纯分页列表查询")
    @ApiOperation(value = "培训人员-纯分页列表查询", notes = "培训人员-纯分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<TrainingPlanUser>> queryPageList(HttpServletRequest req,
                                                         TrainingPlanUser trainingPlanUser,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        QueryWrapper<TrainingPlanUser> queryWrapper = QueryGenerator.initQueryWrapper(trainingPlanUser, req.getParameterMap());
        queryWrapper.lambda().eq(TrainingPlanUser::getDelFlag, CommonConstant.DEL_FLAG_0);
        IPage<TrainingPlanUser> pageList = trainingPlanUserService.page(new Page<>(pageNo, pageSize), queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 分页列表查询
     *
     * @param planId   培训计划id
     * @param pageNo   页码
     * @param pageSize 页数
     * @return 培训人员列表
     */
    @AutoLog(value = "培训人员-id列表查询")
    @ApiOperation(value = "培训人员-id列表查询", notes = "培训人员-id列表查询")
    @GetMapping(value = "/listByPlanId")
    public Result<IPage<TrainingPlanUser>> queryPageList(@RequestParam(name = "planId") @NotNull(message = "id不能为空") Long planId,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<TrainingPlanUser> page = new Page<>(pageNo, pageSize);
        IPage<TrainingPlanUser> pageList = this.trainingPlanUserService.listByPlanId(page, planId);
        return Result.ok(pageList);
    }


    /**
     * 编辑
     *
     * @param trainingPlan 参数
     * @return 状态
     */
    @AutoLog(value = "培训人员-编辑")
    @ApiOperation(value = "培训人员-编辑", notes = "培训人员-编辑")
    @PutMapping(value = "/edit")
    public Result<TrainingPlanUser> edit(@RequestBody TrainingPlanUser trainingPlan) {
        Result<TrainingPlanUser> result = new Result<>();
        TrainingPlanUser trainingPlanEntity = trainingPlanUserService.getById(trainingPlan.getId());
        if (ObjectUtil.isEmpty(trainingPlanEntity)) {
            result.onnull("未找到对应实体");
        } else {
            boolean ok = trainingPlanUserService.updateById(trainingPlan);
            if (ok) {
                result.success("修改成功!");
            }
        }
        return result;
    }

    @AutoLog(value = "个人管理-导出excel")
    @ApiOperation(value = "导出excel", notes = "导出excel")
    @RequestMapping(value = "/exportTrainingPlanXls")
    public ModelAndView exportTrainingPlanXls(PlanUserParam param) {
        return trainingPlanUserService.exportListPlan(param);
    }

}
