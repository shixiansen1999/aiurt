package com.aiurt.boot.materials.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.entity.EmergencyMaterialsInvoices;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsInvoicesItemMapper;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsInvoicesMapper;
import com.aiurt.boot.materials.service.IEmergencyMaterialsInvoicesItemService;
import com.aiurt.boot.materials.entity.EmergencyMaterialsInvoicesItem;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: emergency_materials_invoices_item
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyMaterialsInvoicesItemServiceImpl extends ServiceImpl<EmergencyMaterialsInvoicesItemMapper, EmergencyMaterialsInvoicesItem> implements IEmergencyMaterialsInvoicesItemService {
    @Autowired
    private EmergencyMaterialsInvoicesItemMapper emergencyMaterialsInvoicesItemMapper;

    @Autowired
    private EmergencyMaterialsInvoicesMapper emergencyMaterialsInvoicesMapper;

    @Autowired
    private ISysBaseAPI iSysBaseAPI;


    @Override
    public Page<EmergencyMaterialsInvoicesItem> getPatrolRecord(Page<EmergencyMaterialsInvoicesItem> pageList, String materialsCode, String startTime, String endTime,String  standardCode,String lineCode,String stationCode,String positionCode) {
        //父级
        EmergencyMaterialsInvoices emergencyMaterialsInvoices = new EmergencyMaterialsInvoices();
        LambdaQueryWrapper<EmergencyMaterialsInvoicesItem> emergencyMaterialsInvoicesItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
        emergencyMaterialsInvoicesItemLambdaQueryWrapper.eq(EmergencyMaterialsInvoicesItem::getDelFlag,0);
        if(StrUtil.isNotBlank(materialsCode)){
            emergencyMaterialsInvoicesItemLambdaQueryWrapper.eq(EmergencyMaterialsInvoicesItem::getMaterialsCode,materialsCode);
        }
        List<EmergencyMaterialsInvoicesItem> emergencyMaterialsInvoicesItems = emergencyMaterialsInvoicesItemMapper.selectList(emergencyMaterialsInvoicesItemLambdaQueryWrapper);
        if (CollectionUtil.isNotEmpty(emergencyMaterialsInvoicesItems)){
            List<String> collect = emergencyMaterialsInvoicesItems.stream().map(EmergencyMaterialsInvoicesItem::getInvoicesId).collect(Collectors.toList());
            LambdaQueryWrapper<EmergencyMaterialsInvoices> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            if (CollectionUtil.isNotEmpty(collect)){
                lambdaQueryWrapper.eq(EmergencyMaterialsInvoices::getDelFlag,0);
                lambdaQueryWrapper.orderByDesc(EmergencyMaterialsInvoices::getCreateTime);
                 if (StrUtil.isNotBlank(lineCode)){
                    lambdaQueryWrapper.eq(EmergencyMaterialsInvoices::getLineCode,lineCode);
                 }
                 if (StrUtil.isNotBlank(stationCode)){
                    lambdaQueryWrapper.eq(EmergencyMaterialsInvoices::getStationCode,stationCode);
                 }
                 if (StrUtil.isNotBlank(positionCode)){
                   lambdaQueryWrapper.eq(EmergencyMaterialsInvoices::getPositionCode,positionCode);
                 }
                lambdaQueryWrapper.in(EmergencyMaterialsInvoices::getId, collect);
                lambdaQueryWrapper.last("limit 1");
            }
            emergencyMaterialsInvoices = emergencyMaterialsInvoicesMapper.selectOne(lambdaQueryWrapper);
        }
        List<EmergencyMaterialsInvoicesItem> patrolRecord = emergencyMaterialsInvoicesItemMapper.getPatrolRecord(pageList,
                                                 materialsCode,
                                                 startTime,
                                                 endTime,
                                                 StrUtil.isBlank(standardCode)&& ObjectUtil.isNotEmpty(emergencyMaterialsInvoices) ? emergencyMaterialsInvoices.getStandardCode() : standardCode,
                                                  "0",
                                                 lineCode,
                                                 stationCode,
                                                 positionCode);
        patrolRecord.forEach(e->{
            if (StrUtil.isNotBlank(e.getPatrolId())) {
                //根据巡视人id查询巡视人名称
                String[] split = e.getPatrolId().split(",");
                List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(split);
                if (CollUtil.isNotEmpty(loginUsers)){
                    String collect = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
                    e.setPatrolName(collect);
                }
            }if(StrUtil.isNotBlank(e.getPatrolTeamCode())){
                //根据巡检班组code查询巡检班组名称
                String departNameByOrgCode = iSysBaseAPI.getDepartNameByOrgCode(e.getPatrolTeamCode());
                e.setPatrolTeamName(departNameByOrgCode);

                //子级
            }if("0".equals(e.getPid()) && StrUtil.isNotBlank(e.getId())){
                List<EmergencyMaterialsInvoicesItem> patrolRecord1 = emergencyMaterialsInvoicesItemMapper.getPatrolRecord(pageList, materialsCode, startTime, endTime, standardCode, e.getId(),lineCode,stationCode,positionCode);
                e.setSubLevel(patrolRecord1);
            }
        });
        return pageList.setRecords(patrolRecord);
    }
}
