package com.aiurt.boot.plan.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.dto.MaterialAccountDTO;
import com.aiurt.boot.materials.entity.EmergencyMaterials;
import com.aiurt.boot.materials.entity.EmergencyMaterialsCategory;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsMapper;
import com.aiurt.boot.materials.service.IEmergencyMaterialsCategoryService;
import com.aiurt.boot.materials.service.IEmergencyMaterialsService;
import com.aiurt.boot.plan.constant.EmergencyPlanConstant;
import com.aiurt.boot.plan.dto.EmergencyPlanMaterialsDTO;
import com.aiurt.boot.plan.entity.EmergencyPlanMaterials;
import com.aiurt.boot.plan.mapper.EmergencyPlanMaterialsMapper;
import com.aiurt.boot.plan.service.IEmergencyPlanMaterialsService;
import com.aiurt.boot.plan.vo.EmergencyPlanMaterialsExportExcelVO;
import com.aiurt.boot.team.constants.TeamConstant;
import com.aiurt.boot.team.entity.EmergencyCrew;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: emergency_plan_materials
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyPlanMaterialsServiceImpl extends ServiceImpl<EmergencyPlanMaterialsMapper, EmergencyPlanMaterials> implements IEmergencyPlanMaterialsService {
    @Autowired
    private EmergencyPlanMaterialsMapper emergencyPlanMaterialsMapper;
    @Autowired
    private IEmergencyMaterialsService emergencyMaterialsService;
    @Autowired
    private IEmergencyMaterialsCategoryService emergencyMaterialsCategoryService;

    @Override
    public Page<EmergencyPlanMaterialsDTO> getMaterialAccountList(Page<EmergencyPlanMaterialsDTO> pageList, EmergencyPlanMaterialsDTO condition) {
        List<EmergencyPlanMaterialsDTO> planMaterialsList = emergencyPlanMaterialsMapper.getMaterialAccountList(pageList, condition);
        return pageList.setRecords(planMaterialsList);
    }

    @Override
    public List<EmergencyPlanMaterialsDTO> queryById(String id) {
        List<EmergencyPlanMaterialsDTO> planMaterialsList = new ArrayList<>();
        //根据应急预案id查询物资信息
        LambdaQueryWrapper<EmergencyPlanMaterials> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmergencyPlanMaterials::getDelFlag, EmergencyPlanConstant.DEL_FLAG0);
        wrapper.eq(EmergencyPlanMaterials::getEmergencyPlanId, id);
        List<EmergencyPlanMaterials> planMaterials = emergencyPlanMaterialsMapper.selectList(wrapper);

        if(CollUtil.isNotEmpty(planMaterials)){
            for (EmergencyPlanMaterials planMaterial : planMaterials) {
                EmergencyPlanMaterialsDTO emergencyPlanMaterialsDto = new EmergencyPlanMaterialsDTO();
                String materialsCode = planMaterial.getMaterialsCode();
                String MaterialId = planMaterial.getId();
                Integer materialsNumber = planMaterial.getMaterialsNumber();
                emergencyPlanMaterialsDto.setId(MaterialId);
                emergencyPlanMaterialsDto.setMaterialsCode(materialsCode);
                emergencyPlanMaterialsDto.setMaterialsNumber(materialsNumber);
                //根据物资code查询物资信息
                List<EmergencyMaterials> materialsList = emergencyMaterialsService.lambdaQuery().eq(EmergencyMaterials::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                        .eq(EmergencyMaterials::getMaterialsCode, materialsCode).list();
                if(CollUtil.isNotEmpty(materialsList)){
                    for (EmergencyMaterials emergencyMaterials : materialsList) {
                        String categoryCode = emergencyMaterials.getCategoryCode();
                        //查询物资分类信息
                        List<EmergencyMaterialsCategory> list = emergencyMaterialsCategoryService.lambdaQuery()
                                .eq(EmergencyMaterialsCategory::getCategoryCode, categoryCode)
                                .eq(EmergencyMaterialsCategory::getDelFlag,EmergencyPlanConstant.DEL_FLAG0)
                                .list();
                        for (EmergencyMaterialsCategory emergencyMaterialsCategory : list) {
                            String categoryName = emergencyMaterialsCategory.getCategoryName();
                            emergencyPlanMaterialsDto.setCategoryName(categoryName);
                        }
                        String materialsName = emergencyMaterials.getMaterialsName();
                        String unit = emergencyMaterials.getUnit();
                        emergencyPlanMaterialsDto.setMaterialsName(materialsName);
                        emergencyPlanMaterialsDto.setUnit(unit);
                    }
                    planMaterialsList.add(emergencyPlanMaterialsDto);
                }
            }

        }
        return planMaterialsList;
    }
}
