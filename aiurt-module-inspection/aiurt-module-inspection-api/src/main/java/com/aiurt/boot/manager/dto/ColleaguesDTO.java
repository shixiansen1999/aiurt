package com.aiurt.boot.manager.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zwl
 * @Title:
 * @Description:
 */
@Data
public class ColleaguesDTO {

    /**同行人id*/
    @ApiModelProperty(value = "同行人id")
    @TableField(exist = false)
    private String realId;

    /**同行人名称*/
    @ApiModelProperty(value = "同行人名称")
    @TableField(exist = false)
    private String realName;

    /**抽检人id*/
    @ApiModelProperty(value = "抽检人id")
    @TableField(exist = false)
    private String samplingId;

    /**抽检人名称*/
    @ApiModelProperty(value = "抽检人名称")
    @TableField(exist = false)
    private String samplingName;
}
