package com.aiurt.modules.device.service.impl;

import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.device.mapper.DeviceTypeMapper;
import com.aiurt.modules.device.service.IDeviceTypeService;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.mapper.MaterialBaseTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @Description: device_type
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Service
public class DeviceTypeServiceImpl extends ServiceImpl<DeviceTypeMapper, DeviceType> implements IDeviceTypeService {
    @Autowired
    private DeviceTypeMapper deviceTypeMapper;
    @Override
    public String getCcStr(DeviceType deviceType) {
        String res = "";
        String str = Ccstr(deviceType, "");
        if( !"" .equals(str) ){
            if(str.contains("/")){
                List<String> strings = Arrays.asList(str.split("/"));
                Collections.reverse(strings);
                for(String s : strings){
                    res += s + "/";
                }
                res = res.substring(0,res.length()-1);
            }else{
                res = str;
            }
        }
        return res;
    }

    String Ccstr(DeviceType deviceType, String str){
        DeviceType deviceType1 = new DeviceType();
        if("0".equals(deviceType.getPid())){
            str += deviceType.getCode();
        }else{
            str += deviceType.getCode() + "/";
            deviceType1 = deviceTypeMapper.selectById(deviceType.getPid());
            str = Ccstr(deviceType1, str);
        }
        return str;
    }
}
