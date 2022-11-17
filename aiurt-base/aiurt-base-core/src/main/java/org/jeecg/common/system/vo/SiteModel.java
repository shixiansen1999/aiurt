package org.jeecg.common.system.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/914:51
 */
@Data
public class SiteModel {
    @ApiModelProperty("工区id")
    private String siteId;
    @ApiModelProperty("工区名称")
    private String siteName;
    @ApiModelProperty("工区位置")
    private String position;
    @ApiModelProperty("工区负责人")
    private String realName;
}
