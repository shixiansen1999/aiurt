package com.aiurt.modules.material.controller;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.material.constant.MaterialRequisitionConstant;
import com.aiurt.modules.material.dto.MaterialRequisitionDetailInfoDTO;
import com.aiurt.modules.material.dto.MaterialRequisitionInfoDTO;
import com.aiurt.modules.material.entity.MaterialRequisition;
import com.aiurt.modules.material.entity.MaterialRequisitionDetail;
import com.aiurt.modules.material.mapper.MaterialRequisitionDetailMapper;
import com.aiurt.modules.material.service.IMaterialRequisitionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : sbx
 * @description :
 * @date : 2023/9/25 15:41
 */

@Slf4j
@Api(tags = "物资申领")
@RestController
@RequestMapping("/materialRequisition")
public class MaterialRequisitionController extends BaseController<MaterialRequisition, IMaterialRequisitionService> {
    @Autowired
    private MaterialRequisitionDetailMapper materialRequisitionDetailMapper;
    @Autowired
    private IMaterialRequisitionService materialRequisitionService;

    /**
     * 根据申领单号查询详情
     * @param code
     * @return
     */
    @AutoLog(value = "根据申领单号查询详情")
    @ApiOperation(value = "根据申领单号查询详情", notes = "根据申领单号查询详情")
    @GetMapping(value = "/queryByCode")
    public Result<MaterialRequisitionInfoDTO> queryByCode(@ApiParam(value = "申领单号", required = true) String code) {
        MaterialRequisitionInfoDTO sparePartRequisitionDTO = materialRequisitionService.queryByCode(code);
        return Result.ok(sparePartRequisitionDTO);
    }

    @AutoLog(value = "根据申领单号查询领料单明细")
    @ApiOperation(value = "根据申领单号查询领料单明细", notes = "根据申领单号查询领料单明细")
    @GetMapping(value = "/queryPageDetail")
    public Result<Page> queryPageDetail(@ApiParam(value = "申领单号", required = true) String code,
                                        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<MaterialRequisitionDetailInfoDTO> page = new Page<>(pageNo, pageSize);
        materialRequisitionService.queryPageDetail(page, code);
        return Result.ok(page);
    }

    /**
     * 根据申领单id查询详情
     * @param id 申领单id
     * @return Result<MaterialRequisitionInfoDTO> 返回申领单详情DTO
     */
    @AutoLog(value = "根据申领单id查询详情")
    @ApiOperation(value = "根据申领单id查询详情", notes = "根据申领单id查询详情")
    @GetMapping(value = "/getDetailById")
    public Result<MaterialRequisitionInfoDTO> getDetailById(@RequestParam(name = "id") String id){
        MaterialRequisitionInfoDTO materialRequisitionInfoDTO = materialRequisitionService.getDetailById(id);
        return Result.ok(materialRequisitionInfoDTO);
    }


    /**
     * 申领单-根据id删除
     * @param id 申领单id
     * @return Result<String> 返回删除成功的提示
     */
    @AutoLog(value = "申领单-根据id删除")
    @ApiOperation(value="申领单-根据id删除", notes="申领单-根据id删除")
    @DeleteMapping("/deleteById")
    public Result<String> deleteById(@RequestParam(name = "id") String id){
        materialRequisitionService.deleteById(id);
        return Result.ok("删除成功！");
    }

    /**
     * 根据故障维修单查询管理
     * @param faultRepairRecordId 故障维修单id
     * @return Result<MaterialRequisitionInfoDTO> 返回申领单详情DTO
     */
    @AutoLog(value = "根据故障维修单查询管理")
    @ApiOperation(value = "根据故障维修单查询管理", notes = "根据故障维修单查询管理")
    @GetMapping(value = "/getMaterialRequisitions")
    public Result<List<MaterialRequisition>> getMaterialRequisitions(@RequestParam(name = "faultRepairRecordId") String faultRepairRecordId){
        LambdaQueryWrapper<MaterialRequisition> wrapper = new LambdaQueryWrapper<>();
        List<MaterialRequisition> materialRequisitions = materialRequisitionService.getBaseMapper().selectList(wrapper.eq(MaterialRequisition::getFaultRepairRecordId, faultRepairRecordId)
                .eq(MaterialRequisition::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(MaterialRequisition::getMaterialRequisitionType, MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_REPAIR));
        if (CollUtil.isNotEmpty(materialRequisitions)) {
            for (MaterialRequisition materialRequisition : materialRequisitions) {
                if (MaterialRequisitionConstant.STATUS_COMPLETED.equals(materialRequisition.getStatus())) {
                    LambdaQueryWrapper<MaterialRequisitionDetail> queryWrapper = new LambdaQueryWrapper<>();
                    List<MaterialRequisitionDetail> materialRequisitionDetails = materialRequisitionDetailMapper.selectList(queryWrapper.eq(MaterialRequisitionDetail::getMaterialRequisitionId, materialRequisition.getId()));
                    materialRequisition.setMaterialRequisitionDetails(materialRequisitionDetails);
                }
            }
        }

        return Result.ok(materialRequisitions);
    }
}
