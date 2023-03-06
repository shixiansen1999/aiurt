package com.aiurt.modules.faultexternal.service.impl;

import cn.hutool.core.util.StrUtil;

import cn.hutool.core.date.DateUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.enums.ProcessLinkEnum;
import com.aiurt.common.enums.RepairWayEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.fault.dto.RepairRecordDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.OperationProcess;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.fault.entity.FaultRepairRecord;
import com.aiurt.modules.fault.entity.OperationProcess;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.fault.mapper.FaultRepairRecordMapper;
import com.aiurt.modules.fault.mapper.OperationProcessMapper;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.faultenclosure.entity.FaultEnclosure;
import com.aiurt.modules.faultenclosure.mapper.FaultEnclosureMapper;
import com.aiurt.modules.faultexternal.dto.FaultExternalDTO;
import com.aiurt.modules.faultexternal.entity.FalutExternalReceiveDTO;
import com.aiurt.modules.faultexternal.entity.FaultExternal;
import com.aiurt.modules.faultexternal.mapper.FaultExternalMapper;
import com.aiurt.modules.faultexternal.service.IFaultExternalService;
import com.aiurt.modules.worklog.entity.Station;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.checkerframework.common.value.qual.StringVal;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
//import javax.transaction.Transactional;
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


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addFaultExternal(FaultExternalDTO dto, HttpServletRequest req) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Fault fault = new Fault();
        fault.setLineCode(dto.getLineCode());
        fault.setStationCode(dto.getStationCode());
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
//        fault.setFaultType(dto.getFaultType());
        fault.setFaultLevel(dto.getFaultLevel());
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
        fault.setRepairCode(dto.getRepairCode());
        if (StringUtils.isNotBlank(dto.getLocation())) {
            fault.setDetailLocation(dto.getLocation());
        }
        if (StringUtils.isNotBlank(dto.getScope())) {
            fault.setScope(dto.getScope());
        }
        fault.setHappenTime(dto.getHappenTime());
        fault.setStatus(0);
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
            faultExternal.setStationIds(strings);
        }
        List<FaultExternal> faultExternals = baseMapper.selectFaultExternalPage(page, faultExternal);
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
    public void complete(RepairRecordDTO dto, LoginUser user) {
        //如果是调度推送过来的故障，发送推送数据至调度系统
        //通过faultCode找到对应的faultExternal
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
            data.put("smethod", dto.getMaintenanceMeasures());
            data.put("icharger", null);
            data.put("sworkno", user.getUsername());
            data.put("scharger", user.getRealname());
            //花费的时间
            Date startTime = faultRecord.getCreateTime();
            Date overTime = faultRecord.getEndTime();
            long start = startTime.getTime();
            long over = overTime.getTime();
            long diff = over - start;
            long nd = 1000 * 24 * 60 * 60;//一天的毫秒数
            long nh = 1000 * 60 * 60;//一小时的毫秒数
            long hour = diff % nd / nh;
            data.put("irepairtime", hour);
            data.put("dcompelete", faultRecord.getEndTime());

            param.put("code", 200);
            param.put("message", "success");
            param.put("data", data);
            param.put("systemid", "TXSYS");
            try {
                JSONObject json = (JSONObject) JSONObject.toJSON(param);
                String url = "http://123.57.62.172:30235/tpsms/center/std/stdMalfunctionCenter/noGetwayMalfunctionData";
                restTemplate.postForObject(url, json, JSONObject.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
