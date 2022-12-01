package com.aiurt.boot.materials.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.dto.MaterialAccountDTO;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsMapper;
import com.aiurt.boot.materials.service.IEmergencyMaterialsService;
import com.aiurt.boot.materials.entity.EmergencyMaterials;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
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
            if (StrUtil.isNotBlank(e.getUserId())){
                //根据负责人id查询负责人名称
                LoginUser userById = iSysBaseAPI.getUserById(e.getUserId());
                e.setUserName(userById.getRealname());
            }if (StrUtil.isNotBlank(e.getPrimaryOrg())){
                //根据部门编码查询部门名称
                SysDepartModel departByOrgCode = iSysBaseAPI.getDepartByOrgCode(e.getPrimaryOrg());
                e.setPrimaryName(departByOrgCode.getDepartName());
            }if (StrUtil.isNotBlank(e.getLineCode())){
                //根据线路编码查询线路名称
                String position = iSysBaseAPI.getPosition(e.getLineCode());
                e.setLineName(position);
            }if(StrUtil.isNotBlank(e.getStationCode())){
                //根据站点编码查询站点名称
                String position = iSysBaseAPI.getPosition(e.getStationCode());
                e.setStationName(position);
            }if(StrUtil.isNotBlank(e.getPositionCode())){
                //根据位置编码查询位置名称
                String position = iSysBaseAPI.getPosition(e.getPositionCode());
                e.setPositionName(position);
            }
        });
        return pageList.setRecords(materialAccountList);
    }
}
