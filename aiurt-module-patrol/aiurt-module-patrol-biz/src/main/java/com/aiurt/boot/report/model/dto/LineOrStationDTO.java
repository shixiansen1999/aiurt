package com.aiurt.boot.report.model.dto;

import lombok.Data;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.boot.report.model.dto
 * @className: LineDTO
 * @author: life-0
 * @date: 2022/9/23 17:08
 * @description: TODO
 * @version: 1.0
 */
@Data
public class LineOrStationDTO {
    private String id;
    private String name;
    private String code;
    private String orgCategory;
}
