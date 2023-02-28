package com.aiurt.modules.faultproducereport.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

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
    public Result<IPage<FaultProduceReportDTO>> queryPageList(FaultProduceReport faultProduceReport,
                                                           String beginDay, String endDay,
                                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                           HttpServletRequest req) {
        // 自己写查询
        Page<FaultProduceReportDTO> pageList = new Page<>(pageNo, pageSize);
        return faultProduceReportService.queryPageList(pageList, faultProduceReport, beginDay, endDay);
    }

    /**
     * 生产日报审核分页列表查询
     * 查询参数只能是专业、统计时间（开始时间、结束时间）
     *
     * @param faultProduceReport
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "生产日报-审核分页列表查询")
    @ApiOperation(value = "生产日报-审核分页列表查询", notes = "生产日报-审核分页列表查询")
    @GetMapping(value = "/AuditList")
    public Result<IPage<FaultProduceReportDTO>> queryPageAuditList(FaultProduceReport faultProduceReport,
                                                           String beginDay, String endDay,
                                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                           HttpServletRequest req) {
        // 自己写查询
        Page<FaultProduceReportDTO> pageList = new Page<>(pageNo, pageSize);
        return faultProduceReportService.queryPageAuditList(pageList, faultProduceReport, beginDay, endDay);
    }

    /**
     * 提交
     *
     * @param
     * @return
     */
    @AutoLog(value = "生产日报-提交")
    @ApiOperation(value = "生产日报-提交", notes = "生产日报-提交")
    @PostMapping(value = "/workSubmit")
    public Result<FaultProduceReport> workSubmit(@RequestBody FaultProduceReport faultProduceReport) {
        faultProduceReportService.workSubmit(faultProduceReport);
        return Result.OK("提交成功") ;
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
     * @param faultProduceReportDTO
     * @return
     */
    @AutoLog(value = "生产日报-编辑")
    @ApiOperation(value = "生产日报-编辑", notes = "生产日报-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody FaultProduceReportDTO faultProduceReportDTO) {
        Integer state = faultProduceReportDTO.getState();
        // 只有待提审(status=0)或者已驳回(status=0)状态的生产日报才能编辑
        if (!state.equals(0)) {
            return Result.error("只有待提审/已驳回状态的生产日报才能编辑!");
        }
        // 编辑只能编辑故障清单的【处理情况及管控措施】字段
        List<FaultProduceReportLineDetailDTO> reportLineDetailDTOList = faultProduceReportDTO.getReportLineDetailDTOList();
        iFaultProduceReportLineDetailService.updateListByIds(reportLineDetailDTOList);
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
        List<String> csMajorNamesByCodes = iSysBaseAPI.getCsMajorNamesByCodes(Collections.singletonList(report.getMajorCode()));
        String majorName = null;
        if (csMajorNamesByCodes.size() > 0) {
            majorName = csMajorNamesByCodes.get(0);
        }
        reportDTO.setMajorName(majorName);  // 设置专业名称
        // 设置提交人的realname
        if (report.getSubmitUserName() != null) {
            LoginUser submitUser = iSysBaseAPI.queryUser(report.getSubmitUserName());
            reportDTO.setSubmitUserRealname(submitUser.getRealname());
        }

        // 2、再查询出该生产日报的线路故障数据，
        LambdaQueryWrapper<FaultProduceReportLine> reportLineLambdaQueryWrapper = new LambdaQueryWrapper<>();
        reportLineLambdaQueryWrapper.eq(FaultProduceReportLine::getFaultProduceReportId, report.getId());
        List<FaultProduceReportLine> reportLineList = iFaultProduceReportLineService.list(reportLineLambdaQueryWrapper);
        // 将reportLineList放入reportDTO
        reportDTO.setReportLineList(reportLineList);
        // 3、根据生产日报id查询出故障清单数据，转为reportLineDetailDTO后存入reportLineDetailDTOList
        LambdaQueryWrapper<FaultProduceReportLineDetail> reportLineDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
        reportLineDetailLambdaQueryWrapper.eq(FaultProduceReportLineDetail::getFaultProduceReportId, report.getId());
        reportLineDetailLambdaQueryWrapper.orderByDesc(FaultProduceReportLineDetail::getCreateTime);
        List<FaultProduceReportLineDetail> reportLineDetailList = iFaultProduceReportLineDetailService.list(reportLineDetailLambdaQueryWrapper);
        String finalMajorName = majorName;
        List<FaultProduceReportLineDetailDTO> reportLineDetailDTOList = reportLineDetailList.stream().map(item -> {
            // 新建一个reportLineDetailDTO
            FaultProduceReportLineDetailDTO reportLineDetailDTO = new FaultProduceReportLineDetailDTO();
            // 给reportLineDetailDTO赋值
            BeanUtils.copyProperties(item, reportLineDetailDTO);
            reportLineDetailDTO.setMajorCode(report.getMajorCode()); // 设置专业编码
            reportLineDetailDTO.setMajorName(finalMajorName); // 设置专业名称
            // 设置三个是否的中文
            List<DictModel> reportStateList = iSysBaseAPI.getDictItems("fault_yn");
            reportStateList.forEach(dictModel->{
                if (item.getAffectDrive().toString().equals(dictModel.getValue())) {
                    reportLineDetailDTO.setAffectDriveName(dictModel.getText());
                }
                if (item.getAffectPassengerService().toString().equals(dictModel.getValue())) {
                    reportLineDetailDTO.setAffectPassengerServiceName(dictModel.getText());
                }
                if (item.getIsStopService().toString().equals(dictModel.getValue())) {
                    reportLineDetailDTO.setIsStopServiceName(dictModel.getText());
                }
            });
            return reportLineDetailDTO;
        }).collect(Collectors.toList());
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
