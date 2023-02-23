package com.aiurt.modules.faultexternal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.enums.RepairWayEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.mapper.FaultRepairRecordMapper;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.faultexternal.dto.FaultExternalDTO;
import com.aiurt.modules.faultexternal.entity.FaultExternal;
import com.aiurt.modules.faultexternal.mapper.FaultExternalMapper;
import com.aiurt.modules.faultexternal.service.IFaultExternalService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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


//    LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
    @Override
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
            this.updateById(faultExternal);
        }
    }

}
