package com.aiurt.boot.modules.fault.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swsc.copsms.common.exception.SwscException;
import com.swsc.copsms.common.result.FaultAnalysisReportResult;
import com.swsc.copsms.common.system.util.JwtUtil;
import com.swsc.copsms.common.util.TokenUtils;
import com.swsc.copsms.modules.fault.dto.FaultAnalysisReportDTO;
import com.swsc.copsms.modules.fault.entity.AnalysisReportEnclosure;
import com.swsc.copsms.modules.fault.entity.FaultAnalysisReport;
import com.swsc.copsms.modules.fault.mapper.AnalysisReportEnclosureMapper;
import com.swsc.copsms.modules.fault.mapper.FaultAnalysisReportMapper;
import com.swsc.copsms.modules.fault.param.FaultAnalysisReportParam;
import com.swsc.copsms.modules.fault.service.IFaultAnalysisReportService;
import com.swsc.copsms.modules.patrol.constant.PatrolConstant;
import com.swsc.copsms.modules.system.entity.SysUser;
import com.swsc.copsms.modules.system.mapper.SysUserMapper;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * @Description: 故障分析报告
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Service
public class FaultAnalysisReportServiceImpl extends ServiceImpl<FaultAnalysisReportMapper, FaultAnalysisReport> implements IFaultAnalysisReportService {


    @Resource
    private FaultAnalysisReportMapper faultAnalysisReportMapper;

    @Resource
    private AnalysisReportEnclosureMapper analysisReportEnclosureMapper;

    @Resource
    private SysUserMapper userMapper;


    /**
     * 查询故障分析报告
     * @param page
     * @param queryWrapper
     * @param param
     * @return
     */
    @Override
    public IPage<FaultAnalysisReportResult> pageList(IPage<FaultAnalysisReportResult> page, Wrapper<FaultAnalysisReportResult> queryWrapper, FaultAnalysisReportParam param) {
        IPage<FaultAnalysisReportResult> result = faultAnalysisReportMapper.queryFaultAnalysisReport(page, queryWrapper, param);
        return result;
    }

    /**
     * 根据id假删除
     * @param id
     */
    @Override
    public void deleteById(Integer id) {
        faultAnalysisReportMapper.deleteOne(id);
    }

    /**
     * 根据code查询故障分析报告
     * @param code
     * @return
     */
    @Override
    public FaultAnalysisReportResult getAnalysisReport(String code) {
        FaultAnalysisReportResult result = faultAnalysisReportMapper.selectAnalysisReport(code);
        //分析报告附件列表
        List<String> query = analysisReportEnclosureMapper.query(result.getId());
        result.setUrlList(query);
        return result;
    }

    /**
     * 新增故障分析报告
     * @param dto
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void add(FaultAnalysisReportDTO dto, HttpServletRequest req) {
        FaultAnalysisReport report = new FaultAnalysisReport();
        if (dto.getFaultCode() == null || "".equals(dto.getFaultCode())) {
            throw new SwscException("故障编号不能为空");
        }
        report.setFaultCode(dto.getFaultCode());

        if (dto.getFaultAnalysis() == null ||  "".equals(dto.getFaultAnalysis())) {
            throw new SwscException("故障分析不能为空");
        }
        report.setFaultAnalysis(dto.getFaultAnalysis());

        if (dto.getSolution() == null || "".equals(dto.getSolution())) {
            throw new SwscException("解决方案不能为空");
        }
        report.setSolution(dto.getSolution());

        report.setDelFlag(0);
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
        report.setCreateBy(id);
        if (dto.getUpdateBy()!= null) {
            report.setUpdateBy(dto.getUpdateBy());
        }
        report.setCreateTime(new Date());
        report.setUpdateTime(new Date());
        faultAnalysisReportMapper.insert(report);

        //存储故障分析报告附件
        if (dto.urlList!= null) {
            AnalysisReportEnclosure enclosure = new AnalysisReportEnclosure();
            List<String> urlList = dto.urlList;
            for (String s : urlList) {
                enclosure.setAnalysisReportId(report.getId());
                enclosure.setUrl(s);
                enclosure.setDelFlag(0);
                enclosure.setCreateBy(report.getCreateBy());
                if (report.getUpdateBy()!= null) {
                    enclosure.setUpdateBy(report.getUpdateBy());
                }
                enclosure.setCreateTime(new Date());
                enclosure.setUpdateTime(new Date());
                analysisReportEnclosureMapper.insert(enclosure);
            }
        }
    }
}
