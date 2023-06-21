package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 检修任务需要签名的用户DTO
 * @author 华宜威
 * @date 2023-06-16 16:23:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUserDTO {
    /**用户id*/
    @ApiModelProperty(value = "用户id")
    private String userId;
    /**用户真实名字*/
    @ApiModelProperty(value = "用户真实名字")
    private String realname;
    /**是否是同行人，0否1是。不是同行人代表着是检修人*/
    @ApiModelProperty(value = "是否是同行人，0否1是。不是同行人代表着是检修人")
    private Integer isPeer;
    /**签名的url地址*/
    @ApiModelProperty(value = "签名的url地址")
    private String signUrl;
}