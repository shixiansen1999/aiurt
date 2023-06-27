package com.aiurt.boot.report.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author admin
 */
@Data
public class SystemMonthDTO {
    private String orgName;
    private String orgCode;
    private String shortenedForm;
    private String systemCode;
    private List<MonthDTO> monthDTOList;
}
