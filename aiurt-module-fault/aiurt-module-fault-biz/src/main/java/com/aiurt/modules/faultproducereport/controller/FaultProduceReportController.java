package com.aiurt.modules.faultproducereport.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.fault.service.IFaultRepairRecordService;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.faultproducereport.entity.FaultProduceReport;
import com.aiurt.modules.faultproducereport.service.IFaultProduceReportService;
import com.aiurt.modules.faultproducereportline.service.IFaultProduceReportLineService;
import com.aiurt.modules.faultproducereportlinedetail.service.IFaultProduceReportLineDetailService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 生产日报
 * @Author: aiurt
 * @Date: 2023-02-23
 * @Version: V1.0
 */
@Api(tags = "生产日报")
@RestController
@RequestMapping("/faultproducereport/faultProduceReport")
@Slf4j
public class FaultProduceReportController extends BaseController<FaultProduceReport, IFaultProduceReportService> {
    @Autowired
    private IFaultProduceReportService faultProduceReportService;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;
    @Autowired
    private IFaultService iFaultService;
    @Autowired
    private IFaultProduceReportLineService iFaultProduceReportLineService;
    @Autowired
    private IFaultRepairRecordService iFaultRepairRecordService;
    @Autowired
    private IFaultProduceReportLineDetailService iFaultProduceReportLineDetailService;

    /**
     * 分页列表查询
     * 查询参数只能是专业、统计时间（开始时间、结束时间）
     *
     * @param faultProduceReport
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "生产日报-分页列表查询")
    @ApiOperation(value = "生产日报-分页列表查询", notes = "生产日报-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<FaultProduceReport>> queryPageList(FaultProduceReport faultProduceReport,
                                                           String beginDay, String endDay,
                                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                           HttpServletRequest req) {
        // 获取到当前登录的用户的专业(majorCode、可能有多个，使用List存储)
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserMajorModel> CsUserMajorModelList = iSysBaseAPI.getMajorByUserId(user.getId());
        List<String> majorCodeList = new ArrayList<>();
        for (CsUserMajorModel csUserMajorModel : CsUserMajorModelList) {
            majorCodeList.add(csUserMajorModel.getMajorCode());
        }
        // 如果查询参数有majorCode，这个majorCode在当前登录的用户的专业内，查询的专业只查询这个majorCode
        if (faultProduceReport.getMajorCode() != null) {
            if (majorCodeList.contains(faultProduceReport.getMajorCode())) {
                majorCodeList.clear();
                majorCodeList.add(faultProduceReport.getMajorCode());
            }
        }

        // 根据专业(List)、统计时间创建查询条件QueryWrapper
        LambdaQueryWrapper<FaultProduceReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FaultProduceReport::getMajorCode, majorCodeList);

        // 不传时间参数，默认查询所有
        // 传有时间参数的话，统计时间大于等于开始时间，小于等于结束时间
        String[] pattern = new String[]{"yyyy-MM-dd HH:mm:ss"};
        // 时间是否是指定格式(日期格式：yyyy-MM-dd)， 不是指定格式的话，舍弃
        if (beginDay != null){
            try {
                queryWrapper.ge(FaultProduceReport::getStatisticsDate,
                        DateUtils.parseDate(beginDay + " 00:00:00", pattern));
            } catch (Exception ignored){}
        }
        if (endDay != null){
            try {
                queryWrapper.le(FaultProduceReport::getStatisticsDate,
                        DateUtils.parseDate(endDay + " 23:59:59", pattern));
            } catch (Exception ignored){}
        }

        Page<FaultProduceReport> page = new Page<FaultProduceReport>(pageNo, pageSize);
        IPage<FaultProduceReport> pageList = faultProduceReportService.page(page, queryWrapper);
        return Result.OK(pageList);
    }


    /**
     * 查看
     *
     * @param
     * @return
     */
    @AutoLog(value = "生产日报-查看")
    @ApiOperation(value = "生产日报-查看", notes = "生产日报-查看")
    @PostMapping(value = "/getDetail")
    public Result<FaultProduceReport> getDetail() {

        return faultProduceReportService.getDetail();
    }
    /**
     * 添加
     *
     * @param faultProduceReport
     * @return
     */
    @AutoLog(value = "生产日报-添加")
    @ApiOperation(value = "生产日报-添加", notes = "生产日报-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody FaultProduceReport faultProduceReport) {
        faultProduceReportService.save(faultProduceReport);
        return Result.OK("添加成功！");
    }


    /**
     * 编辑
     *
     * @param faultProduceReport
     * @return
     */
    @AutoLog(value = "生产日报-编辑")
    @ApiOperation(value = "生产日报-编辑", notes = "生产日报-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody FaultProduceReport faultProduceReport) {
        faultProduceReportService.updateById(faultProduceReport);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "生产日报-通过id删除")
    @ApiOperation(value = "生产日报-通过id删除", notes = "生产日报-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        faultProduceReportService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "生产日报-批量删除")
    @ApiOperation(value = "生产日报-批量删除", notes = "生产日报-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.faultProduceReportService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "生产日报-通过id查询")
    @ApiOperation(value = "生产日报-通过id查询", notes = "生产日报-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<FaultProduceReport> queryById(@RequestParam(name = "id", required = true) String id) {
        FaultProduceReport faultProduceReport = faultProduceReportService.getById(id);
        if (faultProduceReport == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(faultProduceReport);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param faultProduceReport
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FaultProduceReport faultProduceReport) {
        return super.exportXls(request, faultProduceReport, FaultProduceReport.class, "生产日报");
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
        return super.importExcel(request, response, FaultProduceReport.class);
    }

}
