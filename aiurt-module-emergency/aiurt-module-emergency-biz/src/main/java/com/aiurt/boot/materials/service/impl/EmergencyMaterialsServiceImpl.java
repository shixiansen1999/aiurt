package com.aiurt.boot.materials.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.dto.MaterialAccountDTO;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsMapper;
import com.aiurt.boot.materials.service.IEmergencyMaterialsService;
import com.aiurt.boot.materials.entity.EmergencyMaterials;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: emergency_materials
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyMaterialsServiceImpl extends ServiceImpl<EmergencyMaterialsMapper, EmergencyMaterials> implements IEmergencyMaterialsService {

    @Autowired
    private EmergencyMaterialsMapper emergencyMaterialsMapper;

    @Autowired
    private ISysBaseAPI iSysBaseAPI;

    @Override
    public Page<MaterialAccountDTO> getMaterialAccountList(Page<MaterialAccountDTO> pageList, MaterialAccountDTO condition) {
        List<MaterialAccountDTO> materialAccountList = emergencyMaterialsMapper.getMaterialAccountList(pageList, condition);
        materialAccountList.forEach(e->{
            if (StrUtil.isNotBlank(e.getLocation())){

            }

        });
        return pageList.setRecords(materialAccountList);
    }
}
