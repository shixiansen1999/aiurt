package com.aiurt.boot.manager.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zwl
 * @Title:
 * @Description:
 * @date 2022/6/2817:16
 */
@Data
public class ExamineDTO {

    /**检修任务id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修任务id",required = true)
    @NotBlank
    private String id;

    /**是否通过*/
    @TableField(exist = false)
    @ApiModelProperty(value = "是否通过 未通过0,通过1",required = true)
    @NotNull
    private Integer status;

    /**备注*/
    @TableField(exist = false)
    @ApiModelProperty(value = "备注")
    private String content;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件")
    private String path;

    /**提交签名*/
    @TableField(exist = false)
    @ApiModelProperty(value = "提交签名")
    private String confirmUrl;
    /**验收标记 验收0，审核1*/
    private Integer acceptanceRemark;

    /**检修任务状态*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修任务状态")
    private Integer inspectionStatus;
}
