package com.aiurt.modules.fault.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.fault.dto.FaultDeviceRepairDTO;
import com.aiurt.modules.fault.entity.FaultDevice;
import com.aiurt.modules.fault.mapper.FaultDeviceMapper;
import com.aiurt.modules.fault.service.IFaultDeviceService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

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
        return faultDeviceList;
    }

    @Override
    public IPage<FaultDeviceRepairDTO> queryRepairDeviceList(Page<FaultDeviceRepairDTO> page, FaultDeviceRepairDTO FaultDeviceRepairDTO) {
//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        String roleCodes = sysUser.getRoleCodes();
//        boolean repairAgent = roleCodes.contains("repair_agent");
//        if(!repairAgent){
//            page.setRecords(new ArrayList<>());
//            return page;
//        }
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
            List<String> departId = baseMapper.queryDepartId(receiveUserName);
            //部门下工班长的用户集合
            List<String> list = baseMapper.queryUserId(departId);
            if(CollUtil.isNotEmpty(list)){
                record.setChargeUserName(list);
            }
            //送修经办人
            String repairUserName = baseMapper.queryRepairUserName();
            record.setRepairUserName(repairUserName);
            getUserNames(record);
        }
        page.setRecords(records);
        return page;
    }

    private void getUserNames(FaultDeviceRepairDTO faultDeviceRepairDTO) {
        List<String> list = faultDeviceRepairDTO.getChargeUserName();
        StringBuilder str = new StringBuilder();
        for (String userName : list) {
            if (StrUtil.isNotBlank(userName)) {
                            LoginUser userById = sysBaseApi.getUserByName(userName);
                            if (!ObjectUtils.isEmpty(userById)) {
                                str.append(userById.getRealname()).append(",");
                            }
            }
        }
        if (StrUtil.isNotBlank(str)) {
            faultDeviceRepairDTO.setChargeRealName(Collections.singletonList(str.deleteCharAt(str.length() - 1).toString()));
        }
    }


}
