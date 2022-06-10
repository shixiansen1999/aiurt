package com.aiurt.boot.modules.training.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.util.oConvertUtils;
import com.swsc.copsms.modules.system.entity.SysUser;
import com.swsc.copsms.modules.system.service.ISysUserService;
import com.swsc.copsms.modules.training.entity.CoursewareStock;
import com.swsc.copsms.modules.training.entity.TrainingPlan;
import com.swsc.copsms.modules.training.entity.TrainingPlanObj;
import com.swsc.copsms.modules.training.service.ICoursewareStockService;
import com.swsc.copsms.modules.training.service.ITrainingPlanObjService;
import com.swsc.copsms.modules.training.service.ITrainingPlanService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: 培训计划
 * @Author: swsc
 * @Date: 2021-09-17
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "培训计划")
@RestController
@RequestMapping("/training/trainingPlan")
public class TrainingPlanController {
    @Autowired
    private ITrainingPlanService trainingPlanService;

    @Autowired
    private ICoursewareStockService coursewareStockService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ITrainingPlanObjService trainingPlanObjService;

    /**
     * 分页列表查询
     *
     * @param trainingPlan
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "培训计划-分页列表查询")
    @ApiOperation(value = "培训计划-分页列表查询", notes = "培训计划-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<TrainingPlan>> queryPageList(TrainingPlan trainingPlan,
                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Result<IPage<TrainingPlan>> result = new Result<IPage<TrainingPlan>>();
        QueryWrapper<TrainingPlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", 0);
        if (trainingPlan.getStartTime() != null && trainingPlan.getEndTime() != null) {
            queryWrapper.between("start_date", trainingPlan.getStartTime(), trainingPlan.getEndTime());
            queryWrapper.between("end_date", trainingPlan.getStartTime(), trainingPlan.getEndTime());
        }
        if (trainingPlan.getName() != null) {
            queryWrapper.like("name", trainingPlan.getName());
        }
        if (trainingPlan.getTrainingType() != null) {
            queryWrapper.eq("training_type", trainingPlan.getTrainingType());
        }
        if (trainingPlan.getAddress() != null) {
            queryWrapper.like("address", trainingPlan.getAddress());
        }
        Page<TrainingPlan> page = new Page<TrainingPlan>(pageNo, pageSize);
        IPage<TrainingPlan> pageList = trainingPlanService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param trainingPlan
     * @return
     */
    @AutoLog(value = "培训计划-添加")
    @ApiOperation(value = "培训计划-添加", notes = "培训计划-添加")
    @PostMapping(value = "/add")
    public Result<TrainingPlan> add(@RequestBody TrainingPlan trainingPlan) {
        Result<TrainingPlan> result = new Result<TrainingPlan>();
        try {
            trainingPlanService.save(trainingPlan);
            for (String id : trainingPlan.getPlanObj()) {
                SysUser sysUser = sysUserService.getUserAllInfoByUserId(id);
                TrainingPlanObj trainingPlanObj = new TrainingPlanObj();
                trainingPlanObj.setPlanId(trainingPlan.getId());
                trainingPlanObj.setPlanObj(sysUser.getId());
                trainingPlanObjService.save(trainingPlanObj);
            }
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param trainingPlan
     * @return
     */
    @AutoLog(value = "培训计划-编辑")
    @ApiOperation(value = "培训计划-编辑", notes = "培训计划-编辑")
    @PutMapping(value = "/edit")
    public Result<TrainingPlan> edit(@RequestBody TrainingPlan trainingPlan) {
        Result<TrainingPlan> result = new Result<TrainingPlan>();
        TrainingPlan trainingPlanEntity = trainingPlanService.getById(trainingPlan.getId());
        if (trainingPlanEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = trainingPlanService.updateById(trainingPlan);
            //TODO 返回false说明什么？
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "培训计划-通过id删除")
    @ApiOperation(value = "培训计划-通过id删除", notes = "培训计划-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            trainingPlanService.removeById(id);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "培训计划-批量删除")
    @ApiOperation(value = "培训计划-批量删除", notes = "培训计划-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<TrainingPlan> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<TrainingPlan> result = new Result<TrainingPlan>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.trainingPlanService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "培训计划-通过id查询")
    @ApiOperation(value = "培训计划-通过id查询", notes = "培训计划-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<TrainingPlan> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<TrainingPlan> result = new Result<TrainingPlan>();
        TrainingPlan trainingPlan = trainingPlanService.getById(id);
        if (trainingPlan == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(trainingPlan);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
        // Step.1 组装查询条件
        QueryWrapper<TrainingPlan> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                TrainingPlan trainingPlan = JSON.parseObject(deString, TrainingPlan.class);
                queryWrapper = QueryGenerator.initQueryWrapper(trainingPlan, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<TrainingPlan> pageList = trainingPlanService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "培训计划列表");
        mv.addObject(NormalExcelConstants.CLASS, TrainingPlan.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("培训计划列表数据", "导出人:Jeecg", "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
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
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<TrainingPlan> listTrainingPlans = ExcelImportUtil.importExcel(file.getInputStream(), TrainingPlan.class, params);
                trainingPlanService.saveBatch(listTrainingPlans);
                return Result.ok("文件导入成功！数据行数:" + listTrainingPlans.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.ok("文件导入失败！");
    }

    /**
     * 课件list
     *
     * @return
     */
    @AutoLog(value = "培训计划-课件list")
    @ApiOperation(value = "培训计划-课件list", notes = "培训计划-课件list")
    @GetMapping(value = "/coursewareList")
    public Result<List<CoursewareStock>> coursewareList() {
        Result<List<CoursewareStock>> result = new Result<List<CoursewareStock>>();
        QueryWrapper<CoursewareStock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", 0);
        List<CoursewareStock> coursewareStockList = coursewareStockService.list(queryWrapper);
        result.setResult(coursewareStockList);
        result.setSuccess(true);
        return result;
    }

    /**
     * 培训对象list
     *
     * @return
     */
    @AutoLog(value = "培训计划-培训对象list")
    @ApiOperation(value = "培训计划-培训对象list", notes = "培训计划-培训对象list")
    @GetMapping(value = "/objectList")
    public Result<List<SysUser>> objectList() {
        Result<List<SysUser>> result = new Result<List<SysUser>>();
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", 0);
        List<SysUser> sysUserList = sysUserService.list(queryWrapper);
        result.setResult(sysUserList);
        result.setSuccess(true);
        return result;
    }

    /**
     * 课程学员详细信息
     *
     * @param id
     * @return
     */
    @AutoLog(value = "培训计划-课程学员详细信息")
    @ApiOperation(value = "培训计划-课程学员详细信息", notes = "培训计划-课程学员详细信息")
    @GetMapping(value = "/objectDetails")
    public Result<List<TrainingPlanObj>> objectDetails(Long id) {
        Result<List<TrainingPlanObj>> result = new Result<List<TrainingPlanObj>>();
        QueryWrapper<TrainingPlanObj> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", 0);
        queryWrapper.eq("plan_id", id);
        List<TrainingPlanObj> trainingPlanObjList = trainingPlanObjService.list(queryWrapper);
        result.setResult(trainingPlanObjList);
        result.setSuccess(true);
        return result;
    }

    /**
     * 课程课件详细信息
     * @param id
     * @return
     */
    @AutoLog(value = "培训计划-课程课件详细信息")
    @ApiOperation(value = "培训计划-课程课件详细信息", notes = "培训计划-课程课件详细信息")
    @GetMapping(value = "/coursewareDetails")
    public Result<List<CoursewareStock>> coursewareDetails(Long id) {
        Result<List<CoursewareStock>> result = new Result<List<CoursewareStock>>();

        QueryWrapper<TrainingPlan> trainingPlanQueryWrapper = new QueryWrapper<>();
        trainingPlanQueryWrapper.eq("del_flag", 0);
        trainingPlanQueryWrapper.eq("id",id);
        TrainingPlan trainingPlan=trainingPlanService.getOne(trainingPlanQueryWrapper);

        List<CoursewareStock> coursewareStockList=new ArrayList<CoursewareStock>();

        String coursewares = trainingPlan.getCoursewares();
        String[] split = coursewares.split(",");
        for (String s : split) {
            QueryWrapper<CoursewareStock> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("del_flag", 0);
            queryWrapper.eq("id", s);
            CoursewareStock coursewareStock=coursewareStockService.getOne(queryWrapper);
            coursewareStockList.add(coursewareStock);
        }
        result.setResult(coursewareStockList);
        result.setSuccess(true);
        return result;
    }




}
