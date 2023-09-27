package com.aiurt.modules.sparepart.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.material.service.IMaterialRequisitionService;
import com.aiurt.modules.sparepart.entity.dto.req.SparePartRequisitionAddReqDTO;
import com.aiurt.modules.sparepart.entity.dto.req.SparePartRequisitionListReqDTO;
import com.aiurt.modules.sparepart.entity.dto.resp.SparePartRequisitionListRespDTO;
import com.aiurt.modules.sparepart.service.SparePartRequisitionService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

/**
 * 三级库申领的controller
 *
 * @author 华宜威
 * @date 2023-09-21 09:54:40
 */
@Slf4j
@Api(tags = "三级库管理-三级库申领")
@RestController
@RequestMapping("/sparepart/SparePartRequisition")
public class SparePartRequisitionController {

    @Autowired
    private SparePartRequisitionService sparePartRequisitionService;
    @Autowired
    private IMaterialRequisitionService materialRequisitionService;

    /**
     * 三级库管理-分页列表查询
     *
     * @param sparePartRequisitionListReqDTO 三级库申领分页列表查询的请求DTO
     * @return Result<IPage<StockLevel2RequisitionListRespDTO>> 返回分页列表查询结果
     */
    @AutoLog(value = "三级库管理-三级库申领-分页列表查询")
    @ApiOperation(value="三级库管理-三级库申领-分页列表查询", notes="三级库管理-三级库申领-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SparePartRequisitionListRespDTO>> pageList(SparePartRequisitionListReqDTO sparePartRequisitionListReqDTO){
        Page<SparePartRequisitionListRespDTO> pageList = sparePartRequisitionService.pageList(sparePartRequisitionListReqDTO);
        return Result.ok(pageList);
    }

    /**
     * 三级库管理-添加
     *
     * @param sparePartRequisitionAddReqDTO 三级库申领的添加、编辑等请求DTO
     * @param applyType 领用类型
     * @return Result<String> 返回添加成功提示
     */
    @AutoLog(value = "三级库管理-三级库申领-添加")
    @ApiOperation(value="三级库管理-三级库申领-添加", notes="三级库管理-三级库申领-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody SparePartRequisitionAddReqDTO sparePartRequisitionAddReqDTO, @RequestParam(name ="applyType") Integer applyType) throws ParseException {
        sparePartRequisitionService.add(sparePartRequisitionAddReqDTO,applyType);
        return Result.ok("添加成功！");
    }

    /**
     * 三级库管理-编辑
     *
     * @param sparePartRequisitionAddReqDTOs 三级库申领的添加、编辑等请求DTO
     * @param applyType 领用类型
     * @return Result<String> 返回编辑成功提示
     */
    @AutoLog(value = "三级库管理-三级库申领-编辑")
    @ApiOperation(value="三级库管理-三级库申领-编辑", notes="三级库管理-三级库申领-编辑")
    @PostMapping(value = "/edit")
    public Result<String> edit(@RequestBody List<SparePartRequisitionAddReqDTO> sparePartRequisitionAddReqDTOs, @RequestParam(name ="applyType") Integer applyType){
        sparePartRequisitionService.edit(sparePartRequisitionAddReqDTOs,applyType);
        return Result.ok("编辑成功！");
    }
}
