package com.aiurt.modules.fault.dto;

import lombok.Data;

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
public class UserTimeDTO {

    private String userId;

    private String frrId;

    private Long duration;
}
