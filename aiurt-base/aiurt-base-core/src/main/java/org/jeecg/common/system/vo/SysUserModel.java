package org.jeecg.common.system.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 此类用作一个树的模型，目前有使用的有岗位树、角色树
 * @author zwl
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SysUserModel implements Serializable {

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "key")
    private String key;

    @ApiModelProperty(value = "value")
    private String value;

    @ApiModelProperty(value = "用户名")
    private String label;

    @ApiModelProperty(value = "用户名")
    private String title;

    @ApiModelProperty(value = "是否是角色标记")
    private Boolean isRole;

    @ApiModelProperty(value = "是否是部门标记")
    private Boolean isOrg;

    @ApiModelProperty(value = "是否是岗位标记")
    private Boolean isPost;

    @ApiModelProperty(value = "用户角色，多个使用英文逗号分隔")
    private String roleName;

    @ApiModelProperty(value = "岗位名称，多个使用英文逗号分隔")
    private String postName;

    @ApiModelProperty(value = "所属部门编码")
    private String orgCode;

    @ApiModelProperty(value = "所属部门名称")
    private String orgName;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "用户组")
    private List<SysUserModel> children;

    @ApiModelProperty(value = "人员数量")
    private Long userNum;

    @ApiModelProperty(value = "是否可选")
    private Boolean isSelect = Boolean.TRUE;
}
