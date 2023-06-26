package com.aiurt.boot.report.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author admin
 */
@Data
public class SystemMonthDTO {
    private String orgName;
    private String shortenedForm;
    private List<MonthDTO> monthDTOList;
}
