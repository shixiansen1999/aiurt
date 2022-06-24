package com.aiurt.boot.manager.dto;


import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zwl
 * @Title:
 * @Description:
 * @date 2022/6/2412:15
 */
@Data
public class MajorDTO {

    /**专业编码*/
    @ApiModelProperty(value = "专业编码")
    @TableField(exist = false)
    private String majorCode;


    /**专业名称*/
    @ApiModelProperty(value = "专业名称")
    @TableField(exist = false)
    private String majorName;

    /**子系统列表*/
    @ApiModelProperty(value = "子系统列表")
    @TableField(exist = false)
    private List<SubsystemDTO> subsystemDTOList;

}
