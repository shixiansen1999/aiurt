package com.aiurt.config.thirdapp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 第三方App对接
 * @author: jeecg-boot
 */
@Data
@ApiModel("第三方App配置")
public class ThirdAppTypeItemVo {

    /**
     * 是否启用
     */
    private boolean enabled;
    /**
     * 应用Key
     */
    @ApiModelProperty(value = "企业id")
    private String clientId;
    /**
     * 应用Secret
     */
    private String clientSecret;
    /**
     * 应用ID
     */
    @ApiModelProperty(value = "应用id")
    private String agentId;
    /**
     * 目前仅企业微信用到：自建应用Secret
     */
    private String agentAppSecret;

    public int getAgentIdInt() {
        return Integer.parseInt(agentId);
    }

}
