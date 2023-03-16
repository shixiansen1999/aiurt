package com.aiurt.modules.device.service.impl;


import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.subsystem.entity.CsSubsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * @author zwl
 * @Title:
 * @Description: 线程池处理类
 * @date 2023/3/15:05
 */
public class CsMajorThreadService implements Callable<CsMajor> {

    public static final int LEVEL_2 =2;

    private CsMajor csMajor;

    private List<DeviceType> newList;

    private Integer level;

    private List<CsSubsystem> systemList;

    private  List<DeviceType> deviceTypeTree;

    public CsMajorThreadService(CsMajor csMajor,List<DeviceType> newList,Integer level,List<CsSubsystem> systemList, List<DeviceType> deviceTypeTree){
        this.csMajor = csMajor;
        this.newList = newList;
        this.level = level;
        this.systemList = systemList;
        this.deviceTypeTree = deviceTypeTree;
    }

    @Override
    public CsMajor call() throws Exception {
        DeviceType major = setEntity(csMajor.getId(),"zy",csMajor.getMajorCode(),csMajor.getMajorName(),null,null,null,csMajor.getMajorCode(),null,"-",null);
        major.setTitle(major.getName());
        major.setValue(major.getCode());
        List<CsSubsystem> sysList = systemList.stream().filter(system-> system.getMajorCode().equals(csMajor.getMajorCode())).collect(Collectors.toList());
        List<DeviceType> majorDeviceType = deviceTypeTree.stream().filter(type-> csMajor.getMajorCode().equals(type.getMajorCode()) && (null==type.getSystemCode() || "".equals(type.getSystemCode())) && ("0").equals(type.getPid())).collect(Collectors.toList());
        List<DeviceType> twoList = new ArrayList<>();
        if(level>LEVEL_2) {
            //添加设备类型数据
            twoList.addAll(majorDeviceType);
        }
        //判断是否有子系统数据
        sysList.forEach(two ->{
            DeviceType system = setEntity(two.getId()+"","zxt",two.getSystemCode(),two.getSystemName(),null,null,null,two.getMajorCode(),two.getSystemCode(),csMajor.getMajorName(),null);
            if(level>LEVEL_2) {
                List<DeviceType> sysDeviceType = deviceTypeTree.stream().filter(type -> system.getMajorCode().equals(type.getMajorCode()) && (null != type.getSystemCode() && !"".equals(type.getSystemCode()) && system.getSystemCode().equals(type.getSystemCode()))).collect(Collectors.toList());
                List<DeviceType> collect = sysDeviceType.stream().distinct().collect(Collectors.toList());
                //name赋值给title，code赋值给value
                for (DeviceType deviceType : collect) {
                    deviceType.setTitle(deviceType.getName());
                    deviceType.setValue(deviceType.getCode());
                }
                system.setChildren(collect);
            }
            //name赋值给title，code赋值给value
            system.setValue(system.getCode());
            system.setTitle(system.getName());
            twoList.add(system);
        });
        if(!sysList.isEmpty()){
            major.setPIsHaveSystem(1);
        }else{
            major.setPIsHaveSystem(0);
        }
        major.setChildren(twoList);
        newList.add(major);
        return csMajor;
    }

    /**
     * 设备类型-转换实体
     * @param id
     * @param treeType
     * @param code
     * @param name
     * @param status
     * @param isSpecialDevice
     * @param isEnd
     * @return
     */
    public DeviceType setEntity(String id,String treeType,String code,String name,Integer status,Integer isSpecialDevice,Integer isEnd,String majorCode,String systemCode,String pUrl,Integer pIsSpecialDevice){
        DeviceType type = new DeviceType();
        type.setId(id);
        type.setTreeType(treeType);
        type.setCode(code);
        type.setName(name);
        type.setStatus(status);
        type.setIsSpecialDevice(isSpecialDevice);
        type.setIsEnd(isEnd);
        type.setMajorCode(majorCode);
        type.setSystemCode(systemCode);
        type.setPUrl(pUrl);
        type.setPIsSpecialDevice(pIsSpecialDevice);
        return type;
    }
}
