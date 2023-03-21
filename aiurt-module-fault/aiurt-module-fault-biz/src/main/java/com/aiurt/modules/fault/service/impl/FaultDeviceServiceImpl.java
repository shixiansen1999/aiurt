package com.aiurt.modules.fault.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.fault.dto.FaultDeviceRepairDTO;
import com.aiurt.modules.fault.entity.FaultDevice;
import com.aiurt.modules.fault.mapper.FaultDeviceMapper;
import com.aiurt.modules.fault.service.IFaultDeviceService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Description: fault_device
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
@Service
public class FaultDeviceServiceImpl extends ServiceImpl<FaultDeviceMapper, FaultDevice> implements IFaultDeviceService {
@Autowired
private ISysBaseAPI sysBaseApi;
    @Override
    public List<FaultDevice> queryByFaultCode(String faultCode) {
        List<FaultDevice> faultDeviceList = baseMapper.queryByFaultCode(faultCode);
        faultDeviceList.forEach(faultDevice ->{
            if(ObjectUtil.isNotEmpty(faultDevice.getMaterialCodes())){
                String materialNames = sysBaseApi.getMaterialNameByCodes(faultDevice.getMaterialCodes());
                faultDevice.setMaterialNames(materialNames);
            }
        } );
        return faultDeviceList;
    }

    @Override
    public IPage<FaultDeviceRepairDTO> queryRepairDeviceList(Page<FaultDeviceRepairDTO> page, FaultDeviceRepairDTO FaultDeviceRepairDTO) {
        //只允许送修经办人查看和修改
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String roleCodes = sysUser.getRoleCodes();
        boolean repairAgent = roleCodes.contains("repair_agent");
        if(!repairAgent){
            page.setRecords(new ArrayList<>());
            return page;
        }
        //查询故障单状态为已完成且为委外送修的设备数据
        IPage<FaultDeviceRepairDTO> faultDeviceRepairDtoList = baseMapper.queryRepairDeviceList(page, FaultDeviceRepairDTO);
        List<FaultDeviceRepairDTO> records = faultDeviceRepairDtoList.getRecords();
        for (FaultDeviceRepairDTO record : records) {
            //将查出来的数据设置送修状态为待返修
            FaultDevice faultDevice = new FaultDevice();
            BeanUtils.copyProperties(record,faultDevice);
            String repairStatus = faultDevice.getRepairStatus();
            if(StrUtil.isBlank(repairStatus)){
                faultDevice.setRepairStatus("1");
                baseMapper.updateById(faultDevice);
            }
            //查找接报人所在部门id
            String receiveUserName = record.getReceiveUserName();
            //查找接报人所在部门下工班长的用户集合
            List<String> list = baseMapper.queryUserId(receiveUserName);
            if(CollUtil.isNotEmpty(list)){
                record.setChargeUserName(list);
                //负责人翻译
                StringBuilder userNames = getUserNames(list);
                record.setChargeRealName(userNames.toString());
            }

            //送修经办人
            List<String> repairUserName = baseMapper.queryRepairUserName();
            if(CollUtil.isNotEmpty(repairUserName)){
                record.setRepairUserName(repairUserName);
                StringBuilder userNames = getUserNames(repairUserName);
                record.setRepairRealName(userNames.toString());
            }


            //设备位置数据组装
            //线路
            String lineCode = record.getLineCode()==null?"":record.getLineCode();
            //站点
            String stationCode = record.getStationCode()==null?"":record.getStationCode();
            //位置
            String positionCode = record.getPositionCode()==null?"":record.getPositionCode();
            String lineCodeName = sysBaseApi.translateDictFromTable("cs_line", "line_name", "line_code", lineCode);
            String stationCodeName = sysBaseApi.translateDictFromTable("cs_station", "station_name", "station_code", stationCode);
            String positionCodeName = sysBaseApi.translateDictFromTable("cs_station_position", "position_name", "position_code", positionCode);
            String positionCodeCc = lineCode ;
            if(stationCode!= null && !"".equals(stationCode)){
                positionCodeCc += CommonConstant.SYSTEM_SPLIT_STR + stationCode;
            }

            if (!"".equals(positionCode) && positionCode != null) {
                positionCodeCc += CommonConstant.SYSTEM_SPLIT_STR + positionCode;
            }
            String positionCodeCcName = lineCodeName ;
            if(stationCodeName != null && !"".equals(stationCodeName)){
                positionCodeCcName +=  CommonConstant.SYSTEM_SPLIT_STR + stationCodeName  ;
            }
            if(!"".equals(positionCodeName) && positionCodeName != null){
                positionCodeCcName += CommonConstant.SYSTEM_SPLIT_STR + positionCodeName;
            }
            record.setPositionCodeCcName(positionCodeCcName);
            record.setPositionCodeCc(positionCodeCc);
        }
        page.setRecords(records);
        return page;
    }

    private StringBuilder getUserNames( List<String> list) {
        StringBuilder str = new StringBuilder();
        if(CollUtil.isNotEmpty(list)){
            for (String userName : list) {
                if (StrUtil.isNotBlank(userName)) {
                    LoginUser userById = sysBaseApi.getUserByName(userName);
                    if (!ObjectUtils.isEmpty(userById)) {
                        str.append(userById.getRealname()).append(",");
                    }
                }
            }
        }
        if (StrUtil.isNotBlank(str)) {
            str.deleteCharAt(str.length() - 1).toString();
        }
        return str;
    }


}
