package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/6/23
 * @desc
 */
@Data
public class PatrolTaskUserDTO {
    @Excel(name = "组织机构编号", width = 15)
    @ApiModelProperty(value = "组织机构编号")
    private String orgCode;
    @Excel(name = "组织机构名称", width = 15)
    @ApiModelProperty(value = "组织机构名称")
    private String organizationName;
    @Excel(name = "人员信息", width = 15)
    @ApiModelProperty(value = "人员信息")
    private List<PatrolTaskUserContentDTO> userList;
    @Excel(name = "人员信息", width = 15)
    @ApiModelProperty(value = "人员信息")
    private List<PatrolTaskUserDTO> list;


}
