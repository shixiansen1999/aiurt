package com.aiurt.boot.standard.dto;

import lombok.Data;

import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/7
 * @desc
 */
@Data
public class SelectTableDTO {
    private String value;

    private String label;

    private List<SelectTableDTO> children;

    private Integer level;
    /**
     * 针对位置管理: 站所
     */
    private String stationCode;

}
