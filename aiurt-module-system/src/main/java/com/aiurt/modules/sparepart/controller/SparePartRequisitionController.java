package com.aiurt.modules.sparepart.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.util.CodeGenerateUtils;
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
     * @return Result<String> 返回添加成功提示
     */
    @AutoLog(value = "三级库管理-三级库申领-添加")
    @ApiOperation(value="三级库管理-三级库申领-添加", notes="三级库管理-三级库申领-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody SparePartRequisitionAddReqDTO sparePartRequisitionAddReqDTO) throws ParseException {
        sparePartRequisitionService.add(sparePartRequisitionAddReqDTO);
        return Result.ok("添加成功！");
    }

    /**
     * 三级库管理-编辑
     *
     * @param sparePartRequisitionAddReqDTO 三级库申领的添加、编辑等请求DTO
     * @return Result<String> 返回编辑成功提示
     */
    @AutoLog(value = "三级库管理-三级库申领-编辑")
    @ApiOperation(value="三级库管理-三级库申领-编辑", notes="三级库管理-三级库申领-编辑")
    @PostMapping(value = "/edit")
    public Result<String> edit(@RequestBody SparePartRequisitionAddReqDTO sparePartRequisitionAddReqDTO){
        sparePartRequisitionService.edit(sparePartRequisitionAddReqDTO);
        return Result.ok("编辑成功！");
    }

    /**
     * 三级库管理-提交
     *
     * @param id 领料单id
     * @return Result<String> 返回编辑成功提示
     */
    @AutoLog(value = "三级库管理-三级库申领-提交")
    @ApiOperation(value="三级库管理-三级库申领-提交", notes="三级库管理-三级库申领-提交")
    @PostMapping(value = "/submit")
    public Result<String> submit(@RequestParam(name ="id") String id ){
        sparePartRequisitionService.submit(id);
        return Result.ok("提交成功！");
    }
    /**
     * 生成一个单号
     * @param codePrefix 编码前缀
     * @param snSize 编码顺序号数量，即生成多少位数的顺序号，不能小于1，当已经是该位数的最大值时，只能返回9999这种类似的
     * @return String 返回一个编码
     */
    @AutoLog(value = "三级库管理-生成一个单号")
    @ApiOperation(value="三级库管理-二生成一个code", notes="二级库管理-生成一个单号")
    @GetMapping("/getCode")
    public Result<String> getCode(String codePrefix, Integer snSize){
        String code = CodeGenerateUtils.generateSingleCode(codePrefix, snSize);
        return Result.ok(code);
    }

}
