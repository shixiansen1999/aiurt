package com.aiurt.boot.plan.dto;

import lombok.Data;

import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/2015:06
 */
@Data
public class CodeManageDTO {
    private String code;
    private List<String> list;
    private List<StationDTO> stationDTOS;
}
