package org.jeecg.common.system.vo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author zwl
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CsRoleUserModel implements Serializable {

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "角色编码")
    private String value;

    @ApiModelProperty(value = "角色编码")
    private String key;

    @ApiModelProperty(value = "角色名称")
    private String label;

    @ApiModelProperty(value = "用户组")
    private List<SysUserModel> children;

    @ApiModelProperty(value = "是否是角色标记")
    private Boolean isRole;

    @ApiModelProperty(value = "角色名称")
    private String title;

    @ApiModelProperty(value = "人员数量")
    private Integer userNum;
}
