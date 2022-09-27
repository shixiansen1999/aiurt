package com.aiurt.boot.screen.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author JB
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ScreenDurationTask {

    private String userId;

    private Long duration;
}
