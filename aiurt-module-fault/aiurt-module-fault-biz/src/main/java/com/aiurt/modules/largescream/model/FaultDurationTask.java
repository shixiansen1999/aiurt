package com.aiurt.modules.largescream.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
/**
 * 任务时长
 *
 * @author: qkx
 * @date: 2022-09-13 14:37
 */

/**
 * @author qkx
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FaultDurationTask {

    private String userId;

    private String taskId;

    private Long duration;
}
