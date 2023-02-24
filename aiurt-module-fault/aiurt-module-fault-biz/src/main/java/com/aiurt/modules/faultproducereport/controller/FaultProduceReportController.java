package com.aiurt.modules.faultproducereport.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultRepairRecord;
import com.aiurt.modules.fault.service.IFaultRepairRecordService;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.faultproducereport.dto.FaultProduceReportDTO;
import com.aiurt.modules.faultproducereport.entity.FaultProduceReport;
import com.aiurt.modules.faultproducereport.service.IFaultProduceReportService;
import com.aiurt.modules.faultproducereportline.entity.FaultProduceReportLine;
import com.aiurt.modules.faultproducereportline.service.IFaultProduceReportLineService;
import com.aiurt.modules.faultproducereportlinedetail.dto.FaultProduceReportLineDetailDTO;
import com.aiurt.modules.faultproducereportlinedetail.entity.FaultProduceReportLineDetail;
import com.aiurt.modules.faultproducereportlinedetail.service.IFaultProduceReportLineDetailService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @AutoLog(value = "生产日报-获取当前登录用户的专业")
    @ApiOperation(value = "生产日报-获取当前登录用户的专业", notes = "生产日报-获取当前登录用户的专业")
    @GetMapping("/getLoginUserMajors")
    public Result<List<CsUserMajorModel>> getLoginUserMajors(){
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserMajorModel> CsUserMajorModelList = iSysBaseAPI.getMajorByUserId(user.getId());
        return Result.OK(CsUserMajorModelList);
    }

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
        // 自己写查询
        Page<FaultProduceReport> pageList = new Page<>(pageNo, pageSize);
        return faultProduceReportService.queryPageList(pageList, faultProduceReport, beginDay, endDay);
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
    @AutoLog(value = "生产日报-通过id查询")
    @ApiOperation(value = "生产日报-通过id查询", notes = "生产日报-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<FaultProduceReportDTO> queryById(@RequestParam(name = "id", required = true) String id) {
        // 1、根据id查询出生产日报数据
        FaultProduceReport report = faultProduceReportService.getById(id);
        if (report == null) {
            return Result.error("未找到对应数据");
        }
        // 最后返回的是reportDTO
        FaultProduceReportDTO reportDTO = new FaultProduceReportDTO();
        // 将report的数据复制到reportDTO当中
        BeanUtils.copyProperties(report, reportDTO);
        // 2、再查询出该生产日报的线路故障数据，
        LambdaQueryWrapper<FaultProduceReportLine> reportLineLambdaQueryWrapper = new LambdaQueryWrapper<>();
        reportLineLambdaQueryWrapper.eq(FaultProduceReportLine::getFaultProduceReportId, report.getId());
        List<FaultProduceReportLine> reportLineList = iFaultProduceReportLineService.list(reportLineLambdaQueryWrapper);
        // 将reportLineList放入reportDTO
        reportDTO.setReportLineList(reportLineList);
        // 3、根据线路故障、查询出故障清单数据，转为reportLineDetailDTO后存入reportLineDetailDTOList
        List<FaultProduceReportLineDetailDTO> reportLineDetailDTOList = new ArrayList<>();
        for (FaultProduceReportLine reportLine: reportLineList) {
            LambdaQueryWrapper<FaultProduceReportLineDetail> reportLineDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            reportLineDetailLambdaQueryWrapper.eq(FaultProduceReportLineDetail::getFaultProduceReportLineId, reportLine.getId());
            reportLineDetailLambdaQueryWrapper.orderByDesc(FaultProduceReportLineDetail::getCreateTime);
            List<FaultProduceReportLineDetail> reportLineDetailList = iFaultProduceReportLineDetailService.list(reportLineDetailLambdaQueryWrapper);
            reportLineDetailList.forEach(item->{
                // 新建一个reportLineDetailDTO
                FaultProduceReportLineDetailDTO reportLineDetailDTO = new FaultProduceReportLineDetailDTO();
                // 给reportLineDetailDTO赋值
                BeanUtils.copyProperties(item, reportLineDetailDTO);
                reportLineDetailDTO.setMajorCode(report.getMajorCode());
                List<String> csMajorNamesByCodes = iSysBaseAPI.getCsMajorNamesByCodes(Collections.singletonList(report.getMajorCode()));
                if (csMajorNamesByCodes.size() > 0) {
                    reportLineDetailDTO.setMajorName(csMajorNamesByCodes.get(0));
                }
                reportLineDetailDTOList.add(reportLineDetailDTO);
            });
        }
        // 将reportLineDetailDTOList放入reportDTO
        reportDTO.setReportLineDetailDTOList(reportLineDetailDTOList);
        return Result.ok(reportDTO);
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
