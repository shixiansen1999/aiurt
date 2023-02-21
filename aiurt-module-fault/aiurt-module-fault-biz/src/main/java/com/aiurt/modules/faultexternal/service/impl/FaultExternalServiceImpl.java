package com.aiurt.modules.faultexternal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.enums.ProcessLinkEnum;
import com.aiurt.common.enums.RepairWayEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.OperationProcess;
import com.aiurt.modules.fault.mapper.FaultMapper;
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
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.checkerframework.common.value.qual.StringVal;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

//    @Autowired
//    private IRepairTaskService repairTaskService;

//    @Autowired
//    private ICsStationService csStationService;
    @Autowired
    private FaultMapper faultMapper;
//    @Autowired
//    private FaultEnclosureMapper faultEnclosureMapper;
//    @Autowired
//    private OperationProcessMapper operationProcessMapper;
//    @Autowired
//    private ISysUserService sysUserService;
//    @Autowired
//    private IPatrolTaskReportService patrolTaskReportService;

//    @Autowired
//    private IFaultExternalService faultExternalService;

    @Autowired
    @Lazy
    private IFaultService faultService;

    @Override
    public Result<?> addFaultExternal(FaultExternalDTO dto, HttpServletRequest req) {

        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();


        Fault fault = new Fault();
        //生成故障编号

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
        fault.setFaultModeCode(dto.getFaultModeCode());
        fault.setFaultPhenomenon(dto.getFaultPhenomenon());
//        fault.setFaultType(dto.getFaultType());
        fault.setFaultLevel(dto.getFaultLevel());
        fault.setRepairCode(dto.getRepairCode());
        /*if (StringUtils.isNotBlank(dto.getLocation())) {
            fault.setLocation(dto.getLocation());
        }*/
        if (StringUtils.isNotBlank(dto.getScope())) {
            fault.setScope(dto.getScope());
        }
        fault.setHappenTime(dto.getHappenTime());
        fault.setStatus(0);//??
//        fault.setSystemCode(dto.getSystemCode());
//        fault.setDelFlag(0);
//        fault.setHangState(0);
//        fault.setAssignStatus(0);
//        fault.setSort(0);
//        fault.setDetailLocation(dto.getDetailLocation());
//        fault.setExternalIndocno(dto.getExternalIndocno());

//        Station station =null;

        //添加机构id
//        fault.setOrgId(station.getTeamId());
        fault.setCreateBy(user.getId());
//        faultMapper.insert(fault);
        String code = faultService.add(fault);
        //插入附件表

        // TODO: 2023/2/20 新的接口迁移
        //记录常见故障数量

        //记录运转流程

        //发送app消息

        if (fault.getFaultModeCode().equals("1")) {
//            List<SysUser> userList = null;
//            List<SysUser> userList = sysUserService.list(new LambdaQueryWrapper<SysUser>().eq(SysUser::getOrgId, station.getTeamId()));
//            List<String> userIds = userList.stream().map(SysUser::getId).collect(Collectors.toList());
            // TODO: 2023/2/20 message
        }

            //回调调度系统故障接口
            if (dto.getExternalIndocno() != null) {
//                String code=null;
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
        /*LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        FaultExternal faultExternal = this.getOne(new LambdaQueryWrapper<FaultExternal>()
                .eq(FaultExternal::getIndocno, externalIndocno)
                .orderByDesc(FaultExternal::getId).last("limit 0,1"));
        if (faultExternal != null) {
            faultExternal.setFaultcode(code);
            faultExternal.setStatus(1);
            this.updateById(faultExternal);
            Map param = new HashMap<String,Object>();
            Map<String,Object> data = new HashMap<>();
            data.put("indocno",faultExternal.getIndocno());
            data.put("smfcode",faultExternal.getSmfcode());
            data.put("sexecode",faultExternal.getSexecode());
            data.put("iresult",1);
//            data.put("smethod",faultExternal.getMaintenanceMeasures());
            data.put("icharger",null);
            data.put("sworkno",user.getUsername());
            data.put("scharger",user.getRealname());
            //花费的时间
            Date startTime = faultRecord.getCreateTime();
            Date overTime = dto.getOverTime();
            long start = startTime.getTime();
            long over = overTime.getTime();
            long diff = over-start;
            long nd = 1000*24*60*60;//一天的毫秒数
            long nh = 1000*60*60;//一小时的毫秒数
            long hour = diff%nd/nh;
            data.put("irepairtime",hour);
            data.put("dcompelete",dto.getOverTime());

            param.put("code",200);
            param.put("message","success");
            param.put("data",data);
            param.put("systemid","TXSYS");
            JSONObject json = (JSONObject) JSONObject.toJSON(param);
            String url = "http://10.3.2.2:30300/tpsms/center/std/stdMalfunctionCenter/noGetwayMalfunctionData";
            try {
                HttpURLConnectionUtil.doPost(url,json);
            }catch (Exception e){
                e.printStackTrace();
            }
        }*/
    }

    /*@Override
    public void complete(FaultRepairRecordDTO dto, LoginUser user) {
        //如果是调度推送过来的故障，发送推送数据至调度系统
        //通过faultCode找到对应的fault
        FaultRepairRecord faultRecord = faultRepairRecordMapper.selectById(dto.getId());
        String code = faultRecord.getFaultCode();
        Fault fault = FaultMapper.selectOne(new LambdaQueryWrapper<Fault>().eq(Fault::getCode, code));
        //有indocno的是故障推送过来的故障
        Integer indocno = fault.getExternalIndocno();
        if (indocno!=null){
            FaultExternal faultExternal = this.getOne(new LambdaQueryWrapper<FaultExternal>()
                    .eq(FaultExternal::getIndocno, indocno)
                    .orderByDesc(FaultExternal::getId).last("limit 0,1"));
            if (faultExternal != null) {
                Map param = new HashMap<String,Object>();
                Map<String,Object> data = new HashMap<>();
                data.put("indocno",faultExternal.getIndocno());
                data.put("smfcode",faultExternal.getSmfcode());
                data.put("sexecode",faultExternal.getSexecode());
                data.put("iresult",1);
                data.put("smethod",dto.getMaintenanceMeasures());
                data.put("icharger",null);
                data.put("sworkno",user.getUsername());
                data.put("scharger",user.getRealname());
                //花费的时间
                Date startTime = faultRecord.getCreateTime();
                Date overTime = dto.getOverTime();
                long start = startTime.getTime();
                long over = overTime.getTime();
                long diff = over-start;
                long nd = 1000*24*60*60;//一天的毫秒数
                long nh = 1000*60*60;//一小时的毫秒数
                long hour = diff%nd/nh;
                data.put("irepairtime",hour);
                data.put("dcompelete",dto.getOverTime());

                param.put("code",200);
                param.put("message","success");
                param.put("data",data);
                param.put("systemid","TXSYS");
                JSONObject json = (JSONObject) JSONObject.toJSON(param);
                String url = "http://10.3.2.2:30300/tpsms/center/std/stdMalfunctionCenter/noGetwayMalfunctionData";
                try {
                    HttpURLConnectionUtil.doPost(url,json);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }*/
}
