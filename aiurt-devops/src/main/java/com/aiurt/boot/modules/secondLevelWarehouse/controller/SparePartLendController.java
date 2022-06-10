package com.aiurt.boot.modules.secondLevelWarehouse.controller;


import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartLend;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartLendDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartLendParam;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartLendVO;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartLendService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @Description: 备件借出表
 * @Author: qian
 * @Date: 2021-09-22
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "备件借出表")
@RestController
@RequestMapping("/secondLevelWarehouse/sparePartLend")
public class SparePartLendController {
    @Autowired
    private ISparePartLendService sparePartLendService;

    /**
     * 备件借出表-分页列表查询
     * @param param
     * @param req
     * @return
     */
    @AutoLog(value = "备件借出表-分页列表查询")
    @ApiOperation(value = "备件借出表-分页列表查询", notes = "备件借出表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SparePartLendVO>> queryPageList(SparePartLendVO sparePartLendVO,
                                                        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                        SparePartLendParam param,
                                                        HttpServletRequest req) {
        Result<IPage<SparePartLendVO>> result = new Result<IPage<SparePartLendVO>>();
        QueryWrapper<SparePartLendVO> queryWrapper = QueryGenerator.initQueryWrapper(sparePartLendVO, req.getParameterMap());
        Page<SparePartLendVO> page = new Page<>(pageNo, pageSize);
        IPage<SparePartLendVO> pageList = sparePartLendService.queryPageList(page,queryWrapper, param);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     * @param dto
     * @return
     */
    @AutoLog(value = "备件借出表-添加")
    @ApiOperation(value = "备件借出表-添加", notes = "备件借出表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@Valid @RequestBody SparePartLendDTO dto, HttpServletRequest req) {
        Result<?> result = new Result<>();
        try {
            result = sparePartLendService.addLend(result, dto, req);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * 借出确认
     * @param id
     * @param confirmNum
     * @param req
     * @return
     */
    @AutoLog(value = "借出确认")
    @ApiOperation(value = "借出确认", notes = "借出确认")
    @GetMapping(value = "/lendConfirm")
    public Result<SparePartLend> lendConfirm(@RequestParam("id")Integer id,
                                             @RequestParam("confirmNum")Integer confirmNum,
                                             HttpServletRequest req){
        Result<SparePartLend> result = new Result<SparePartLend>();
        SparePartLend sparePartLend = sparePartLendService.getById(id);
        if (confirmNum == null || confirmNum < 1) {
            return result.error500("确认数量不能为空或者小于1");
        }
        if (sparePartLend == null) {
            return result.onnull("未找到对应实体");
        }
        try {
            sparePartLendService.lendConfirm(sparePartLend, confirmNum, req);
            result.success("确认成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("确认失败"+e.getMessage());
        }
        return result;
    }


    /**
     * 备件还回
     * @param id
     * @param returnNum
     * @param req
     * @return
     */
    @AutoLog(value = "备件还回")
    @ApiOperation(value = "备件还回", notes = "备件还回")
    @GetMapping(value = "/edit")
    public Result<SparePartLend> edit(@ApiParam("id") @RequestParam("id") Integer id,
                                      @ApiParam("还回数量") @RequestParam("returnNum") Integer returnNum,
                                      HttpServletRequest req) {
        Result<SparePartLend> result = new Result<SparePartLend>();
        SparePartLend sparePartLendEntity = sparePartLendService.getById(id);
        if (returnNum == null || returnNum < 1) {
            return result.error500("还回数量不能为空或者小于1");
        }
        if (sparePartLendEntity == null) {
            return result.onnull("未找到对应实体");
        }
        try {
            sparePartLendService.returnMaterial(sparePartLendEntity, returnNum, req);
            result.success("修改成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败"+e.getMessage());
        }
        return result;
    }


    /**
     * 导出excel
     * @param param
     * @return
     */
    @AutoLog("备件借出信息-导出")
    @ApiOperation("备件借出信息导出")
    @GetMapping(value = "/exportXls")
    public ModelAndView exportXls(SparePartLendParam param) {
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SparePartLendVO> iPage = sparePartLendService.exportXls(param);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "备件借出表列表");
        mv.addObject(NormalExcelConstants.CLASS, SparePartLendVO.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备件借出表列表数据","导出信息", ExcelType.XSSF));
        mv.addObject(NormalExcelConstants.DATA_LIST, iPage);
        return mv;
    }


}
