package com.aiurt.boot.standard.dto;

import com.aiurt.boot.standard.entity.InspectionCode;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/6/3012:25
 */
@Data
public class InspectionCodeDTO extends InspectionCode {
    @TableField(exist = false)
    @ApiModelProperty("检修策略编码")
    private String strCode;
}
