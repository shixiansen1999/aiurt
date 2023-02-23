package com.aiurt.modules.system.dto;/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2023/2/22
 * @time: 15:41
 */

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2023-02-22 15:41
 */
@Data
public class SysAnnouncementPageDTO {
    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("页码")
    private Integer pageNumber;

    @ApiModelProperty("序号")
    private String seq;

    @ApiModelProperty("条数")
    private Integer dateNumber;


}
