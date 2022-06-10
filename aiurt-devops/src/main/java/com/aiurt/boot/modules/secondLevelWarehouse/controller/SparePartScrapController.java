package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.exception.SwscException;
import com.aiurt.boot.common.result.ReportRepairResult;
import com.aiurt.boot.common.result.ReportWasteResult;
import com.aiurt.boot.common.result.ScrapReportResult;
import com.aiurt.boot.common.system.api.ISysBaseAPI;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.common.util.TokenUtils;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartScrap;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartStock;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.ReportRepairDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.ReportWasteDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartScrapExcel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartScrapQuery;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartScrapVO;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartScrapService;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartStockService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * @Description: 备件报损
 * @Author: qian
 * @Date: 2021-09-23
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "备件报损")
@RestController
@RequestMapping("/secondLevelWarehouse/sparePartScrap")
public class SparePartScrapController {
    @Resource
    private ISparePartScrapService sparePartScrapService;
    @Resource
    private ISysBaseAPI iSysBaseAPI;
    @Resource
    private ISparePartStockService sparePartStockService;

    /**
     * 分页列表查询
     *
     * @param sparePartScrapQuery
     * @param req
     * @return
     */
    @AutoLog(value = "备件报损-分页列表查询")
    @ApiOperation(value = "备件报损-分页列表查询", notes = "备件报损-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SparePartScrapVO>> queryPageList(
            SparePartScrapQuery sparePartScrapQuery,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest req) {
        Result<IPage<SparePartScrapVO>> result = new Result<IPage<SparePartScrapVO>>();
        Page<SparePartScrapVO> page = new Page<SparePartScrapVO>(pageNo, pageSize);
        IPage<SparePartScrapVO> pageList = sparePartScrapService.queryPageList(page, sparePartScrapQuery);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param sparePartScrap
     * @return
     */
    @AutoLog(value = "备件报损-添加")
    @ApiOperation(value = "备件报损-添加", notes = "备件报损-添加")
    @PostMapping(value = "/add")
    public Result<SparePartScrap> add(@Valid @RequestBody SparePartScrap sparePartScrap, HttpServletRequest req) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        SparePartStock stock = sparePartStockService.lambdaQuery().eq(SparePartStock::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(SparePartStock::getMaterialCode, sparePartScrap.getMaterialCode())
                .eq(SparePartStock::getOrgId, user.getOrgId()).last("limit 1").one();

        if (stock == null || (stock.getNum()!=null && stock.getNum() < sparePartScrap.getNum())){
            return Result.error("备件库中数量不足,无法报废");
        }

        Result<SparePartScrap> result = new Result<SparePartScrap>();
        try {
            sparePartScrap.setUpdateBy(user.getId());
            sparePartScrap.setOrgId(user.getOrgId());
            sparePartScrap.setCreateBy(user.getId());
            sparePartScrapService.save(sparePartScrap);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败"+":"+e.getMessage());
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param sparePartScrap
     * @return
     */
    @AutoLog(value = "备件报损-编辑")
    @ApiOperation(value = "备件报损-编辑", notes = "备件报损-编辑")
    @PutMapping(value = "/edit")
    public Result<SparePartScrap> edit(@RequestBody SparePartScrap sparePartScrap,HttpServletRequest req) {
        String userId = TokenUtils.getUserId(req, iSysBaseAPI);
        Result<SparePartScrap> result = new Result<SparePartScrap>();
        SparePartScrap sparePartScrapEntity = sparePartScrapService.getById(sparePartScrap.getId());
        if (sparePartScrapEntity == null) {
            result.onnull("未找到对应实体");
        } else {
            sparePartScrap.setUpdateBy(userId);
            boolean ok = sparePartScrapService.updateById(sparePartScrap);

            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 导出excel
     * @param sparePartScrapQuery
     * @return
     */
    @AutoLog("备件报损信息-导出")
    @ApiOperation("备件报损信息导出")
    @GetMapping(value = "/exportXls")
    public ModelAndView exportXls(SparePartScrapQuery sparePartScrapQuery) {
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SparePartScrapExcel> list = sparePartScrapService.exportXls(sparePartScrapQuery);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "备件报损信息列表");
        mv.addObject(NormalExcelConstants.CLASS, SparePartScrapExcel.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备件报损信息列表数据","导出信息", ExcelType.XSSF));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    /**
     * 根据id查询报损详情
     * @param id
     * @return
     */
    @AutoLog(value = "根据id查询报损详情")
    @ApiOperation(value = "根据id查询报损详情", notes = "根据id查询报损详情")
    @GetMapping(value = "/scrapReportDetail")
    public Result<ScrapReportResult> scrapReportDetail(@RequestParam (name = "id",required = true) String id){
        Result<ScrapReportResult> detail = sparePartScrapService.getDetailById(id);
        return detail;
    }

    /**
     * 报修
     * @param dto
     * @return
     */
    @AutoLog(value = "报修")
    @ApiOperation(value = "报修", notes = "报修")
    @PostMapping(value = "/reportRepair")
    public Result reportRepair(@Valid @RequestBody ReportRepairDTO dto){
        if (dto.getId()==null) {
            throw new SwscException("id不能为空！");
        }
        Result<Object> result = new Result<>();
        try {
            sparePartScrapService.reportRepair(dto);
            result.success("报修成功");
        }catch (Exception e){
            log.error(e.getMessage(),e);
            result.error500("报修失败"+":"+e.getMessage());
        }
        return result;
    }

    /**
     * 报废
     * @param dto
     * @return
     */
    @AutoLog(value = "报废")
    @ApiOperation(value = "报废", notes = "报废")
    @PostMapping(value = "/reportWaste")
    @Transactional(rollbackFor = Exception.class)
    public Result reportWaste(@Valid @RequestBody ReportWasteDTO dto){
        if (dto.getId()==null) {
            throw new SwscException("id不能为空！");
        }
        SparePartScrap sparePartScrap = sparePartScrapService.getById(dto.getId());
        SparePartStock stock = sparePartStockService.lambdaQuery().eq(SparePartStock::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(SparePartStock::getMaterialCode, sparePartScrap.getMaterialCode())
                .eq(SparePartStock::getOrgId, sparePartScrap.getOrgId()).last("limit 1").one();

        if (stock == null || (stock.getNum()!=null && stock.getNum()<sparePartScrap.getNum())){
            return Result.error("备件库中数量不足,无法报废");
        }
        sparePartScrap.setScrapReason(dto.getScrapReason());
        sparePartScrap.setUseLife(dto.getUseLife());
        sparePartScrap.setServiceLife(dto.getServiceLife());
        sparePartScrap.setRepairTime(new Date());
        sparePartScrap.setBuyTime(dto.getBuyTime());
        //修改状态为报废
        sparePartScrap.setStatus(2);
        if (sparePartScrapService.updateById(sparePartScrap)){
            if (stock.getNum()!=null && sparePartScrap.getNum()!=null) {
                stock.setNum(stock.getNum() - sparePartScrap.getNum());
                sparePartStockService.updateById(stock);
            }
            return Result.ok("报废成功");
        }else {
            throw new SwscException("报废失败");
        }
    }

    /**
     * 获取送修详情
     * @param id
     * @return
     */
    @AutoLog(value = "获取送修详情")
    @ApiOperation(value = "获取送修详情", notes = "获取送修详情")
    @GetMapping(value = "/getRepairDetailById")
    public Result<ReportRepairResult> getRepairDetailById(@RequestParam (name = "id",required = true) String id){
        Result<ReportRepairResult> detail = sparePartScrapService.getRepairDetailById(id);
        return detail;
    }

    /**
     * 获取送报废详情
     * @param id
     * @return
     */
    @AutoLog(value = "获取送报废详情")
    @ApiOperation(value = "获取送报废详情", notes = "根据id查询获报废详情")
    @GetMapping(value = "/getWasteDetailById")
    public Result<ReportWasteResult> getWasteDetailById(@RequestParam (name = "id",required = true) String id){
        Result<ReportWasteResult> detail = sparePartScrapService.getWasteDetailById(id);
        return detail;
    }

}
