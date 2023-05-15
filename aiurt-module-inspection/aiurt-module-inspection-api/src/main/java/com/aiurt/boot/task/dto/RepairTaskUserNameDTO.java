package com.aiurt.boot.task.dto;

import lombok.Data;

/**
 * @author:wgp
 * @create: 2023-05-06 08:36
 * @Description: RepairTaskUserNameDTO 类用于存储检修任务的ID和关联的用户名称列表。
 */
@Data
public class RepairTaskUserNameDTO {
    /**
     * 检修任务的ID
     */
    private String id;
    /**
     * 逗号分隔的用户id列表
     */
    private String userIds;
    /**
     * 与检修任务相关联的用户名称列表，以逗号分隔的字符串形式。
     */
    private String userNames;
}
