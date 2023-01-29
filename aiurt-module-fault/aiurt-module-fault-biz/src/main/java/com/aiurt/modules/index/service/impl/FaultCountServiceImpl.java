package com.aiurt.modules.index.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultDevice;
import com.aiurt.modules.fault.enums.FaultStatusEnum;
import com.aiurt.modules.fault.service.IFaultDeviceService;
import com.aiurt.modules.index.mapper.FaultCountMapper;
import com.aiurt.modules.index.service.IFaultCountService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 首页故障统计
 *
 * @author: qkx
 * @date: 2022年09月05日 15:54
 */
@Service
public class FaultCountServiceImpl implements IFaultCountService {

   @Autowired
   private FaultCountMapper faultCountMapper;

   @Autowired
   private IFaultDeviceService faultDeviceService;
    @Resource
    private ISysBaseAPI sysBaseApi;

    /**
     * 首页统计故障概况
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public FaultIndexDTO queryFaultCount(Date startDate, Date endDate) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
         String[] split = user.getRoleCodes().split(",");
        List<String> roleCodes = CollUtil.newArrayList(split);
        roleCodes = roleCodes.stream().filter(s->s.equals("director")).collect(Collectors.toList());
        //当前登录人为主任，则根据当前用户所拥有的专业，查询该专业下的故障信息
        boolean isDirector = false;
        if(roleCodes.size()>0)
        {
            isDirector=true;
        }
        List<CsUserMajorModel> majorByUserId = sysBaseApi.getMajorByUserId(user.getId());
        List<String> majors = majorByUserId.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());
        FaultIndexDTO faultIndexDTO = new FaultIndexDTO();
        if (ObjectUtil.isEmpty(startDate) || ObjectUtil.isEmpty(endDate)) {
            return  setDefault();
        }
        if(isDirector&&CollUtil.isEmpty(majors))
        {
            return  setDefault();
        }
        //将符合条件的故障数据查出
        LambdaQueryWrapper<Fault> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(Fault::getApprovalPassTime, DateUtil.beginOfDay(startDate));
        queryWrapper.le(Fault::getApprovalPassTime, DateUtil.beginOfDay(endDate));
        List<CsUserDepartModel> departByUserId = sysBaseApi.getDepartByUserId(user.getId());
        if(CollUtil.isEmpty(departByUserId)&&!isDirector)
        {
            return  setDefault();
        }
        List<String> ordId = departByUserId.stream().map(CsUserDepartModel::getDepartId).collect(Collectors.toList());
        List<Fault> faultList = faultCountMapper.queryFaultCount(startDate,endDate,ordId,majors,isDirector);

        //故障总数
        faultIndexDTO.setSum(CollUtil.isNotEmpty(faultList)?faultList.size():0L);
        //已解决数
        faultIndexDTO.setSolve(CollUtil.isNotEmpty(faultList)?faultList.stream().filter(re -> FaultStatusEnum.Close.getStatus().equals(re.getStatus())).count() : 0L);
        //未解决数
        faultIndexDTO.setUnSolve(CollUtil.isNotEmpty(faultList)?faultList.stream().filter(re -> !FaultStatusEnum.Close.getStatus().equals(re.getStatus())).count() : 0L);
        //挂起数
        faultIndexDTO.setHang(CollUtil.isNotEmpty(faultList)?faultList.stream().filter(re -> FaultStatusEnum.HANGUP.getStatus().equals(re.getStatus())).count() : 0L);

        // 已解决率
        if (faultIndexDTO.getSum() <= 0 || faultIndexDTO.getSolve() <= 0) {
            faultIndexDTO.setSolveRate("0");
        } else {
            double d = new BigDecimal((double) faultIndexDTO.getSolve() * 100 / faultIndexDTO.getSum()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            faultIndexDTO.setSolveRate(d + "%");
        }


        //故障等级数量统计
        if(faultList!=null &&! faultList.isEmpty()){
            int number1 = 0;
            int number2 = 0;
            int number3 = 0;
            for (Fault fault : faultList) {
                //计算故障发生时间到当前时间时间差
                long result=DateUtil.between(fault.getHappenTime(),new Date(), DateUnit.HOUR);

                //三级故障超时(12-24小时)
                if(result>=12 && result<24 & !FaultStatusEnum.Close.getStatus().equals(fault.getStatus())){
                    number1++;
                    faultIndexDTO.setLevelThreeNumber(number1);
                }
                //二级故障超时(24-48小时)
                else if(result>=24 && result<48 & !FaultStatusEnum.Close.getStatus().equals(fault.getStatus())){
                    number2++;
                    faultIndexDTO.setLevelTwoNumber(number2);
                }
                //一级故障超时(大于48小时)
                else if(result>=48 && !FaultStatusEnum.Close.getStatus().equals(fault.getStatus())){
                    number3++;
                    faultIndexDTO.setLevelOneNumber(number3);
                }

            }

        }
        return faultIndexDTO;
    }

    public FaultIndexDTO setDefault()
{
    FaultIndexDTO faultIndexDTO = new FaultIndexDTO();
    faultIndexDTO.setHang(0L);
    faultIndexDTO.setSum(0L);
    faultIndexDTO.setUnSolve(0L);
    faultIndexDTO.setSolve(0L);
    faultIndexDTO.setLevelOneNumber(0);
    faultIndexDTO.setLevelTwoNumber(0);
    faultIndexDTO.setLevelThreeNumber(0);
    faultIndexDTO.setSolveRate("0%");
    return  faultIndexDTO;
}
    /**
     * 首页-故障概况详情(总数和已解决)
     * @param faultCountInfoReq
     * @return
     */
    @Override
    public IPage<FaultCountInfoDTO> getFaultCountInfo(FaultCountInfoReq faultCountInfoReq) {
        IPage<FaultCountInfoDTO> result = new Page<>();
        if (ObjectUtil.isEmpty(faultCountInfoReq.getType())
                || ObjectUtil.isEmpty(faultCountInfoReq)
                || ObjectUtil.isEmpty(faultCountInfoReq.getStartDate())
                || ObjectUtil.isEmpty(faultCountInfoReq.getEndDate())) {
            return result;
        }
        // 分页数据
        Page<FaultCountInfoDTO> page = new Page<>(faultCountInfoReq.getPageNo(), faultCountInfoReq.getPageSize());
        //权限控制
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String[] split = user.getRoleCodes().split(",");
        List<String> roleCodes = CollUtil.newArrayList(split);
        roleCodes = roleCodes.stream().filter(s->s.equals("director")).collect(Collectors.toList());
        //当前登录人为主任，则根据当前用户所拥有的专业，查询该专业下的故障信息
        boolean isDirector = false;
        if(roleCodes.size()>0)
        {
            isDirector=true;
        }
        List<CsUserMajorModel> majorByUserId = sysBaseApi.getMajorByUserId(user.getId());
        List<String> majors = majorByUserId.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());
        List<CsUserDepartModel> departByUserId = sysBaseApi.getDepartByUserId(user.getId());
        if(CollUtil.isEmpty(departByUserId)&&!isDirector)
        {
            return result;
        }
        if(!isDirector&&CollUtil.isEmpty(majors))
        {
            return result;
        }
        List<String> ordId = departByUserId.stream().map(CsUserDepartModel::getDepartId).collect(Collectors.toList());
        List<FaultCountInfoDTO> faultData = faultCountMapper.getFaultCountInfo(faultCountInfoReq.getType(), page, faultCountInfoReq,ordId,majors,isDirector);
        if (CollUtil.isNotEmpty(faultData)) {
            for (FaultCountInfoDTO faultDatum : faultData) {
                //查找设备编码
                List<FaultDevice> faultDeviceList = faultDeviceService.queryByFaultCode(faultDatum.getCode());
                if(CollUtil.isNotEmpty(faultDeviceList)){
                    for (FaultDevice faultDevice : faultDeviceList) {
                        faultDatum.setDeviceCode(faultDevice.getDeviceCode());
                        faultDatum.setDeviceName(faultDevice.getDeviceName());
                    }
                }
            }
        }
        page.setRecords(faultData);
        return page;
    }

    /**
     * 首页-故障概况详情(未解决和挂起数)
     * @param faultCountInfoReq
     * @return
     */
    @Override
    public IPage<FaultCountInfosDTO> getFaultCountInfos(FaultCountInfoReq faultCountInfoReq) {
        IPage<FaultCountInfosDTO> result = new Page<>();
        if (ObjectUtil.isEmpty(faultCountInfoReq.getType())
                || ObjectUtil.isEmpty(faultCountInfoReq)
                || ObjectUtil.isEmpty(faultCountInfoReq.getStartDate())
                || ObjectUtil.isEmpty(faultCountInfoReq.getEndDate())) {
            return result;
        }
        // 分页数据
        Page<FaultCountInfosDTO> page = new Page<>(faultCountInfoReq.getPageNo(), faultCountInfoReq.getPageSize());
        //权限控制
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String[] split = user.getRoleCodes().split(",");
        List<String> roleCodes = CollUtil.newArrayList(split);
        roleCodes = roleCodes.stream().filter(s->s.equals("director")).collect(Collectors.toList());
        //当前登录人为主任，则根据当前用户所拥有的专业，查询该专业下的故障信息
        boolean isDirector = false;
        if(roleCodes.size()>0)
        {
            isDirector=true;
        }
        List<CsUserMajorModel> majorByUserId = sysBaseApi.getMajorByUserId(user.getId());
        List<String> majors = majorByUserId.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());
        List<CsUserDepartModel> departByUserId = sysBaseApi.getDepartByUserId(user.getId());
        if(CollUtil.isEmpty(departByUserId)&&!isDirector)
        {
            return result;
        }
        if(isDirector&&CollUtil.isNotEmpty(majors))
        {
            return  result;
        }
        List<String> ordId = departByUserId.stream().map(CsUserDepartModel::getDepartId).collect(Collectors.toList());
        List<FaultCountInfosDTO> faultData = faultCountMapper.getFaultCountInfos(faultCountInfoReq.getType(), page, faultCountInfoReq,ordId,majors,isDirector);
        if (CollUtil.isNotEmpty(faultData)) {
            for (FaultCountInfosDTO faultDatum : faultData) {
                //查找设备编码
                List<FaultDevice> faultDeviceList = faultDeviceService.queryByFaultCode(faultDatum.getCode());
                if(CollUtil.isNotEmpty(faultDeviceList)){
                    for (FaultDevice faultDevice : faultDeviceList) {
                        faultDatum.setDeviceCode(faultDevice.getDeviceCode());
                        faultDatum.setDeviceName(faultDevice.getDeviceName());
                    }
                }
            }
        }
        page.setRecords(faultData);
        return page;
    }

    /**
     *分页查询故障超时等级
     * @param faultTimeoutLevelReq 查询条件
     * @return
     */
    @Override
    public IPage<FaultTimeoutLevelDTO> getFaultLevelInfo(FaultTimeoutLevelReq faultTimeoutLevelReq) {
        IPage<FaultTimeoutLevelDTO> result = new Page<>();
        if (ObjectUtil.isEmpty(faultTimeoutLevelReq.getLevel())
                || ObjectUtil.isEmpty(faultTimeoutLevelReq)
                || ObjectUtil.isEmpty(faultTimeoutLevelReq.getStartDate())
                || ObjectUtil.isEmpty(faultTimeoutLevelReq.getEndDate())) {
            return result;
        }
        // 分页数据
        Page<FaultTimeoutLevelDTO> page = new Page<>(faultTimeoutLevelReq.getPageNo(), faultTimeoutLevelReq.getPageSize());
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String[] split = user.getRoleCodes().split(",");
        List<String> roleCodes = CollUtil.newArrayList(split);
        roleCodes = roleCodes.stream().filter(s->s.equals("director")).collect(Collectors.toList());
        //当前登录人为主任，则根据当前用户所拥有的专业，查询该专业下的故障信息
        boolean isDirector = false;
        if(roleCodes.size()>0)
        {
            isDirector=true;
        }
        List<CsUserMajorModel> majorByUserId = sysBaseApi.getMajorByUserId(user.getId());
        List<String> majors = majorByUserId.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());
        List<CsUserDepartModel> departByUserId = sysBaseApi.getDepartByUserId(user.getId());
        if(CollUtil.isEmpty(departByUserId)&&!isDirector)
        {
            return result;
        }
        if(isDirector&&CollUtil.isNotEmpty(majors))
        {
            return  result;
        }
         List<String> ordCode = departByUserId.stream().map(CsUserDepartModel::getDepartId).collect(Collectors.toList());
        faultTimeoutLevelReq.setOrgList(ordCode);
        List<FaultTimeoutLevelDTO> faultData = faultCountMapper.getFaultData(faultTimeoutLevelReq.getLevel(), page, faultTimeoutLevelReq,majors,isDirector);
        if (CollUtil.isNotEmpty(faultData)) {
            for (FaultTimeoutLevelDTO faultDatum : faultData) {
                //查找设备编码
                List<FaultDevice> faultDeviceList = faultDeviceService.queryByFaultCode(faultDatum.getCode());
                if(CollUtil.isNotEmpty(faultDeviceList)){
                    for (FaultDevice faultDevice : faultDeviceList) {
                        faultDatum.setDeviceCode(faultDevice.getDeviceCode());
                        faultDatum.setDeviceName(faultDevice.getDeviceName());
                    }
                }
                //计算超时时长
                long hour=DateUtil.between(faultDatum.getHappenTime(),new Date(), DateUnit.HOUR);
                long min=DateUtil.between(faultDatum.getHappenTime(),new Date(), DateUnit.MINUTE);
                int m = ((new Double(min % 60))).intValue();
                String time = hour + "h" + m + "min";
                long appHour =hour;
                if(m>0)
                {
                    appHour=appHour+1;
                }
                String appTime = appHour + "h";
                if (faultTimeoutLevelReq.getLevel() == 1) {
                    faultDatum.setTimeoutDuration(time);
                    faultDatum.setAppTimeoutDuration(appTime);
                    if (hour >= 48 && !FaultStatusEnum.Close.getStatus().equals(faultDatum.getStatus())) {
                        faultDatum.setTimeoutType("一级超时");
                    }
                } else if (faultTimeoutLevelReq.getLevel() == 2) {
                    faultDatum.setTimeoutDuration(time);
                    faultDatum.setAppTimeoutDuration(appTime);
                    if (hour >= 24 && hour < 48 & !FaultStatusEnum.Close.getStatus().equals(faultDatum.getStatus())) {
                        faultDatum.setTimeoutType("二级超时");
                    }
                } else if (faultTimeoutLevelReq.getLevel() == 3) {
                    faultDatum.setTimeoutDuration(time);
                    faultDatum.setAppTimeoutDuration(appTime);
                    if (hour >= 12 && hour < 24 & !FaultStatusEnum.Close.getStatus().equals(faultDatum.getStatus())) {
                        faultDatum.setTimeoutType("三级超时");
                    }
                }

            }
        }
        page.setRecords(faultData);
        return page;
    }



    /**
     *分页查询待办事项故障情况
     * @param page
     * @param startDate
     * @return
     */
    @Override
    public IPage<FaultTimeoutLevelDTO> getMainFaultCondition(Page<FaultTimeoutLevelDTO> page, Date startDate){
        List<FaultTimeoutLevelDTO> mainFaultCondition = faultCountMapper.getMainFaultCondition(page, startDate);
        return page.setRecords(mainFaultCondition);
    }

}
