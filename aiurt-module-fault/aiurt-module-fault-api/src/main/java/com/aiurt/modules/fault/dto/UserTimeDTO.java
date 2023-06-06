package com.aiurt.modules.fault.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.modules.fault.dto
 * @className: UserTimeDTO
 * @author: life-0
 * @date: 2022/10/12 9:08
 * @description: TODO
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTimeDTO {

    private String userId;

    private String frrId;

    private Long duration;
}
