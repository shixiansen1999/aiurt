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
public class PostModel implements Serializable {

    @ApiModelProperty(value = "主键")
    private String id;


    @ApiModelProperty(value = "岗位名称")
    private String label;

    @ApiModelProperty(value = "标记")
    private Boolean isOrg;

    @ApiModelProperty(value = "用户组")
    private List<SysUserModel> children;
}
