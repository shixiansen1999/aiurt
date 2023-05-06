package com.aiurt.boot.task.dto;

import lombok.Data;

/**
 * @author:wgp
 * @create: 2023-05-05 18:14
 * @Description: RepairTaskSampNameDTO 类用于存储检修任务的ID和关联的抽检人员名称列表。
 */
@Data
public class RepairTaskSampNameDTO {
    /**
     * 检修任务的ID
     */
    private String id;
    /**
     * 与检修任务相关联的抽检人员名称列表，以逗号分隔的字符串形式。
     */
    private String sampNames;
}
