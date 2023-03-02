package com.aiurt.modules.faultproducereport.dto;

import com.aiurt.modules.faultproducereport.entity.FaultProduceReport;
import com.aiurt.modules.faultproducereportline.entity.FaultProduceReportLine;
import com.aiurt.modules.faultproducereportlinedetail.dto.FaultProduceReportLineDetailDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class FaultProduceReportDTO extends FaultProduceReport {
    /**传入的reportId列表*/
    private List<String> selections;
    /**查询统计日期：开始时间*/
    private String beginDay;
    /**查询统计日期：结束时间*/
    private String endDay;
    /**专业名称*/
    private String majorName;
    /**提交人的真实名称*/
    private String submitUserRealname;
    /**一条生产日报下的所有线路故障数据*/
    private List<FaultProduceReportLine> reportLineList;
    /**一条生产日报下的所有故障清单数据*/
    private List<FaultProduceReportLineDetailDTO> reportLineDetailDTOList;
    private Boolean isOpen;
}
