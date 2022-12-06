package com.aiurt.boot.materials.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.dto.MaterialAccountDTO;
import com.aiurt.boot.materials.dto.MaterialPatrolDTO;
import com.aiurt.boot.materials.dto.PatrolStandardDTO;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsMapper;
import com.aiurt.boot.materials.service.IEmergencyMaterialsService;
import com.aiurt.boot.materials.entity.EmergencyMaterials;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
        List<PatrolStandardItemsModel> patrolStandardItemsModels = iSysBaseAPI.patrolStandardList(condition.getPatrolStandardId());
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
            //巡检项
            if (CollUtil.isNotEmpty(patrolStandardItemsModels)){
                e.setPatrolStandardItemsModelList(patrolStandardItemsModels);
            }
        });
        return pageList.setRecords(materialAccountList);
    }

    @Override
    public MaterialPatrolDTO getMaterialPatrol() {
        MaterialPatrolDTO materialPatrolDTO  = new MaterialPatrolDTO();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        materialPatrolDTO.setPatrolName(sysUser.getRealname());

        //根据用户id查询专业
        List<CsUserMajorModel> majorByUserId = iSysBaseAPI.getMajorByUserId(sysUser.getId());
        List<String> collect = majorByUserId.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(collect)){
            List<PatrolStandardDTO> patrolStandardList = emergencyMaterialsMapper.getPatrolStandardList(collect);
            materialPatrolDTO.setPatrolStandardDTOList(CollUtil.isNotEmpty(patrolStandardList) ? patrolStandardList : null);
        }

        //生成4位英文随机字符串
        String random = org.apache.commons.lang3.RandomStringUtils.random(4,true,false);
        //转换为大写
        String string = random.toUpperCase();

        //生成3位数字随机字符串
        String random1 = RandomStringUtils.random(3,false,true);

        //当前时间
        String replace = DateUtil.formatDate(DateUtil.date()).replace("-", "");

        //拼接字符串
        String string1 = String.join("-", string, replace, random1);

        materialPatrolDTO.setMaterialsPatrolCode(string1);
        return materialPatrolDTO;
    }
}
