package com.aiurt.modules.index.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultDevice;
import com.aiurt.modules.fault.enums.FaultStatusEnum;
import com.aiurt.modules.fault.service.IFaultDeviceService;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.index.mapper.FaultCountMapper;
import com.aiurt.modules.index.service.IFaultCountService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsWorkAreaModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
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

    @Autowired
    private IFaultService faultService;

    @Value("${fault.lv1}")
    private Integer lv1Hours;

    @Value("${fault.lv2}")
    private Integer lv2Hours;

    @Value("${fault.lv3}")
    private Integer lv3Hours;

    /**
     * 首页统计故障概况
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public FaultIndexDTO queryFaultCount(Date startDate, Date endDate) {
        if (ObjectUtil.isEmpty(startDate) || ObjectUtil.isEmpty(endDate)) {
            return  setDefault();
        }
        boolean b = GlobalThreadLocal.setDataFilter(false);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        FaultIndexDTO faultIndexDTO = new FaultIndexDTO();
//        List<String> ordCode = null;
        List<String> majors = null;
        List<String> stationCodeList = null;

        GlobalThreadLocal.setDataFilter(b);


        //将符合条件的故障数据查出
        LambdaQueryWrapper<Fault> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(Fault::getApprovalPassTime, DateUtil.beginOfDay(startDate));
        queryWrapper.le(Fault::getApprovalPassTime, DateUtil.endOfDay(endDate));

//        if (CollUtil.isNotEmpty(ordCode)) {
//            queryWrapper.in(Fault::getSysOrgCode, ordCode);
//        }

        if (CollUtil.isNotEmpty(majors)) {
            queryWrapper.in(Fault::getMajorCode, majors);
        }

        if (CollUtil.isNotEmpty(stationCodeList)) {
            queryWrapper.in(Fault::getStationCode, stationCodeList);
        }

        List<Fault> faultList = faultService.list(queryWrapper);

        boolean b1 = GlobalThreadLocal.setDataFilter(false);
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

                //三级故障超时(24-48小时)
                if(result>=lv3Hours && result<lv2Hours & !FaultStatusEnum.Close.getStatus().equals(fault.getStatus())){
                    number1++;
                    faultIndexDTO.setLevelThreeNumber(number1);
                }
                //二级故障超时(48-72小时)
                else if(result>=lv2Hours && result<lv1Hours & !FaultStatusEnum.Close.getStatus().equals(fault.getStatus())){
                    number2++;
                    faultIndexDTO.setLevelTwoNumber(number2);
                }
                //一级故障超时(大于72小时)
                else if(result>=lv1Hours && !FaultStatusEnum.Close.getStatus().equals(fault.getStatus())){
                    number3++;
                    faultIndexDTO.setLevelOneNumber(number3);
                }

            }

        }
        GlobalThreadLocal.setDataFilter(b1);
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
        boolean b = GlobalThreadLocal.setDataFilter(false);

        List<String> userNameByRealName = sysBaseApi.getUserNameByRealName(faultCountInfoReq.getAppointUserName());
        GlobalThreadLocal.setDataFilter(b);
        List<FaultCountInfoDTO> faultData = faultCountMapper.getFaultCountInfo(faultCountInfoReq.getType(), page, faultCountInfoReq, null, null,userNameByRealName);
        if (CollUtil.isNotEmpty(faultData)) {

            Set<String> faultCodeSet = faultData.stream().map(FaultCountInfoDTO::getCode).collect(Collectors.toSet());
            Map<String, Fault> faultMap = new HashMap<>(16);
            Map<String, List<FaultDevice>> faultDeviceMap = new HashMap<>(16);
            if (CollUtil.isNotEmpty(faultCodeSet)) {
                List<Fault> faultList = faultService.list(new LambdaQueryWrapper<Fault>().in(Fault::getCode, faultCodeSet));

                faultMap = faultList.stream().collect(Collectors.toMap(Fault::getCode, Function.identity()));


                List<FaultDevice> faultDeviceList = faultDeviceService.queryListByFaultCodeList(new ArrayList<>(faultCodeSet));
                faultDeviceMap = faultDeviceList.stream().collect(Collectors.groupingBy(FaultDevice::getFaultCode));
            }

            for (FaultCountInfoDTO faultDatum : faultData) {
                List<FaultDevice> faultDeviceList = faultDeviceMap.get(faultDatum.getCode());

                if (CollUtil.isNotEmpty(faultDeviceList)) {
                    faultDatum.setDeviceCode(faultDeviceList.stream().map(FaultDevice::getDeviceCode).collect(Collectors.joining(",")));
                    faultDatum.setDeviceName(faultDeviceList.stream().map(FaultDevice::getDeviceName).collect(Collectors.joining(",")));
                }
                //班组名称和班组负责人
                faultDatum.setTeamName(faultDatum.getFaultApplicantDept());
                //获取填报人组织机构
                Fault one = faultMap.getOrDefault(faultDatum.getCode(), new Fault());

                /*List<String> list = faultCountMapper.getShiftLeader(one.getFaultApplicant(), RoleConstant.FOREMAN);
                String teamUser = list.stream().map(String::valueOf).collect(Collectors.joining(","));
                faultDatum.setTeamUser(teamUser);*/
                //根据站点找工区再找到班组获取工班长
                List<String> list = getForemanByWorkArea(one.getStationCode());
                String teamUser = CollUtil.isNotEmpty(list) ? list.stream().map(String::valueOf).collect(Collectors.joining(",")) : "";
                faultDatum.setTeamUser(teamUser);
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
        boolean b = GlobalThreadLocal.setDataFilter(false);
        //通过真实姓名模糊查询username
        List<String> userNameByRealName = sysBaseApi.getUserNameByRealName(faultCountInfoReq.getAppointUserName());
        GlobalThreadLocal.setDataFilter(b);
        List<FaultCountInfosDTO> faultData = faultCountMapper.getFaultCountInfos(faultCountInfoReq.getType(), page, faultCountInfoReq, null, null,userNameByRealName);
        boolean b1 = GlobalThreadLocal.setDataFilter(false);
        //
        if (CollUtil.isNotEmpty(faultData)) {

            Set<String> faultCodeSet = faultData.stream().map(FaultCountInfosDTO::getCode).collect(Collectors.toSet());
            Map<String, Fault> faultMap = new HashMap<>(16);
            Map<String, List<FaultDevice>> faultDeviceMap = new HashMap<>(16);
            if (CollUtil.isNotEmpty(faultCodeSet)) {
                List<Fault> faultList = faultService.list(new LambdaQueryWrapper<Fault>().in(Fault::getCode, faultCodeSet));

                faultMap = faultList.stream().collect(Collectors.toMap(Fault::getCode, Function.identity()));

                List<FaultDevice> faultDeviceList = faultDeviceService.queryListByFaultCodeList(new ArrayList<>(faultCodeSet));
                faultDeviceMap = faultDeviceList.stream().collect(Collectors.groupingBy(FaultDevice::getFaultCode));
            }


            for (FaultCountInfosDTO faultDatum : faultData) {
                //查找设备编码
                List<FaultDevice> faultDeviceList = faultDeviceMap.get(faultDatum.getCode());
                if (CollUtil.isNotEmpty(faultDeviceList)) {
                    faultDatum.setDeviceCode(faultDeviceList.stream().map(FaultDevice::getDeviceCode).collect(Collectors.joining(",")));
                    faultDatum.setDeviceName(faultDeviceList.stream().map(FaultDevice::getDeviceName).collect(Collectors.joining(",")));
                }
                //班组名称和班组负责人
                faultDatum.setTeamName(faultDatum.getFaultApplicantDept());
                //获取填报人组织机构
                Fault one = faultMap.getOrDefault(faultDatum.getCode(), new Fault());

                //stream 流 过滤 填报人的组织机构 string
                 /*List<String> list = faultCountMapper.getShiftLeader(one.getFaultApplicant(), RoleConstant.FOREMAN);
                String teamUser = list.stream().map(String::valueOf).collect(Collectors.joining(","));
                faultDatum.setTeamUser(teamUser);*/
                //根据站点找工区再找到班组获取工班长
                List<String> list = getForemanByWorkArea(one.getStationCode());
                String teamUser = CollUtil.isNotEmpty(list) ? list.stream().map(String::valueOf).collect(Collectors.joining(",")) : "";
                faultDatum.setTeamUser(teamUser);

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
        boolean b = GlobalThreadLocal.setDataFilter(false);

        List<String> majors = null;
        List<String> stationCodeList = null;

        List<String> userNameByRealName = sysBaseApi.getUserNameByRealName(faultTimeoutLevelReq.getAppointUserName());
        GlobalThreadLocal.setDataFilter(b);
        Date date = new Date();
        List<FaultTimeoutLevelDTO> faultData = faultCountMapper.getFaultData(faultTimeoutLevelReq.getLevel(), page, faultTimeoutLevelReq, majors, stationCodeList, lv1Hours, lv2Hours, lv3Hours, userNameByRealName,date);
        if (CollUtil.isNotEmpty(faultData)) {

            Set<String> faultCodeSet = faultData.stream().map(FaultTimeoutLevelDTO::getCode).collect(Collectors.toSet());
            Map<String, Fault> faultMap = new HashMap<>(16);
            Map<String, List<FaultDevice>> faultDeviceMap = new HashMap<>(16);
            if (CollUtil.isNotEmpty(faultCodeSet)) {
                List<Fault> faultList = faultService.list(new LambdaQueryWrapper<Fault>().in(Fault::getCode, faultCodeSet));
                faultMap = faultList.stream().collect(Collectors.toMap(Fault::getCode, Function.identity()));

                List<FaultDevice> faultDeviceList = faultDeviceService.queryListByFaultCodeList(new ArrayList<>(faultCodeSet));
                faultDeviceMap = faultDeviceList.stream().collect(Collectors.groupingBy(FaultDevice::getFaultCode));
            }

            for (FaultTimeoutLevelDTO faultDatum : faultData) {
                //查找设备编码
                List<FaultDevice> faultDeviceList = faultDeviceMap.get(faultDatum.getDeviceCode());
                if (CollUtil.isNotEmpty(faultDeviceList)) {
                    faultDatum.setDeviceCode(faultDeviceList.stream().map(FaultDevice::getDeviceCode).collect(Collectors.joining(",")));
                    faultDatum.setDeviceName(faultDeviceList.stream().map(FaultDevice::getDeviceName).collect(Collectors.joining(",")));
                }
                //计算超时时长
                long hour=DateUtil.between(faultDatum.getHappenTime(),date, DateUnit.HOUR);
                long min=DateUtil.between(faultDatum.getHappenTime(),date, DateUnit.MINUTE);
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
                    if (hour >= lv1Hours && !FaultStatusEnum.Close.getStatus().equals(faultDatum.getStatus())) {
                        faultDatum.setTimeoutType("一级超时");
                    }
                } else if (faultTimeoutLevelReq.getLevel() == 2) {
                    faultDatum.setTimeoutDuration(time);
                    faultDatum.setAppTimeoutDuration(appTime);
                    if (hour >= lv2Hours && hour < lv1Hours & !FaultStatusEnum.Close.getStatus().equals(faultDatum.getStatus())) {
                        faultDatum.setTimeoutType("二级超时");
                    }
                } else if (faultTimeoutLevelReq.getLevel() == 3) {
                    faultDatum.setTimeoutDuration(time);
                    faultDatum.setAppTimeoutDuration(appTime);
                    if (hour >= lv3Hours && hour < lv2Hours & !FaultStatusEnum.Close.getStatus().equals(faultDatum.getStatus())) {
                        faultDatum.setTimeoutType("三级超时");
                    }
                }
                //班组名称和班组负责人
                faultDatum.setTeamName(faultDatum.getFaultApplicantDept());
                //获取填报人组织机构
                Fault one = faultMap.getOrDefault(faultDatum.getCode(), new Fault());

                 /*List<String> list = faultCountMapper.getShiftLeader(one.getFaultApplicant(), RoleConstant.FOREMAN);
                String teamUser = list.stream().map(String::valueOf).collect(Collectors.joining(","));
                faultDatum.setTeamUser(teamUser);*/
                //根据站点找工区再找到班组获取工班长
                List<String> list = getForemanByWorkArea(one.getStationCode());
                String teamUser = CollUtil.isNotEmpty(list) ? list.stream().map(String::valueOf).collect(Collectors.joining(",")) : "";
                faultDatum.setTeamUser(teamUser);
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

    /**获取设备所在站点对应关联的班组的工班长*/
    private List<String> getForemanByWorkArea(String stationCode) {
        List<String> users = new ArrayList<>();
        List<CsWorkAreaModel> workAreaByCode = sysBaseApi.getWorkAreaByCode(stationCode);
        if (CollUtil.isNotEmpty(workAreaByCode)) {
            for (CsWorkAreaModel csWorkAreaModel : workAreaByCode) {
                List<String> orgCodeList = csWorkAreaModel.getOrgCodeList();
                String realName = sysBaseApi.getRealNameByOrgCodeAndRoleCode(orgCodeList, Collections.singletonList(RoleConstant.FOREMAN));
                users.add(realName);
            }
        }
        return users;
    }
}
