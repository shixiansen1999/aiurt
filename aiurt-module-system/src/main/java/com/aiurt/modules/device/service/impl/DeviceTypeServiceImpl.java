package com.aiurt.modules.device.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.device.entity.DeviceCompose;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.device.mapper.DeviceComposeMapper;
import com.aiurt.modules.device.mapper.DeviceTypeMapper;
import com.aiurt.modules.device.service.IDeviceTypeService;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.mapper.CsMajorMapper;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.mapper.CsSubsystemMapper;
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
    @Autowired
    private CsSubsystemMapper subsystemMapper;
    @Autowired
    private CsMajorMapper majorMapper;



    /**
     * 列表
     * @return
     */
    public List<DeviceType> selectList(){
        return deviceTypeMapper.readAll();
    }
    /**
     * 添加
     *
     * @param deviceType
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(DeviceType deviceType) {
        if(null == deviceType.getPid()){
            deviceType.setPid("0");
        }
        //分类编号不能重复
        LambdaQueryWrapper<DeviceType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceType::getCode, deviceType.getCode());
        queryWrapper.eq(DeviceType::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<DeviceType> list = deviceTypeMapper.selectList(queryWrapper);
        if (!list.isEmpty()) {
            return Result.error("分类编码重复，请重新填写！");
        }
        //同一专业下、同一子系统、同一设备类型，分类名称不能重复
        LambdaQueryWrapper<DeviceType> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(DeviceType::getMajorCode, deviceType.getMajorCode());
        nameWrapper.eq(DeviceType::getSystemCode, deviceType.getSystemCode());
        nameWrapper.eq(DeviceType::getName, deviceType.getName());
        nameWrapper.eq(DeviceType::getPid, deviceType.getPid());
        nameWrapper.eq(DeviceType::getDelFlag, CommonConstant.DEL_FLAG_0);
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
        if(null == deviceType.getPid()){
            deviceType.setPid("0");
        }
        //删除设备组成
        QueryWrapper<DeviceCompose> composeWrapper = new QueryWrapper<DeviceCompose>();
        composeWrapper.eq("device_type_code", deviceType.getCode());
        deviceComposeMapper.delete(composeWrapper);
        //分类编号不能重复
        LambdaQueryWrapper<DeviceType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceType::getCode, deviceType.getCode());
        queryWrapper.eq(DeviceType::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<DeviceType> list = deviceTypeMapper.selectList(queryWrapper);
        if (!list.isEmpty() && !list.get(0).getId().equals(deviceType.getId())) {
            return Result.error("分类编码重复，请重新填写！");
        }
        //同一专业下、同一子系统、同一设备类型，分类名称不能重复
        LambdaQueryWrapper<DeviceType> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(DeviceType::getMajorCode, deviceType.getMajorCode());
        nameWrapper.eq(DeviceType::getSystemCode, deviceType.getSystemCode());
        nameWrapper.eq(DeviceType::getName, deviceType.getName());
        nameWrapper.eq(DeviceType::getPid, deviceType.getPid());
        nameWrapper.eq(DeviceType::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<DeviceType> nameList = deviceTypeMapper.selectList(nameWrapper);
        if (!nameList.isEmpty() && !nameList.get(0).getId().equals(deviceType.getId())) {
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
                deviceType.setTreeType("sblx");
                String pUrl = "";
                Integer pIsSpecialDevice = null;
                if(pid.equals("0")){
                    //如果systemCode不是null，查询systemCode的名称
                    if(null!=deviceType.getSystemCode()){
                        pUrl = deviceType.getSystemName();
                    }
                    //如果systemCode是null，查询majorCode的名称
                    if(null==deviceType.getSystemCode() && null!= deviceType.getMajorCode()){
                        pUrl = deviceType.getMajorName();
                    }
                }else{
                    //如果pid不是0，查询设备类型名称
                    LambdaQueryWrapper<DeviceType> wrapper = new LambdaQueryWrapper<>();
                    DeviceType type = deviceTypeMapper.selectOne(wrapper.eq(DeviceType::getId,pid));
                    pUrl = type.getName();
                    pIsSpecialDevice = type.getIsSpecialDevice();
                }
                deviceType.setPUrl(pUrl);
                deviceType.setPIsSpecialDevice(pIsSpecialDevice);
                deviceType.setChildren(treeList(typeList,deviceType.getId().toString()));
            }
        }
        return childList;
    }
    /**
     * 拼接cc字段
     * @param deviceType
     * @return
     */
    @Override
    public String getCcStr(DeviceType deviceType) {
        String res = "";
        String str = Ccstr(deviceType, "");
        if( !"" .equals(str) ){
            if(str.contains(CommonConstant.SYSTEM_SPLIT_STR)){
                List<String> strings = Arrays.asList(str.split(CommonConstant.SYSTEM_SPLIT_STR));
                Collections.reverse(strings);
                for(String s : strings){
                    res += s + CommonConstant.SYSTEM_SPLIT_STR;
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
