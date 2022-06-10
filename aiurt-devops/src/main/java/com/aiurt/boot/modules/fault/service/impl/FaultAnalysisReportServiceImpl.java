package com.aiurt.boot.modules.fault.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.enums.FaultTypeEnum;
import com.aiurt.boot.common.result.FaultAnalysisReportResult;
import com.aiurt.boot.modules.fault.dto.FaultAnalysisReportDTO;
import com.aiurt.boot.modules.fault.entity.AnalysisReportEnclosure;
import com.aiurt.boot.modules.fault.entity.FaultAnalysisReport;
import com.aiurt.boot.modules.fault.mapper.AnalysisReportEnclosureMapper;
import com.aiurt.boot.modules.fault.mapper.FaultAnalysisReportMapper;
import com.aiurt.boot.modules.fault.mapper.FaultMapper;
import com.aiurt.boot.modules.fault.param.FaultAnalysisReportParam;
import com.aiurt.boot.modules.fault.service.IFaultAnalysisReportService;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    private FaultMapper faultMapper;



    /**
     * 查询故障分析报告
     * @param page
     * @param param
     * @return
     */
    @Override
    public IPage<FaultAnalysisReportResult> pageList(IPage<FaultAnalysisReportResult> page, FaultAnalysisReportParam param) {
        IPage<FaultAnalysisReportResult> result = faultAnalysisReportMapper.queryFaultAnalysisReport(page, param);
        List<FaultAnalysisReportResult> records = result.getRecords();
        //故障类型描述
        for (FaultAnalysisReportResult record : records) {
            record.setFaultTypeDesc(FaultTypeEnum.findMessage(record.getFaultType()));
        }
        return result;
    }

    /**
     * 根据code查询故障分析报告
     * @param code
     * @return
     */
    @Override
    public FaultAnalysisReportResult getAnalysisReport(String code) {
        FaultAnalysisReportResult result = faultAnalysisReportMapper.selectAnalysisReport(code);
        if (result!=null) {
            result.setFaultTypeDesc(FaultTypeEnum.findMessage(result.getFaultType()));
            //分析报告附件列表
            List<String> query = analysisReportEnclosureMapper.query(result.getId());
            if (CollUtil.isNotEmpty(query)) {
                result.setUrlList(query);
            }
        }
        return result;
    }

    /**
     * 新增故障分析报告
     * @param dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(FaultAnalysisReportDTO dto, HttpServletRequest req) {

        FaultAnalysisReport one = faultAnalysisReportMapper.selectOne(new LambdaQueryWrapper<FaultAnalysisReport>().eq(FaultAnalysisReport::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(FaultAnalysisReport::getFaultCode, dto.getFaultCode()).last("limit 1"));
        FaultAnalysisReport report = new FaultAnalysisReport();
        report.setFaultCode(dto.getFaultCode());
        report.setFaultAnalysis(dto.getFaultAnalysis());
        report.setSolution(dto.getSolution());
        report.setDelFlag(CommonConstant.DEL_FLAG_0);
        if (one!=null) {
            report.setId(one.getId());
            faultAnalysisReportMapper.updateById(report);
        }else{
            faultAnalysisReportMapper.insert(report);
        }

        //修改故障现象
        if (StringUtils.isNotBlank(dto.getFaultPhenomenon())) {
            faultMapper.updateByFaultCode(dto.getFaultCode(),dto.getFaultPhenomenon());
        }

        //存储故障分析报告附件
        if (CollUtil.isNotEmpty(dto.urlList)) {
            analysisReportEnclosureMapper.delete(new LambdaQueryWrapper<AnalysisReportEnclosure>().eq(AnalysisReportEnclosure::getAnalysisReportId,report.getId()));
            AnalysisReportEnclosure enclosure = new AnalysisReportEnclosure();
            List<String> urlList = dto.urlList;
            for (String s : urlList) {
                enclosure.setAnalysisReportId(report.getId());
                enclosure.setUrl(s);
                enclosure.setDelFlag(CommonConstant.DEL_FLAG_0);
                analysisReportEnclosureMapper.insert(enclosure);
            }
        }
        return Result.ok();
    }
}
