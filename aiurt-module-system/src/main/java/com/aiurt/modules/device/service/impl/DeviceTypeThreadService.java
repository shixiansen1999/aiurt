package com.aiurt.modules.device.service.impl;


import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.device.mapper.DeviceTypeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zwl
 * @Title:
 * @Description: 线程池处理类
 * @date 2023/3/16
 */
public class DeviceTypeThreadService implements Callable<DeviceType> {

    private final String pid;

    private DeviceType deviceType;

    private DeviceTypeMapper deviceTypeMapper;


    public  DeviceTypeThreadService(String pid,DeviceType deviceType,DeviceTypeMapper deviceTypeMapper){
        this.pid = pid;
        this.deviceType = deviceType;
        this.deviceTypeMapper = deviceTypeMapper;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public DeviceType call() throws Exception {
        Lock lock = new ReentrantLock();
        lock.lock();
        try {
            deviceType.setTitle(deviceType.getName());
            deviceType.setValue(deviceType.getCode());
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
        }
        catch (Exception e) {
            throw e;
        } finally {
            lock.unlock();
        }
        return deviceType;
    }
}
