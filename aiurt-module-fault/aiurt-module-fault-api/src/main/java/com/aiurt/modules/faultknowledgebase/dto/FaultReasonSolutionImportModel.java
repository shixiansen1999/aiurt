package com.aiurt.modules.faultknowledgebase.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;

@Data
public class FaultReasonSolutionImportModel implements Serializable {
    private static final long serialVersionUID = -6141999253465493661L;

    /**
     * 故障原因
     */
    @Excel(name = "故障原因", width = 15)
    private java.lang.String faultCause;
    /**
     * 解决方案
     */
    @Excel(name = "解决方案", width = 15)
    private java.lang.String solution;
}
