package com.aiurt.boot.manager.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.system.vo.LoginUser;

import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description: 部门以及部门下的人员信息
 * @date 2022/7/712:12
 */
@Data
public class OrgDTO {
    @ApiModelProperty(value = "机构编码")
    private String orgCode;
    @ApiModelProperty(value = "机构名称")
    private String departName;
    @ApiModelProperty(value = "机构下的人员")
    private List<LoginUser> users;
}
