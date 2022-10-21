package com.aiurt.modules.largescream.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
