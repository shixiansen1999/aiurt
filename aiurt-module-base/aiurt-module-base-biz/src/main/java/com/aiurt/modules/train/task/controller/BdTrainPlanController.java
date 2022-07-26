package com.aiurt.modules.train.task.controller;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.train.task.constans.TainPlanConstans;
import com.aiurt.modules.train.task.entity.BdTrainPlan;
import com.aiurt.modules.train.task.entity.BdTrainPlanSub;
import com.aiurt.modules.train.task.service.IBdTrainPlanService;
import com.aiurt.modules.train.task.service.IBdTrainPlanSubService;
import com.aiurt.modules.train.task.vo.*;
import com.aiurt.modules.train.utils.DlownTemplateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 年计划
 * @Author: jeecg-boot
 * @Date: 2022-04-20
 * @Version: V1.0
 */
@Api(tags = "年计划")
@RestController
@RequestMapping("/task/bdTrainPlan")
@Slf4j
public class BdTrainPlanController extends BaseController<BdTrainPlan, IBdTrainPlanService> {
    @Autowired
    private IBdTrainPlanService bdTrainPlanService;
    @Autowired
    private IBdTrainPlanSubService bdTrainPlanSubService;

    @Autowired
    private ISysBaseAPI sysBaseAPI;
    /**
     * 分页列表查询
     *
     * @param bdTrainPlan
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "年计划-分页列表查询")
    @ApiOperation(value = "年计划-分页列表查询", notes = "年计划-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(BdTrainPlan bdTrainPlan,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (Objects.isNull(sysUser)) {
            return Result.OK();
        }
        //仅管理员查看
       /* ICommonService commonService = SpringContextUtils.getBean(ICommonService.class);
        Boolean admin = commonService.isAdmin(sysUser.getId())*/;

        if (StrUtil.isNotBlank(bdTrainPlan.getPlanName())) {
            bdTrainPlan.setPlanName("%" + bdTrainPlan.getPlanName() + "%");
        }
        QueryWrapper<BdTrainPlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(bdTrainPlan.getPlanName()), "plan_name", bdTrainPlan.getPlanName());
        queryWrapper.eq(bdTrainPlan.getState() != null, "state", bdTrainPlan.getState());
        queryWrapper.eq(bdTrainPlan.getPlanYear() != null, "plan_year", bdTrainPlan.getPlanYear());
        queryWrapper.orderByDesc("id");
        //if (!admin) {
          /*  Integer teamId = sysUser.getTeamId();
            IBdTeamService teamService = SpringContextUtils.getBean(IBdTeamService.class);
            if (Objects.nonNull(teamId))  {
                BdTeam bdTeam = teamService.getById(teamId);
                if (Objects.nonNull(bdTeam)) {
                    queryWrapper.eq("dept_name", bdTeam.getName());
                }
            }*/
        //}

        Page<BdTrainPlan> page = new Page<BdTrainPlan>(pageNo, pageSize);
        IPage<BdTrainPlan> pageList = bdTrainPlanService.page(page, queryWrapper);
        List<BdTrainPlan> records = pageList.getRecords();
        for (BdTrainPlan record : records) {
            //状态翻译
            if (record.getState().equals(TainPlanConstans.IS_PUBLISH)) {
                record.setStateText("已发布");
            } else {
                record.setStateText("未发布");
            }
        }
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param bdTrainPlan
     * @return
     */
    @AutoLog(value = "年计划-添加")
    @ApiOperation(value = "年计划-添加", notes = "年计划-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody BdTrainPlan bdTrainPlan) {
        bdTrainPlanService.save(bdTrainPlan);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param bdTrainPlan
     * @return
     */
    @AutoLog(value = "年计划-编辑")
    @ApiOperation(value = "年计划-编辑", notes = "年计划-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody BdTrainPlan bdTrainPlan) {
        bdTrainPlanService.updateById(bdTrainPlan);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "年计划-通过id删除")
    @ApiOperation(value = "年计划-通过id删除", notes = "年计划-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        bdTrainPlanService.removeById(id);
        //删除子计划
        bdTrainPlanSubService.deleteByPlanId(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "年计划-批量删除")
    @ApiOperation(value = "年计划-批量删除", notes = "年计划-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.bdTrainPlanService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "年计划-通过id查询")
    @ApiOperation(value = "年计划-通过id查询", notes = "年计划-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        BdTrainPlan bdTrainPlan = bdTrainPlanService.getById(id);
        //查询子计划
        List<BdTrainPlanSub> subList = bdTrainPlanSubService.getByPlanId(id);
        bdTrainPlan.setSubList(subList);
        if (bdTrainPlan == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(bdTrainPlan);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param bdTrainPlan
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BdTrainPlan bdTrainPlan) {
        return super.exportXls(request, bdTrainPlan, BdTrainPlan.class, "年计划");
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
        return super.importExcel(request, response, BdTrainPlan.class);
    }

    /**
     * 发布
     *
     * @param id
     * @return
     */
    @AutoLog(value = "年计划-发布")
    @ApiOperation(value = "年计划-发布", notes = "年计划-发布")
    @PutMapping(value = "/publish")
    public Result<?> publish(@RequestParam(name = "id", required = true) String id) {
        bdTrainPlanService.publish(id);
        return Result.OK("发布成功!");
    }

    /**
     * 培训报表管理
     *
     * @param reportReqVO
     * @return
     */
    @AutoLog(value = "培训报表管理")
    @ApiOperation(value = "培训报表管理", notes = "培训报表管理")
    @PostMapping(value = "/report")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = ReportVO.class)
    })
    public Result<?> report(@RequestBody ReportReqVO reportReqVO) {
        Page<ReportVO> page = new Page<>(reportReqVO.getPageNo(), reportReqVO.getPageSize());
        IPage<ReportVO> pageList = bdTrainPlanService.report(page, reportReqVO);
        return Result.OK(pageList);
    }

    /**
     * 培训报表标题获取
     *
     * @param reportReqVO
     * @return
     */
    @AutoLog(value = "培训报表标题获取")
    @ApiOperation(value = "培训报表标题获取", notes = "培训报表标题获取")
    @PostMapping(value = "/reportTitle")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = TitleReportVo.class)
    })
    public Result<?> reportTitle(@RequestBody ReportReqVO reportReqVO) {
        TitleReportVo vo = bdTrainPlanService.reportTitle(reportReqVO);
        return Result.OK(vo);
    }

    /**
     * 培训报表导出
     *
     * @param request
     * @return
     */
    @AutoLog(value = "培训报表导出")
    @ApiOperation(value = "培训报表导出", notes = "培训报表导出")
    @GetMapping(value = "/reportExport")
    public ModelAndView reportExport(HttpServletRequest request, ReportReqVO reportReqVO) {
        return bdTrainPlanService.reportExport(request, reportReqVO);
    }

    /**
     * 培训年计划导入
     *
     * @param request
     * @return
     */
    @AutoLog(value = "培训年计划导入")
    @ApiOperation(value = "培训年计划导入", notes = "培训年计划导入")
    @PostMapping(value = "/yearPlanImport")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = BdTrainPlan.class)
    })
    public Result<?> yearPlanImport(HttpServletRequest request) throws IOException {
        BdTrainPlan bdTrainPlan = bdTrainPlanService.yearPlanImport(request);
        return Result.OK(bdTrainPlan);
    }

    /**
     * 培训年计划保存
     *
     * @param bdTrainPlan
     * @return
     */
    @AutoLog(value = "培训年计划保存")
    @ApiOperation(value = "培训年计划保存", notes = "培训年计划保存")
    @PostMapping(value = "/yearPlanSave")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = BdTrainPlan.class)
    })
    public Result<?> yearPlanSave(@RequestBody BdTrainPlan bdTrainPlan) {
        bdTrainPlanService.yearPlanSave(bdTrainPlan);
        return Result.OK("保存成功！");
    }

    /**
     * 培训复核管理
     *
     * @param reCheckReqVo
     * @return
     */
    @AutoLog(value = "培训复核管理")
    @ApiOperation(value = "培训复核管理", notes = "培训复核管理")
    @PostMapping(value = "/getReCheckList")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = ReCheckVO.class)
    })
    public Result<?> getReCheckList(@RequestBody ReCheckReqVo reCheckReqVo) {
        Page<ReCheckVO> page = new Page<>(reCheckReqVo.getPageNo(), reCheckReqVo.getPageSize());
        IPage<ReCheckVO> pageList = bdTrainPlanService.getReCheckList(page, reCheckReqVo);
        return Result.OK(pageList);
    }
    /**
     * 培训复核管理-复核
     *
     * @param id
     * @return
     */
    @AutoLog(value = "培训复核管理-复核")
    @ApiOperation(value = "培训复核管理-复核", notes = "培训复核管理-复核")
    @PostMapping(value = "/reCheck")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = QuestionReCheckVO.class)
    })
    public Result<?> reCheck(@RequestParam(name = "id", required = true) String id) {
        List<QuestionReCheckVO> list = bdTrainPlanService.reCheck(id);
        return Result.OK(list);
    }

    /**
     * 培训复核管理-提交审核结果
     *
     * @param reqList
     * @return
     */
    @AutoLog(value = "培训复核管理-提交审核结果")
    @ApiOperation(value = "培训复核管理-提交审核结果", notes = "培训复核管理-提交审核结果")
    @PostMapping(value = "/submitReCheck")
    public Result<?> submitReCheck(@ApiParam("reqList") @RequestBody List<ShortQuesReqVo> reqList) {
        bdTrainPlanService.submitReCheck(reqList);
        return Result.OK("复核成功");
    }

    /**
     * 培训复核管理-发布
     *
     * @param reCheckVOList
     * @return
     */
    @AutoLog(value = "培训复核管理-发布")
    @ApiOperation(value = "培训复核管理-发布", notes = "培训复核管理-发布")
    @PostMapping(value = "/reCheckPublish")
    public Result<?> reCheckPublish(@RequestBody List<ReCheckVO> reCheckVOList) {
        bdTrainPlanService.reCheckPublish(reCheckVOList);
        return Result.OK("复核成功");
    }

    /**
     * 培训报表管理-部门下拉数据
     *
     * @return
     */
    @AutoLog(value = "培训报表管理-部门下拉数据")
    @ApiOperation(value = "培训报表管理-部门下拉数据", notes = "培训报表管理-部门下拉数据")
    @PostMapping(value = "/getDept")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = String.class)
    })
    public Result<?> getDept() {
        List<String> depts = bdTrainPlanService.getDept();
        return Result.OK(depts);
    }

    @RequestMapping("noAuthDownLoad")
    @ApiOperation(value = "培训计划内容导入模板", notes = "培训计划内容导入模板")
    public String downLoad(HttpServletRequest request, HttpServletResponse response) {
        String fileName = "xxxx年内部培训计划表模板.xlsx";
        return DlownTemplateUtil.downloadTemplet(request, response, fileName);
    }
}
