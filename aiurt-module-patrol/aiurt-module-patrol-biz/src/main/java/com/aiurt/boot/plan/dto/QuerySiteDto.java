package com.aiurt.boot.plan.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.boot.plan.dto
 * @className: QuerySiteDto
 * @author: life-0
 * @date: 2022/6/27 17:16
 * @description: TODO
 * @version: 1.0
 */
@Data
public class QuerySiteDto {
    @ApiModelProperty(value = "站点名称")
    @TableField(exist = false)
    private java.lang.String siteName;
    @ApiModelProperty(value = "站点Code")
    @TableField(exist = false)
    private String siteCode;
}
