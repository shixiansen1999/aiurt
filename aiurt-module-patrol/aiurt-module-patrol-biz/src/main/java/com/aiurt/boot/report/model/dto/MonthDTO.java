package com.aiurt.boot.report.model.dto;

import lombok.Data;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.boot.report.model.dto
 * @className: MonthDTO
 * @author: life-0
 * @date: 2022/9/22 9:27
 * @description: TODO
 * @version: 1.0
 */
@Data
public class MonthDTO {
    private Integer nums;
    private String orgName;
    private String orgCode;
    private String systemName;
    private String shortenedForm;
    private String systemCode;
    private String approvalPassTime;


}
