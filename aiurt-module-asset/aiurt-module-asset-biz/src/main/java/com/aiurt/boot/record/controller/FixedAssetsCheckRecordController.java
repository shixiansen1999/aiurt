package com.aiurt.boot.record.controller;

import com.aiurt.boot.record.dto.FixedAssetsCheckRecordDTO;
import com.aiurt.boot.record.entity.FixedAssetsCheckRecord;
import com.aiurt.boot.record.service.IFixedAssetsCheckRecordService;
import com.aiurt.boot.record.vo.CheckResultTotalVO;
import com.aiurt.boot.record.vo.FixedAssetsCheckRecordVO;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: fixed_assets_check_record
 * @Author: aiurt
 * @Date: 2023-01-11
 * @Version: V1.0
 */
@Api(tags = "固定资产盘点记录表")
@RestController
@RequestMapping("/record/fixedAssetsCheckRecord")
@Slf4j
public class FixedAssetsCheckRecordController extends BaseController<FixedAssetsCheckRecord, IFixedAssetsCheckRecordService> {
    @Autowired
    private IFixedAssetsCheckRecordService fixedAssetsCheckRecordService;

    /**
     * 固定资产盘点记录-盘点结果记录分页查询
     *
     * @param fixedAssetsCheckRecordDTO
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "固定资产盘点记录表-分页列表查询", notes = "固定资产盘点记录表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<FixedAssetsCheckRecordVO>> queryPageList(FixedAssetsCheckRecordDTO fixedAssetsCheckRecordDTO,
                                                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<FixedAssetsCheckRecordVO> page = new Page<FixedAssetsCheckRecordVO>(pageNo, pageSize);
        IPage<FixedAssetsCheckRecordVO> pageList = fixedAssetsCheckRecordService.queryPageList(page, fixedAssetsCheckRecordDTO);
        return Result.OK(pageList);
    }

    /**
     * 固定资产盘点记录-盘点结果记录(不分页)
     *
     * @param fixedAssetsCheckRecordDTO
     * @return
     */
    @ApiOperation(value = "固定资产盘点记录表-盘点结果记录(不分页)", notes = "固定资产盘点记录表-盘点结果记录(不分页)")
    @GetMapping(value = "/nonsortList")
    public Result<List<FixedAssetsCheckRecordVO>> nonsortList(FixedAssetsCheckRecordDTO fixedAssetsCheckRecordDTO) {
        List<FixedAssetsCheckRecordVO> pageList = fixedAssetsCheckRecordService.nonsortList(fixedAssetsCheckRecordDTO);
        return Result.OK(pageList);
    }

    /**
     * 固定资产-盘点结果统计
     */
    @ApiOperation(value = "固定资产-盘点结果统计", notes = "固定资产-盘点结果统计")
    @GetMapping(value = "/checkResultTotal")
    public Result<CheckResultTotalVO> checkResultTotal(@RequestParam @ApiParam(name = "id", value = "盘点任务记录ID") String id) {
        CheckResultTotalVO totalVO = fixedAssetsCheckRecordService.checkResultTotal(id);
        return Result.OK(totalVO);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param fixedAssetsCheckRecord
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FixedAssetsCheckRecord fixedAssetsCheckRecord) {
        return super.exportXls(request, fixedAssetsCheckRecord, FixedAssetsCheckRecord.class, "fixed_assets_check_record");
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
        return super.importExcel(request, response, FixedAssetsCheckRecord.class);
    }

}
