package com.aiurt.modules.training.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.training.entity.TrainingPlan;
import com.aiurt.modules.training.entity.TrainingPlanFile;
import com.aiurt.modules.training.entity.TrainingPlanUser;
import com.aiurt.modules.training.service.ITrainingPlanFileService;
import com.aiurt.modules.training.service.ITrainingPlanService;
import com.aiurt.modules.training.service.ITrainingPlanUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: 培训计划
 * @Author: hlq
 * @Date: 2023-06-06
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "培训计划")
@RestController
@RequestMapping("/training/trainingPlan")
public class TrainingPlanController extends BaseController<TrainingPlan, ITrainingPlanService> {

    private static final String TRAINING_PLAN_SING_URL = "/training/trainingPlan/signIn/";

    @Autowired
    private ITrainingPlanService trainingPlanService;

    @Autowired
    private ITrainingPlanUserService trainingPlanUserService;


    @Autowired
    private ITrainingPlanFileService trainingPlanFileService;

    /**
     * 分页列表查询
     *
     * @param trainingPlan 查询对象
     * @param pageNo       第几页
     * @param pageSize     每页行数
     * @return 培训计划列表
     */
    @AutoLog(value = "培训计划-分页列表查询")
    @ApiOperation(value = "培训计划-分页列表查询", notes = "培训计划-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<TrainingPlan>> queryPageList(HttpServletRequest req,
                                                     TrainingPlan trainingPlan,
                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<TrainingPlan> queryWrapper = QueryGenerator.initQueryWrapper(trainingPlan, req.getParameterMap());
        queryWrapper.lambda().eq(TrainingPlan::getDelFlag, CommonConstant.DEL_FLAG_0).orderByDesc(TrainingPlan::getCreateTime);
        IPage<TrainingPlan> pageList = trainingPlanService.page(new Page<>(pageNo, pageSize), queryWrapper);
        pageList.getRecords().forEach(f -> f.setQrCode(TRAINING_PLAN_SING_URL.concat(f.getId().toString())));
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param trainingPlan 添加的对象
     * @return 状态
     */
    @AutoLog(value = "培训计划-添加")
    @ApiOperation(value = "培训计划-添加", notes = "培训计划-添加")
    @PostMapping(value = "/add")
    public Result<TrainingPlan> add(@RequestBody @Validated TrainingPlan trainingPlan) {
        return trainingPlanService.add(trainingPlan);
    }

    /**
     * 编辑
     *
     * @param trainingPlan 参数对象
     * @return 状态
     */
    @AutoLog(value = "培训计划-编辑")
    @ApiOperation(value = "培训计划-编辑", notes = "培训计划-编辑")
    @PutMapping(value = "/edit")
    public Result<TrainingPlan> edit(@RequestBody TrainingPlan trainingPlan) {

        return trainingPlanService.edit(trainingPlan);
    }


    /**
     * 通过id删除
     *
     * @param id 删除的id
     * @return 状态
     */
    @AutoLog(value = "培训计划-通过id删除")
    @ApiOperation(value = "培训计划-通过id删除", notes = "培训计划-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id") String id) {
        try {
            trainingPlanService.removeById(id);
            this.trainingPlanFileService.remove(new LambdaQueryWrapper<TrainingPlanFile>().eq(TrainingPlanFile::getPlanId, id));
            this.trainingPlanUserService.remove(new LambdaQueryWrapper<TrainingPlanUser>().eq(TrainingPlanUser::getPlanId, id));
        } catch (Exception e) {
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids 删除的ids
     * @return 状态
     */
    @AutoLog(value = "培训计划-批量删除")
    @ApiOperation(value = "培训计划-批量删除", notes = "培训计划-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<TrainingPlan> deleteBatch(@RequestParam(name = "ids") String ids) {
        Result<TrainingPlan> result = new Result<>();
        if (ObjectUtil.isEmpty(ids)) {
            result.error500("参数不识别！");
        } else {
            List<String> idList = Arrays.asList(ids.split(","));
            this.trainingPlanService.removeByIds(idList);
            this.trainingPlanFileService.remove(new LambdaQueryWrapper<TrainingPlanFile>().in(TrainingPlanFile::getPlanId, idList));
            this.trainingPlanUserService.remove(new LambdaQueryWrapper<TrainingPlanUser>().in(TrainingPlanUser::getPlanId, idList));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id 查询的id
     * @return 返回某个培训计划信息
     */
    @AutoLog(value = "培训计划-通过id查询")
    @ApiOperation(value = "培训计划-通过id查询", notes = "培训计划-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<TrainingPlan> queryById(@RequestParam(name = "id") String id) {
        Result<TrainingPlan> result = new Result<>();
        TrainingPlan trainingPlan = trainingPlanService.getById(id);
        if (ObjectUtil.isEmpty(trainingPlan)) {
            result.onnull("未找到对应实体");
        } else {
            result.setResult(trainingPlan);
            result.setSuccess(true);
        }
        return result;
    }


    /**
     * 通过id查询
     *
     * @param id id查询
     * @return 返回状态
     */
    @AutoLog(value = "培训计划-通过id查询")
    @ApiOperation(value = "培训计划-通过id查询", notes = "培训计划-通过id查询")
    @GetMapping(value = "/signIn/{id}")
    public Result<?> signIn(@PathVariable(name = "id") @NotNull(message = "id不能未空") Long id) {

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        TrainingPlanUser planUser = trainingPlanUserService.getOne(new LambdaQueryWrapper<TrainingPlanUser>()
                .eq(TrainingPlanUser::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(TrainingPlanUser::getPlanId, id)
                .eq(TrainingPlanUser::getUserId, loginUser.getId())
                .last("limit 1")
        );
        if (ObjectUtil.isEmpty(planUser)) {
            return Result.error("未找到对应课程");
        } else if (ObjectUtil.equal(CommonConstant.STATUS_ENABLE, planUser.getSignStatus())) {
            return Result.error("已签到,请勿重复签到");
        } else {
            planUser.setSignStatus(CommonConstant.STATUS_ENABLE).setSignTime(new Date());
            trainingPlanUserService.updateById(planUser);
            return Result.ok("签到成功");
        }
    }
}
