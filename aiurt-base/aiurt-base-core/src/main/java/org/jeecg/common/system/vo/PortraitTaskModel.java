package org.jeecg.common.system.vo;

import lombok.Data;

/**
 * @author
 * @description 人员画像任务数量统计DTO
 */
@Data
public class PortraitTaskModel {
    /**
     * 年份
     */
    private Integer year;
    /**
     * 任务数
     */
    private Long number;
}
