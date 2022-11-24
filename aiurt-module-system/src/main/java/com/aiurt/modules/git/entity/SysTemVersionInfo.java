package com.aiurt.modules.git.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("系统版本信息")
public class SysTemVersionInfo implements Serializable {

    private static final long serialVersionUID = 1263468185259566167L;

    /**
     * 提交记录
     */
    @ApiModelProperty(value = "提交记录")
    private String gitCommitId;

    /**
     * 构建时间
     */
    @ApiModelProperty(value = "构建时间")
    private String buildTime;

    /**
     * 项目版本号
     */
    @ApiModelProperty(value = "项目版本号")
    private String projectVersion;
}
