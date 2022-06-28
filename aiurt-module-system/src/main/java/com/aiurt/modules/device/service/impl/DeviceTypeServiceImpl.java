package com.aiurt.modules.device.service.impl;

import com.aiurt.modules.device.entity.DeviceCompose;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.device.mapper.DeviceComposeMapper;
import com.aiurt.modules.device.mapper.DeviceTypeMapper;
import com.aiurt.modules.device.service.IDeviceTypeService;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.subsystem.entity.CsSubsystemUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: device_type
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Service
public class DeviceTypeServiceImpl extends ServiceImpl<DeviceTypeMapper, DeviceType> implements IDeviceTypeService {
    @Autowired
    @Lazy
    private IDeviceTypeService deviceTypeService;
    @Autowired
    private DeviceTypeMapper deviceTypeMapper;
    @Autowired
    private DeviceComposeMapper deviceComposeMapper;
    /**
     * 添加
     *
     * @param deviceType
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(DeviceType deviceType) {
        //分类编号不能重复
        LambdaQueryWrapper<DeviceType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceType::getCode, deviceType.getCode());
        queryWrapper.eq(DeviceType::getDelFlag, 0);
        List<DeviceType> list = deviceTypeMapper.selectList(queryWrapper);
        if (!list.isEmpty()) {
            return Result.error("分类编码重复，请重新填写！");
        }
        //同一专业下、同一子系统、同一设备类型，分类名称不能重复
        LambdaQueryWrapper<DeviceType> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(DeviceType::getMajorCode, deviceType.getMajorCode());
        nameWrapper.eq(DeviceType::getSystemCode, deviceType.getSystemCode());
        nameWrapper.eq(DeviceType::getPid, deviceType.getPid());
        nameWrapper.eq(DeviceType::getDelFlag, 0);
        List<DeviceType> nameList = deviceTypeMapper.selectList(nameWrapper);
        if (!nameList.isEmpty()) {
            return Result.error("分类名称重复，请重新填写！");
        }
        String typeCodeCc = getCcStr(deviceType);
        deviceType.setCodeCc(typeCodeCc);
        deviceTypeService.save(deviceType);
        //添加设备组成
        if(null!=deviceType.getDeviceComposeList()){
            deviceType.getDeviceComposeList().forEach(compose ->{
                compose.setDeviceTypeCode(deviceType.getCode());
                deviceComposeMapper.insert(compose);
            });
        }
        return Result.OK("添加成功！");
    }
    /**
     * 修改
     *
     * @param deviceType
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(DeviceType deviceType) {
        //删除设备组成
        QueryWrapper<DeviceCompose> composeWrapper = new QueryWrapper<DeviceCompose>();
        composeWrapper.eq("device_type_code", deviceType.getCode());
        deviceComposeMapper.delete(composeWrapper);
        //分类编号不能重复
        LambdaQueryWrapper<DeviceType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceType::getCode, deviceType.getCode());
        queryWrapper.eq(DeviceType::getDelFlag, 0);
        List<DeviceType> list = deviceTypeMapper.selectList(queryWrapper);
        if (!list.isEmpty() && !list.get(0).getId().equals(deviceType.getId())) {
            return Result.error("分类编码重复，请重新填写！");
        }
        //同一专业下、同一子系统、同一设备类型，分类名称不能重复
        LambdaQueryWrapper<DeviceType> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(DeviceType::getMajorCode, deviceType.getMajorCode());
        nameWrapper.eq(DeviceType::getSystemCode, deviceType.getSystemCode());
        nameWrapper.eq(DeviceType::getPid, deviceType.getPid());
        nameWrapper.eq(DeviceType::getDelFlag, 0);
        List<DeviceType> nameList = deviceTypeMapper.selectList(nameWrapper);
        if (!nameList.isEmpty() && !list.get(0).getId().equals(deviceType.getId())) {
            return Result.error("分类名称重复，请重新填写！");
        }
        String typeCodeCc = getCcStr(deviceType);
        deviceType.setCodeCc(typeCodeCc);
        deviceTypeMapper.updateById(deviceType);
        //添加设备组成
        if(null!=deviceType.getDeviceComposeList()){
            deviceType.getDeviceComposeList().forEach(compose ->{
                compose.setDeviceTypeCode(deviceType.getCode());
                deviceComposeMapper.insert(compose);
            });
        }
        return Result.OK("编辑成功！");
    }
    /**
     * DeviceType树
     * @param typeList
     * @param pid
     * @return
     */
    @Override
    public List<DeviceType> treeList(List<DeviceType> typeList, String pid){
        List<DeviceType> childList = typeList.stream().filter(deviceType -> pid.equals(deviceType.getPid())).collect(Collectors.toList());
        if(childList != null && childList.size()>0){
            for (DeviceType deviceType : childList) {
                deviceType.setChildren(treeList(typeList,deviceType.getId().toString()));
            }
        }
        return childList;
    }

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
        DeviceType deviceTypeRes = new DeviceType();
        if("0".equals(deviceType.getPid())){
            str += deviceType.getCode();
        }else{
            str += deviceType.getCode() + "/";
            deviceTypeRes = deviceTypeMapper.selectById(deviceType.getPid());
            str = Ccstr(deviceTypeRes, str);
        }
        return str;
    }


}
