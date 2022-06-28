package com.aiurt.boot.manager.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
    @ApiModelProperty(value = "检修任务id")
    private String id;

    /**是否通过*/
    @TableField(exist = false)
    @ApiModelProperty(value = "是否通过 未通过0,通过1")
    private Integer status;

    /**备注*/
    @TableField(exist = false)
    @ApiModelProperty(value = "备注")
    private String content;
}
