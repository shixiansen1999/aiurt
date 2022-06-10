package com.aiurt.boot.modules.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * The preson who loves you has gone night and night,
 * walking on the way.
 *
 * @purpose: UserTreeVO
 * @data: 2021/11/20 20:31
 * @author: Mr. zhao
 **/
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "树形部门人员列表")
public class UserTreeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "是否为人员, 1:人员 0:班组")
    private Integer userFlag;

    @ApiModelProperty(value = "下级")
    private List<UserChildrenVO> children;
}
