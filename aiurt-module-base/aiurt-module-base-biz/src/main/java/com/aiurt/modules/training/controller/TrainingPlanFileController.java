package com.aiurt.modules.training.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.sysfile.param.SysFileWebParam;
import com.aiurt.modules.sysfile.vo.SysFileManageVO;
import com.aiurt.modules.training.entity.TrainingPlanFile;
import com.aiurt.modules.training.service.ITrainingPlanFileService;
import com.aiurt.modules.training.vo.TrainingPlanFileVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

/**
 * @Description: 培训文件
 * @Author: hlq
 * @Date: 2023-06-06
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "培训文件")
@RestController
@RequestMapping("/training/trainingPlanFile")
public class TrainingPlanFileController extends BaseController<TrainingPlanFile, ITrainingPlanFileService> {

    @Autowired
    private ITrainingPlanFileService trainingPlanFileService;


    /**
     * 分页列表查询
     *
     * @param planId   培训计划id
     * @param pageNo   页码
     * @param pageSize 页数
     * @return 培训文件列表
     */
    @AutoLog(value = "培训文件-分页列表查询")
    @ApiOperation(value = "培训文件-分页列表查询", notes = "培训文件-分页列表查询")
    @GetMapping(value = "/listByPlanId")
    public Result<IPage<TrainingPlanFileVO>> queryPageList(@RequestParam(name = "planId") @NotNull(message = "id不能为空") Long planId,
                                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<TrainingPlanFileVO> page = new Page<>(pageNo, pageSize);
        IPage<TrainingPlanFileVO> pageList = this.trainingPlanFileService.listByPlanId(page, planId);
        return Result.ok(pageList);
    }
    /**
     * 查询文档分页列表
     *
     * @param sysFile
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "查询文档分页列表")
    @ApiOperation(value = "查询文档分页列表", notes = "查询文档分页列表")
    @GetMapping(value = "/list")
    public Result<IPage<SysFileManageVO>> getFilePageList(SysFileWebParam sysFile,
                                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                          HttpServletRequest request) {
        Page<SysFileManageVO> page = new Page<>(pageNo, pageSize);
        page = trainingPlanFileService.getFilePageList(page, sysFile);
        return Result.OK(page);
    }
}
