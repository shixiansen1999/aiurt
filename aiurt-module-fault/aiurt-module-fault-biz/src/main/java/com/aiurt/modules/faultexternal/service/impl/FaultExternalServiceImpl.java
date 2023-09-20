package com.aiurt.modules.faultexternal.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.enums.RepairWayEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.fault.constants.FaultConstant;
import com.aiurt.modules.fault.dto.RepairRecordDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultRepairRecord;
import com.aiurt.modules.fault.mapper.FaultRepairRecordMapper;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.faultexternal.dto.FaultExternalDTO;
import com.aiurt.modules.faultexternal.entity.FaultExternal;
import com.aiurt.modules.faultexternal.mapper.FaultExternalMapper;
import com.aiurt.modules.faultexternal.service.IFaultExternalService;
import com.aiurt.modules.position.entity.CsStation;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 调度系统故障
 * @Author: aiurt
 * @Date:   2023-02-16
 * @Version: V1.0
 */
@Slf4j
@Service
public class FaultExternalServiceImpl extends ServiceImpl<FaultExternalMapper, FaultExternal> implements IFaultExternalService {

    @Autowired
    private FaultRepairRecordMapper faultRepairRecordMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @Lazy
    private IFaultService faultService;

    @Autowired
    private FaultRepairRecordMapper recordMapper;

    @Autowired
    private FaultExternalMapper faultExternalMapper;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private ISysParamAPI sysParamApi;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addFaultExternal(FaultExternalDTO dto, HttpServletRequest req) {
        if(ObjectUtil.isNotEmpty(dto.getId())){
            FaultExternal faultExternal = faultExternalMapper.selectOne(new LambdaQueryWrapper<FaultExternal>().eq(FaultExternal::getId, dto.getId()));
            if(ObjectUtil.isNotEmpty(faultExternal)){
                faultExternal.setStopservice(String.valueOf(dto.getIsStopService()==1?1:2));
                faultExternal.setCrane(String.valueOf(dto.getAffectDrive()==1?1:2));
                faultExternal.setTransportservice(String.valueOf(dto.getAffectPassengerService()==1?1:2));
                faultExternalMapper.updateById(faultExternal);
            }
        }
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Fault fault = new Fault();
        fault.setLineCode(dto.getLineCode());
        fault.setStationCode(dto.getStationCode());
        fault.setStationPositionCode(dto.getStationPositionCode());
        if (StringUtils.isNotBlank(dto.getDevicesIds())) {
            fault.setDevicesIds(dto.getDevicesIds());
        }
        if (String.valueOf(RepairWayEnum.BX.getCode()).equals(dto.getFaultModeCode()) && StringUtils.isBlank(dto.getRepairCode())) {
            throw new AiurtBootException("请输入报修编号");
        } else if (String.valueOf(RepairWayEnum.BX.getCode()).equals(dto.getFaultModeCode()) && StringUtils.isNotBlank(dto.getRepairCode())) {
            char[] chars = dto.getRepairCode().toCharArray();
            if (chars.length > CommonConstant.REPAIR_CODE_SIZE) {
                throw new AiurtBootException("报修编号长度不能大于15");
            }
        }
//        fault.setRepairWay(dto.getRepairWay());
        fault.setMajorCode(dto.getMajorCode());
//        fault.setFaultModeCode(dto.getFaultModeCode());
        fault.setFaultPhenomenon(dto.getFaultPhenomenon());
       fault.setFaultTypeCode(dto.getFaultTypeCode());
        fault.setFaultLevel(dto.getFaultLevel());
        //报修人
        fault.setFaultApplicant(dto.getFaultApplicant());
        //紧急程度
        fault.setUrgency(dto.getUrgency());
        //是否委外
        fault.setIsOutsource(dto.getIsOutsource());
        //接报人
        fault.setReceiveUserName(dto.getReceiveUserName());
        //报修组织机构
        fault.setFaultApplicantDept(dto.getFaultApplicantDept());
        //报修方式
        fault.setFaultModeCode(dto.getFaultModeCode());
        //所属子系统
        fault.setSubSystemCode(dto.getSubSystemCode());
        //是否影响行车
        fault.setAffectDrive(dto.getAffectDrive());
        //是否影响客运服务
        fault.setAffectPassengerService(dto.getAffectPassengerService());
        //是否停止服务
        fault.setIsStopService(dto.getIsStopService());
        //抄送人
        fault.setRemindUserName(dto.getRemindUserName());
        //附件
        fault.setPath(dto.getPath());
        fault.setRepairCode(dto.getRepairCode());
        if (StringUtils.isNotBlank(dto.getLocation())) {
            fault.setDetailLocation(dto.getLocation());
        }
        if (StringUtils.isNotBlank(dto.getScope())) {
            fault.setScope(dto.getScope());
        }
        fault.setHappenTime(dto.getHappenTime());
        fault.setStatus(2);
        fault.setFaultDeviceList(dto.getFaultDeviceList());
        fault.setIsFaultExternal(true);
        //故障现象
        fault.setSymptoms(dto.getSymptoms());
//        fault.setSystemCode(dto.getSystemCode());
//        fault.setDelFlag(0);
//        fault.setHangState(0);
//        fault.setAssignStatus(0);
//        fault.setSort(0);
//        fault.setDetailLocation(dto.getDetailLocation());
//        fault.setIndocno(dto.getExternalIndocno());

        fault.setCreateBy(user.getId());
        String code = faultService.add(fault);

            //回调调度系统故障接口
            if (dto.getExternalIndocno() != null) {
            callback(dto.getExternalIndocno(), code);
            }
            return Result.ok("新增成功");
        }

    @Override
    public Page<FaultExternal> selectPage(Page<FaultExternal> page, FaultExternal faultExternal) {
        String stationId = faultExternal.getStationId();
        if (StrUtil.isNotBlank(stationId)) {
            List<String> strings = StrUtil.splitTrim(stationId, ",");
            List<CsStation> stations = sysBaseApi.queryAllStation();
            List<String> stationCode=  stations.stream().filter(e->strings.contains(e.getId())).map(CsStation::getStationCode).collect(Collectors.toList());
            faultExternal.setStationCodes(stationCode);
        }
        List<FaultExternal> faultExternals = baseMapper.selectFaultExternalPage(page, faultExternal);
        if(CollUtil.isNotEmpty(faultExternals)){
            for (FaultExternal external : faultExternals) {
                {
                    external.setAffectPassengerService(0);
                    external.setAffectDrive(0);
                    external.setIsStopService(0);
                    if("1".equals(external.getCrane())){
                        external.setAffectDrive(1);
                    }
                    if("1".equals(external.getStopservice())){
                        external.setIsStopService(1);
                    }
                    if("1".equals(external.getTransportservice())){
                        external.setAffectPassengerService(1); }
                    //已下发故障待指派可以转派
                    external.setCanReassign(external.getFaultStatus() != null && external.getStatus() == 1 && external.getFaultStatus() <= 3);
                }
            }
        }
        page.setRecords(faultExternals);
        return page;
    }

    public void callback(Integer externalIndocno,String code){
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        FaultExternal faultExternal = this.getOne(new LambdaQueryWrapper<FaultExternal>()
                .eq(FaultExternal::getIndocno, externalIndocno)
                .orderByDesc(FaultExternal::getId).last("limit 0,1"));
        if (faultExternal != null) {
            faultExternal.setFaultcode(code);
            faultExternal.setStatus(1);
            faultExternalMapper.updateById(faultExternal);
        }
    }



    @Override
    public void complete(RepairRecordDTO dto,Date endTime, LoginUser user) {
        //如果是调度推送过来的故障，发送推送数据至调度系统
        //通过faultCode找到对应的faultExternal
        SysParamModel systemIdParam = sysParamApi.selectByCode(SysParamCodeConstant.FAULT_EXTERNAL_SYSTEM_ID);
        String systemId = systemIdParam.getValue();
        FaultRepairRecord faultRecord = recordMapper.selectById(dto.getId());
        String code = faultRecord.getFaultCode();
        FaultExternal faultExternal = faultExternalMapper.selectOne(new LambdaQueryWrapper<FaultExternal>().eq(FaultExternal::getFaultcode, code)
                .orderByDesc(FaultExternal::getId).last("limit 0,1"));;
        if (faultExternal != null) {
            Map param = new HashMap<String, Object>();
            Map<String, Object> data = new HashMap<>();
            data.put("indocno", faultExternal.getIndocno());
            data.put("smfcode", faultExternal.getSmfcode());
            data.put("sexecode", faultExternal.getSexecode());
            data.put("iresult", 1);
            SysParamModel paramModel = sysParamApi.selectByCode(SysParamCodeConstant.IS_DISTINGUISH_SIGNAL_FAULT);
            if ("1".equals(paramModel.getValue()) && FaultConstant.IS_SIGNAL_FAULT_1.equals(dto.getIsSignalFault())) {
                //如果是非信号故障，则给生产调度系统返回处理结果=3（:非本故障）
                data.put("iresult", 3);
                log.info(String.valueOf(data));
            }
            data.put("smethod", dto.getMaintenanceMeasures());
            data.put("icharger", null);
            data.put("sworkno", user.getUsername());
            data.put("scharger", user.getRealname());
            //花费的时间
            Date startTime = faultRecord.getCreateTime();
            Date overTime = endTime;
            long start = startTime.getTime();
            long over = overTime.getTime();
            long diff = over - start;
            long nd = 1000 * 24 * 60 * 60;//一天的毫秒数
            long nh = 1000 * 60 * 60;//一小时的毫秒数
            long hour = diff % nd / nh;
            data.put("irepairtime", hour);
            data.put("dcompelete", endTime);

            param.put("code", 200);
            param.put("message", "success");
            param.put("data", data);
            param.put("systemid", systemId);
            try {
                JSONObject json = (JSONObject) JSONObject.toJSON(param);
                 //String url = "http://123.57.62.172:30235/tpsms/center/std/stdMalfunctionCenter/noGetwayMalfunctionData";
                 //String url = "http://mtrain-cc.lucksoft.com.cn/tpsms/center/std/stdMalfunctionCenter/noGetwayMalfunctionData";
                 //String url = "http://10.3.2.2:30300/tpsms/center/std/stdMalfunctionCenter/noGetwayMalfunctionData";
                SysParamModel sysParamModel = sysParamApi.selectByCode(SysParamCodeConstant.FAULT_EXTERNAL_URL);
                String url = sysParamModel.getValue();
               restTemplate.postForObject(url, json, JSONObject.class);
                log.info("故障推送,请求结果",url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public Result<FaultExternal> appendFault(JSONObject formData) {
        try {
            JSONObject dataJson = formData.getJSONObject("data");
            FaultExternal faultExternal = JSON.toJavaObject(dataJson, FaultExternal.class);
            if (faultExternal.getUrlList() != null && faultExternal.getUrlList().size() > 0) {
                String urls = String.join(",", faultExternal.getUrlList());
                faultExternal.setUrls(urls);
            }
            FaultExternal external = this.getOne(new LambdaQueryWrapper<FaultExternal>().eq(FaultExternal::getIndocno, faultExternal.getIndocno()));
            if (external == null) {
                //信号只要1,2号线的调度故障,如果不是12号线直接返回
                SysParamModel paramModel = sysParamApi.selectByCode(SysParamCodeConstant.FAULT_EXTERNAL_XH12);
                boolean value = "1".equals(paramModel.getValue());
                Integer lineCode = faultExternalMapper.getLineCode(faultExternal.getIline());
                if (value && lineCode == 0) {
                    return  Result.OK("添加成功！");
                }
                faultExternal.setStatus(0);
                faultExternalMapper.insert(faultExternal);
                sysBaseApi.sendAllMessage();

            } else {
                faultExternalMapper.update(faultExternal, new LambdaQueryWrapper<FaultExternal>().eq(FaultExternal::getIndocno, faultExternal.getIndocno()));
            }
            return  Result.OK("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return  Result.error("添加失败");
        }
    }

    @Override
    public void reassign(FaultExternalDTO dto, HttpServletRequest req) {
        if (StrUtil.isBlank(dto.getFaultcode())) {
            throw new AiurtBootException("故障未下发");
        }
        Fault fault = faultService.getOne(new LambdaQueryWrapper<Fault>().eq(Fault::getCode, dto.getFaultcode()), false);
        if (ObjectUtil.isEmpty(fault)) {
            throw new AiurtBootException("未找到该故障单：" + dto.getFaultcode());
        }
        fault.setLineCode(dto.getLineCode());
        fault.setStationCode(dto.getStationCode());
        fault.setStationPositionCode(dto.getStationPositionCode());
        if (StringUtils.isNotBlank(dto.getDevicesIds())) {
            fault.setDevicesIds(dto.getDevicesIds());
        }
        fault.setMajorCode(dto.getMajorCode());
        fault.setFaultPhenomenon(dto.getFaultPhenomenon());
        fault.setFaultTypeCode(dto.getFaultTypeCode());
        fault.setFaultLevel(dto.getFaultLevel());
        //报修人
        fault.setFaultApplicant(dto.getFaultApplicant());
        //紧急程度
        fault.setUrgency(dto.getUrgency());
        //是否委外
        fault.setIsOutsource(dto.getIsOutsource());
        //接报人
        fault.setReceiveUserName(dto.getReceiveUserName());
        //报修组织机构
        fault.setFaultApplicantDept(dto.getFaultApplicantDept());
        //报修方式
        fault.setFaultModeCode(dto.getFaultModeCode());
        //所属子系统
        fault.setSubSystemCode(dto.getSubSystemCode());
        //是否影响行车
        fault.setAffectDrive(dto.getAffectDrive());
        //是否影响客运服务
        fault.setAffectPassengerService(dto.getAffectPassengerService());
        //是否停止服务
        fault.setIsStopService(dto.getIsStopService());
        //抄送人
        fault.setRemindUserName(dto.getRemindUserName());
        //附件
        fault.setPath(dto.getPath());
        if (StringUtils.isNotBlank(dto.getLocation())) {
            fault.setDetailLocation(dto.getLocation());
        }
        if (StringUtils.isNotBlank(dto.getScope())) {
            fault.setScope(dto.getScope());
        }
        fault.setHappenTime(dto.getHappenTime());
        fault.setFaultDeviceList(dto.getFaultDeviceList());
        fault.setIsFaultExternal(true);
        //故障现象
        fault.setSymptoms(dto.getSymptoms());
        faultService.updateById(fault);
    }
}
