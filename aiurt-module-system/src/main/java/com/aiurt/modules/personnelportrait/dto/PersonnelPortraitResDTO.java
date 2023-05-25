package com.aiurt.modules.personnelportrait.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author
 * @description
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "人员画像Model对象", description = "人员画像Model对象")
public class PersonnelPortraitResDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;
    /**
     * 班组组长
     */
    @ApiModelProperty(value = "班组组长")
    private String leader;

    /**
     * 站点个数
     */
    @ApiModelProperty(value = "站点个数")
    private Integer number;

    /**
     * 工区位置
     */
    @ApiModelProperty(value = "工区位置")
    private String position;

    /**
     * 班组负责线路
     */
    @ApiModelProperty(value = "班组负责线路")
    private String line;

    /**
     * 用户信息
     */
    @ApiModelProperty(value = "用户信息")
    private List<UserInfoResDTO> userInfos;
}
