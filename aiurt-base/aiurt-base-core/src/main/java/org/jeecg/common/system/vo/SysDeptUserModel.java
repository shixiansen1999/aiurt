package org.jeecg.common.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author
 * @date 2022/12/2 16:00
 * @description: 组织机构用户联动对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "组织机构用户联动对象", description = "组织机构用户联动对象")
public class SysDeptUserModel {
    /**
     * 组织机构编码
     */
    @ApiModelProperty("组织机构编码")
    private String orgCode;
    /**
     * 组织机构名称
     */
    @ApiModelProperty("组织机构名称")
    private String orgName;
    /**
     * 用户信息
     */
    @ApiModelProperty("用户信息")
    List<LoginUser> userList;
}
