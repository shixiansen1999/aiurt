package com.aiurt.boot.modules.fault.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: swsc
 * 故障分析报告参数列表
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Validated
public class FaultAnalysisReportParam {

    /**
     * 故障编号
     */
    private String code;

    /**
     *站点编号
     */
    private String stationCode;

    /**
     * 开始时间
     */
    private String dayStart;

    /**
     * 结束时间
     */
    private String dayEnd;

    /**
     * 故障现象
     */
    private String faultPhenomenon;
}
