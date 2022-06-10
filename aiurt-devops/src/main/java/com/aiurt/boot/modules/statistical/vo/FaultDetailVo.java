package com.aiurt.boot.modules.statistical.vo;


import com.aiurt.boot.common.result.*;
import lombok.Data;

import java.util.List;

@Data
public class FaultDetailVo {

    private FaultResult faultDetail;
    private List<FaultRepairRecordResult> repairRecord;
    private FaultAnalysisReportResult report;
    private List<SpareResult> spareResults;
    private List<OperationProcessResult> process;
}
