package com.aiurt.boot.modules.fault.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.exception.SwscException;
import com.swsc.copsms.common.result.FaultAnalysisReportResult;
import com.swsc.copsms.common.result.FaultRepairRecordResult;
import com.swsc.copsms.common.result.FaultResult;
import com.swsc.copsms.common.system.util.JwtUtil;
import com.swsc.copsms.common.util.TokenUtils;
import com.swsc.copsms.modules.device.mapper.DeviceMapper;
import com.swsc.copsms.modules.fault.dto.FaultDTO;
import com.swsc.copsms.modules.fault.entity.Fault;
import com.swsc.copsms.modules.fault.entity.FaultEnclosure;
import com.swsc.copsms.modules.fault.mapper.FaultAnalysisReportMapper;
import com.swsc.copsms.modules.fault.mapper.FaultEnclosureMapper;
import com.swsc.copsms.modules.fault.mapper.FaultMapper;
import com.swsc.copsms.modules.fault.mapper.FaultRepairRecordMapper;
import com.swsc.copsms.modules.fault.param.FaultParam;
import com.swsc.copsms.modules.fault.service.IFaultService;
import com.swsc.copsms.modules.patrol.constant.PatrolConstant;
import com.swsc.copsms.modules.patrol.utils.NumberGenerateUtils;
import com.swsc.copsms.modules.system.entity.SysUser;
import com.swsc.copsms.modules.system.mapper.SysUserMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: 故障表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Service
public class FaultServiceImpl extends ServiceImpl<FaultMapper, Fault> implements IFaultService {


    @Resource
    private FaultMapper faultMapper;

    @Resource
    private FaultEnclosureMapper faultEnclosureMapper;

    @Resource
    private NumberGenerateUtils numberGenerateUtils;

    @Resource
    private DeviceMapper deviceMapper;

    @Resource
    private SysUserMapper userMapper;

    @Resource
    private FaultAnalysisReportMapper reportMapper;


    /**
     * 查询故障列表
     * @param page
     * @param queryWrapper
     * @param param
     * @return
     */
    @Override
    public IPage<FaultResult> pageList(IPage<FaultResult> page, Wrapper<FaultResult> queryWrapper, FaultParam param) {
        IPage<FaultResult> faultResults = faultMapper.queryFault(page, queryWrapper,param);
        for (FaultResult record : faultResults.getRecords()) {
            FaultAnalysisReportResult faultAnalysisReportResult = reportMapper.selectLastOne(record.getCode());
            record.setSolution(faultAnalysisReportResult.getSolution());
            record.setFaultReason(faultAnalysisReportResult.getFaultAnalysis());
            String devicesIds = record.getDevicesIds();
            String[] split = devicesIds.split(",");
            List<String> strings = Arrays.asList(split);
            List<String> names = new ArrayList<String>();
            for (String string : strings) {
                names.add(deviceMapper.selectById(string).getName());
            }
            String str = StringUtils.join(names, ",");
            record.setDevice(str);
        }
        return faultResults;
    }

    /**
     * 故障登记
     * @param dto
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public Result add(FaultDTO dto, HttpServletRequest req) {
        Fault fault = new Fault();
        String before = "G" +dto.getLineCode() +dto.getStationCode();
        String codeNo = numberGenerateUtils.getCodeNo(before);
        fault.setCode(codeNo);

        if (dto.getLineCode()==null || "".equals(dto.getLineCode())) {
            throw new SwscException("线路编号不能为空");
        }
        fault.setLineCode(dto.getLineCode());

        if (dto.getStationCode()==null || "".equals(dto.getStationCode())) {
            throw new SwscException("站点编号不能为空");
        }
        fault.setStationCode(dto.getStationCode());

        if (dto.getDevicesIds()!= null) {
            fault.setDevicesIds(dto.getDevicesIds());
        }

        if (dto.getFaultPhenomenon()==null || "".equals(dto.getFaultPhenomenon())) {
            throw new SwscException("故障现象不能为空");
        }
        fault.setRepairWay(dto.getRepairWay());
        if (dto.getRepairWay()==null || "".equals(dto.getRepairWay())) {
            throw new SwscException("报修方式不能为空");
        }
        fault.setFaultPhenomenon(dto.getFaultPhenomenon());

        if (dto.getFaultType() == null) {
            throw new SwscException("故障类型不能为空");
        }
        fault.setFaultType(dto.getFaultType());

        if (dto.getFaultLevel() == null) {
            throw new SwscException("故障级别不能为空");
        }
        fault.setFaultLevel(dto.getFaultLevel());

        if (dto.getLocation() != null) {
            fault.setLocation(dto.getLocation());
        }

        if (dto.getScope() != null) {
            fault.setScope(dto.getScope());
        }

        if (dto.getOccurrenceTime() == null || "".equals(dto.getOccurrenceTime())) {
            throw new SwscException("故障发生时间不能为空");
        }
        fault.setOccurrenceTime(dto.getOccurrenceTime());

        fault.setStatus(0);

        if (dto.getSystemCode()==null || "".equals(dto.getSystemCode())) {
            throw new SwscException("系统编号不能为空");
        }
        fault.setSystemCode(dto.getSystemCode());

        fault.setDelFlag(0);
        fault.setHangState(0);
        fault.setAssignStatus(0);
        // 解密获得username，用于和数据库进行对比
        String token = TokenUtils.getTokenByRequest(req);

        // 解密获得username，用于和数据库进行对比
        String username = JwtUtil.getUsername(token);
        if (username == null) {
            throw new AuthenticationException("token非法无效!");
        }
        // 查询用户信息
        SysUser name = userMapper.getUserByName(username);
        if (name==null){
            throw new AuthenticationException("用户不存在!");
        }
        String id = name.getId();
        fault.setCreateBy(id);
        if (dto.getUpdateBy() != null) {
            fault.setUpdateBy(dto.getUpdateBy());
        }
        fault.setCreateTime(new Date());
        fault.setUpdateTime(new Date());

        faultMapper.insert(fault);

        if (dto.urlList!= null) {
            FaultEnclosure faultEnclosure = new FaultEnclosure();
            List<String> urlList = dto.urlList;
            for (String s : urlList) {
                faultEnclosure.setCreateBy(fault.getCreateBy());
                if (dto.getCreateBy()!=null) {
                    faultEnclosure.setUpdateBy(fault.getUpdateBy());
                }
                faultEnclosure.setCode(fault.getCode());
                faultEnclosure.setUrl(s);
                faultEnclosure.setDelFlag(0);
                faultEnclosure.setCreateTime(new Date());
                faultEnclosure.setUpdateTime(new Date());
                faultEnclosureMapper.insert(faultEnclosure);
            }
        }
        return Result.ok("新增成功");
    }

    /**
     * 根据code查询故障信息
     * @param code
     * @return
     */
    @Override
    public FaultResult getFaultDetail(String code) {
        FaultResult fault = faultMapper.selectDetailByCode(code);
        //故障附件列表
        List<String> query = faultEnclosureMapper.query(code);
        fault.setUrlList(query);
        return fault;
    }

    /**
     * 挂起
     * @param id
     * @return
     */
    @Override
    public Result hang(Integer id,String remark) {
        faultMapper.hang(id,remark);
        return Result.ok();
    }

    /**
     * 取消挂起
     * @param id
     * @return
     */
    @Override
    public Result cancelHang(Integer id) {
        faultMapper.cancel(id);
        return Result.ok();
    }
}
