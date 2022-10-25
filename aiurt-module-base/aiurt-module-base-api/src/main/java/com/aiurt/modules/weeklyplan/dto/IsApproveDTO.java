package com.aiurt.modules.weeklyplan.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description: 是否有审批权限
 * @date 2021/5/1411:53
 */
@Data
public class IsApproveDTO {
    @ApiModelProperty("是否进入流程 1是0否")
    private Integer isBegin;
    @ApiModelProperty("是否有审批权限 1有0没有")
    private Integer isApprove;
}
