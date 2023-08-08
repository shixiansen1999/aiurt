package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModelProperty;
import liquibase.pro.packaged.B;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author:wgp
 * @create: 2023-08-08 11:58
 * @Description:
 */
@Data
public class ProcessParticipantsInfoDetailsDTO implements Serializable {
    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("键")
    private String key;

    @ApiModelProperty("值")
    private String value;

    @ApiModelProperty("标签")
    private String label;

    @ApiModelProperty("维度下的总人数")
    private Integer userNum;

    @ApiModelProperty("是否部门")
    private Boolean isOrg = new Boolean(false);

    @ApiModelProperty("是否岗位")
    private Boolean isPost = new Boolean(false);

    @ApiModelProperty("是否角色")
    private Boolean isRole = new Boolean(false);

    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("所属部门")
    private String orgName;

    @ApiModelProperty("所属岗位，多个用逗号隔开")
    private String postName;

    @ApiModelProperty("所属角色，多个用逗号隔开")
    private String roleName;

    @ApiModelProperty("用户信息")
    private List<ProcessParticipantsInfoDetailsDTO> children;
}
